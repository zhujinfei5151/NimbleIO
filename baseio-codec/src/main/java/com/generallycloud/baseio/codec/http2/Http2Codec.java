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
package com.generallycloud.baseio.codec.http2;

import java.io.IOException;

import com.generallycloud.baseio.buffer.ByteBuf;
import com.generallycloud.baseio.codec.http2.hpack.DefaultHttp2HeadersEncoder;
import com.generallycloud.baseio.codec.http2.hpack.Http2HeadersEncoder;
import com.generallycloud.baseio.common.MathUtil;
import com.generallycloud.baseio.common.ThrowableUtil;
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.protocol.Frame;
import com.generallycloud.baseio.protocol.ProtocolCodec;

/**
 * <pre>
 * +-----------------------------------------------+
 * |                 Length (24)                   |
 * +---------------+---------------+---------------+
 * |   Type (8)    |   Flags (8)   |
 * +-+-------------+---------------+-------------------------------+
 * |R|                 Stream Identifier (31)                      |
 * +=+=============================================================+
 * |                   Frame Payload (0...)                      ...
 * +---------------------------------------------------------------+
 * </pre>
 * <dl>
 * <dt>Length:</dt>
 * <dd>
 * <p>
 * The length of the frame payload expressed as an unsigned 24-bit integer.
 * Values greater than 2<sup>14</sup> (16,384) MUST NOT be sent unless the
 * receiver has set a larger value for <a href="#SETTINGS_MAX_FRAME_SIZE"
 * class="smpl">SETTINGS_MAX_FRAME_SIZE</a>.
 * </p>
 * <p>
 * The 9 octets of the frame header are not included in this value.
 * </p>
 * </dd>
 * <dt>Type:</dt>
 * <dd>
 * <p>
 * The 8-bit type of the frame. The frame type determines the format and
 * semantics of the frame. Implementations MUST ignore and discard any frame
 * that has a type that is unknown.
 * </p>
 * </dd>
 * <dt>Flags:</dt>
 * <dd>
 * <p>
 * An 8-bit field reserved for boolean flags specific to the frame type.
 * </p>
 * <p>
 * Flags are assigned semantics specific to the indicated frame type. Flags that
 * have no defined semantics for a particular frame type MUST be ignored and
 * MUST be left unset (0x0) when sending.
 * </p>
 * </dd>
 * <dt>R:</dt>
 * <dd>
 * <p>
 * A reserved 1-bit field. The semantics of this bit are undefined, and the bit
 * MUST remain unset (0x0) when sending and MUST be ignored when receiving.
 * </p>
 * </dd>
 * <dt>Stream Identifier:</dt>
 * <dd>
 * <p>
 * A stream identifier (see <a href="#StreamIdentifiers"
 * title="Stream Identifiers">Section&nbsp;5.1.1</a>) expressed as an unsigned
 * 31-bit integer. The value 0x0 is reserved for frames that are associated with
 * the connection as a whole as opposed to an individual stream.
 * </p>
 * </dd>
 * </dl>
 * 
 */
//http://httpwg.org/specs/rfc7540.html
//https://blog.csdn.net/u010129119/article/details/79361949
public class Http2Codec extends ProtocolCodec {

    private static final IOException NOT_HTTP2_PROTOCL       = ThrowableUtil
            .unknownStackTrace(new IOException("preface not matched"), Http2Codec.class, "codec");

    public static final int          PROTOCOL_HEADER         = 9;
    public static final int          PROTOCOL_PING           = -1;
    public static final int          PROTOCOL_PONG           = -2;
    public static final int          PROTOCOL_PREFACE_HEADER = 24;
    private Http2HeadersEncoder      http2HeadersEncoder     = new DefaultHttp2HeadersEncoder();

    private static byte[]            PREFACE_BINARY          = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n"
            .getBytes();

    private Http2FrameHeader genFrame(Http2FrameType type, int length, int streamIdentifier,
            byte flags) {
        switch (type) {
            case FRAME_TYPE_CONTINUATION:
                break;
            case FRAME_TYPE_DATA:
                break;
            case FRAME_TYPE_GOAWAY:
                break;
            case FRAME_TYPE_HEADERS:
                return new Http2HeadersFrame();
            case FRAME_TYPE_PING:
                break;
            case FRAME_TYPE_PRIORITY:
                break;
            case FRAME_TYPE_PUSH_PROMISE:
                break;
            case FRAME_TYPE_RST_STREAM:
                break;
            case FRAME_TYPE_SETTINGS:
                return new Http2SettingsFrame();
            case FRAME_TYPE_WINDOW_UPDATE:
                return new Http2WindowUpdateFrame();
            default:
                break;
        }
        throw new IllegalArgumentException(type.toString());
    }

    @Override
    public Frame decode(NioSocketChannel ch, ByteBuf src) throws IOException {
        Http2Session session = Http2Session.getHttp2Session(ch);
        if (session.isPrefaceRead()) {
            if (src.remaining() < PREFACE_BINARY.length) {
                return null;
            }
            for (int i = 0; i < PREFACE_BINARY.length; i++) {
                if (src.getByte() != PREFACE_BINARY[i]) {
                    throw NOT_HTTP2_PROTOCL;
                }
            }
            session.setPrefaceRead(false);
            //这里应该向客户端返回settings帧，先不返回了，等解析完客户端发来的
            //settings帧再给客户端返回
            Http2SettingsFrame f = new Http2SettingsFrame();
            f.setSettings(session.getSettings());
            ch.flush(f);
            return decode(ch, src);
        }
        if (src.remaining() < PROTOCOL_HEADER) {
            return null;
        }
        byte b0 = src.getByte();
        byte b1 = src.getByte();
        byte b2 = src.getByte();
        byte type = src.getByte();
        byte flags = src.getByte();
        int v = src.getInt();
        int length = ((b0 & 0xff) << 8 * 2) | ((b1 & 0xff) << 8 * 1) | ((b2 & 0xff) << 8 * 0);
        int streamIdentifier = v & 0xFFFFFFFF;
        if (src.remaining() < length) {
            src.skip(-PROTOCOL_HEADER);
            return null;
        }
        Http2FrameType hType = Http2FrameType.getValue(type & 0xff);
        Http2FrameHeader frame = genFrame(hType, length, streamIdentifier, flags);
        return frame.decode(session, src, length);
    }

    @Override
    public ByteBuf encode(NioSocketChannel ch, Frame frame) throws IOException {
        Http2Frame f = (Http2Frame) frame;
        Http2FrameType frameType = f.getHttp2FrameType();
        byte[] payload = null;
        switch (frameType) {
            case FRAME_TYPE_CONTINUATION:
                break;
            case FRAME_TYPE_DATA:
                break;
            case FRAME_TYPE_GOAWAY:
                break;
            case FRAME_TYPE_HEADERS:
                Http2HeadersFrame hf = (Http2HeadersFrame) f;
                break;
            case FRAME_TYPE_PING:
                break;
            case FRAME_TYPE_PRIORITY:
                break;
            case FRAME_TYPE_PUSH_PROMISE:
                break;
            case FRAME_TYPE_RST_STREAM:
                break;
            case FRAME_TYPE_SETTINGS:
                Http2SettingsFrame sf = (Http2SettingsFrame) f;
                long[] settings = sf.getSettings();
                payload = new byte[6 * 6];
                for (int i = 0; i < 6; i++) {
                    int realI = i + 1;
                    int offset = i * 6;
                    MathUtil.unsignedShort2Byte(payload, realI, offset);
                    MathUtil.unsignedInt2Byte(payload, settings[realI], offset + 2);
                }
                break;
            case FRAME_TYPE_WINDOW_UPDATE:
                break;
            default:
                break;
        }
        int length = payload.length;
        ByteBuf buf = ch.alloc().allocate(length + PROTOCOL_HEADER);
        byte b2 = (byte) ((length & 0xff));
        byte b1 = (byte) ((length >> 8 * 1) & 0xff);
        byte b0 = (byte) ((length >> 8 * 2) & 0xff);
        byte b3 = frameType.getByteValue();
        buf.putByte(b0);
        buf.putByte(b1);
        buf.putByte(b2);
        buf.putByte(b3);
        buf.putByte((byte) 0);
        buf.putInt(f.getStreamIdentifier());
        buf.put(payload);
        return buf.flip();
    }

    @Override
    public String getProtocolId() {
        return "Http2";
    }

}
