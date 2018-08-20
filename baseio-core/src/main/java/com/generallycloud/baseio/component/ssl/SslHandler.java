/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.baseio.component.ssl;

import static com.generallycloud.baseio.component.ssl.SslContext.SSL_PACKET_BUFFER_SIZE;

import java.io.IOException;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

import com.generallycloud.baseio.buffer.ByteBuf;
import com.generallycloud.baseio.buffer.ByteBufAllocator;
import com.generallycloud.baseio.buffer.EmptyByteBuf;
import com.generallycloud.baseio.buffer.UnpooledByteBufAllocator;
import com.generallycloud.baseio.common.ReleaseUtil;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.component.ProtectedUtil;

public class SslHandler {

    private final ByteBuf dstTemp;

    public SslHandler() {
        ByteBufAllocator allocator = UnpooledByteBufAllocator.getDirect();
        this.dstTemp = allocator.allocate(SSL_PACKET_BUFFER_SIZE);
    }

    //FIXME not correct ,fix this
    private int guessWrapOut(int src, int ext) {
        if (SslContext.isOpenSslAvailable()) {
            return ((src + SSL_PACKET_BUFFER_SIZE - 1) / SSL_PACKET_BUFFER_SIZE + 1) * ext + src;
        } else {
            return ((src + SSL_PACKET_BUFFER_SIZE - 1) / SSL_PACKET_BUFFER_SIZE) * (ext + SSL_PACKET_BUFFER_SIZE);
        }
    }

    public ByteBuf wrap(NioSocketChannel ch, ByteBuf src) throws IOException {
        SSLEngine engine = ch.getSSLEngine();
        ByteBufAllocator allocator = ch.alloc();
        ByteBuf out = null;
        try {
            if (ProtectedUtil.isSslHandshakeFinished(ch)) {
                byte sslWrapExt = ProtectedUtil.getSslWrapExt(ch);
                if (sslWrapExt == 0) {
                    out = allocator.allocate(guessWrapOut(src.limit(), 0xff + 1));
                } else {
                    out = allocator.allocate(guessWrapOut(src.limit(), sslWrapExt & 0xff));
                }
                for (;;) {
                    SSLEngineResult result = engine.wrap(src.nioBuffer(), out.nioBuffer());
                    Status status = result.getStatus();
                    HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                    synchByteBuf(result, src, out);
                    if (status == Status.CLOSED) {
                        return out.flip();
                    } else if (status == Status.BUFFER_OVERFLOW) {
                        out.reallocate(out.capacity() + SSL_PACKET_BUFFER_SIZE, true);
                        continue;
                    }
                    if (handshakeStatus == HandshakeStatus.NOT_HANDSHAKING) {
                        if (src.hasRemaining()) {
                            continue;
                        }
                        if (sslWrapExt == 0) {
                            int srcLen = src.limit();
                            int outLen = out.position();
                            int y = ((srcLen + 1) / SSL_PACKET_BUFFER_SIZE) + 1;
                            int u = ((outLen - srcLen) / y) * 2;
                            ProtectedUtil.setSslWrapExt(ch, (byte) u);
                        }
                        return out.flip();
                    }
                }
            } else {
                ByteBuf dst = dstTemp;
                for (;;) {
                    dst.clear();
                    SSLEngineResult result = engine.wrap(src.nioBuffer(), dst.nioBuffer());
                    Status status = result.getStatus();
                    HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                    synchByteBuf(result, src, dst);
                    if (status == Status.CLOSED) {
                        return swap(allocator, dst.flip());
                    }
                    if (handshakeStatus == HandshakeStatus.NEED_UNWRAP) {
                        if (out != null) {
                            out.read(dst.flip());
                            return out.flip();
                        }
                        return swap(allocator, dst.flip());
                    } else if (handshakeStatus == HandshakeStatus.NEED_WRAP) {
                        if (out == null) {
                            out = allocator.allocate(256);
                        }
                        out.read(dst.flip());
                        continue;
                    } else if (handshakeStatus == HandshakeStatus.FINISHED) {
                        ProtectedUtil.finishHandshake(ch);
                        if (out != null) {
                            out.read(dst.flip());
                            return out.flip();
                        }
                        return swap(allocator, dst.flip());
                    } else if (handshakeStatus == HandshakeStatus.NEED_TASK) {
                        runDelegatedTasks(engine);
                        continue;
                    }
                }
            }
        } catch (Throwable e) {
            ReleaseUtil.release(out);
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException(e);
        }
    }

    //FIXME 部分buf不需要gc
    private ByteBuf swap(ByteBufAllocator allocator, ByteBuf buf) throws IOException {
        ByteBuf out = allocator.allocate(buf.limit());
        try {
            out.read(buf);
        } catch (Exception e) {
            out.release();
            throw e;
        }
        return out.flip();
    }

    public ByteBuf unwrap(NioSocketChannel ch, ByteBuf src) throws IOException {
        SSLEngine sslEngine = ch.getSSLEngine();
        ByteBuf dst = dstTemp;
        if (ProtectedUtil.isSslHandshakeFinished(ch)) {
            dst.clear();
            ProtectedUtil.readPlainRemainingBuf(ch, dst);
            SSLEngineResult result = sslEngine.unwrap(src.nioBuffer(), dst.nioBuffer());
            synchByteBuf(result, src, dst);
            return dst.flip();
        } else {
            for (;;) {
                dst.clear();
                SSLEngineResult result = sslEngine.unwrap(src.nioBuffer(), dst.nioBuffer());
                HandshakeStatus handshakeStatus = result.getHandshakeStatus();
                synchByteBuf(result, src, dst);
                if (handshakeStatus == HandshakeStatus.NEED_WRAP) {
                    ch.flush(EmptyByteBuf.get());
                    return null;
                } else if (handshakeStatus == HandshakeStatus.NEED_TASK) {
                    runDelegatedTasks(sslEngine);
                    continue;
                } else if (handshakeStatus == HandshakeStatus.FINISHED) {
                    ProtectedUtil.finishHandshake(ch);
                    return null;
                } else if (handshakeStatus == HandshakeStatus.NEED_UNWRAP) {
                    if (src.hasRemaining()) {
                        continue;
                    }
                    return null;
                }
            }
        }
    }

    private void synchByteBuf(SSLEngineResult result, ByteBuf src, ByteBuf dst) {
        //FIXME 同步。。。。。
        src.reverse();
        dst.reverse();
        //		int bytesConsumed = result.bytesConsumed();
        //		int bytesProduced = result.bytesProduced();
        //		
        //		if (bytesConsumed > 0) {
        //			src.skipBytes(bytesConsumed);
        //		}
        //
        //		if (bytesProduced > 0) {
        //			dst.skipBytes(bytesProduced);
        //		}
    }

    private void runDelegatedTasks(SSLEngine engine) {
        for (;;) {
            Runnable task = engine.getDelegatedTask();
            if (task == null) {
                break;
            }
            task.run();
        }
    }

}
