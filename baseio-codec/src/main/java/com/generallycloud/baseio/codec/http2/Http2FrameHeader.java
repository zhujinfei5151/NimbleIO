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
import com.generallycloud.baseio.component.NioSocketChannel;
import com.generallycloud.baseio.protocol.AbstractFrame;

public abstract class Http2FrameHeader extends AbstractFrame implements Http2Frame {

    public static final int FLAG_END_STREAM  = 0x1;
    public static final int FLAG_END_HEADERS = 0x4;
    public static final int FLAG_PADDED      = 0x8;
    public static final int FLAG_PRIORITY    = 0x20;

    private byte            flags;
    private int             streamIdentifier;

    @Override
    public boolean read(NioSocketChannel ch, ByteBuf buffer) throws IOException {
        return true;
    }

    @Override
    public byte getFlags() {
        return flags;
    }

    @Override
    public int getStreamIdentifier() {
        return streamIdentifier;
    }

    @Override
    public void setFlags(byte flags) {
        this.flags = flags;
    }

    @Override
    public void setStreamIdentifier(int streamIdentifier) {
        this.streamIdentifier = streamIdentifier;
    }

    abstract Http2Frame decode(Http2Session session, ByteBuf src, int length) throws IOException;

}
