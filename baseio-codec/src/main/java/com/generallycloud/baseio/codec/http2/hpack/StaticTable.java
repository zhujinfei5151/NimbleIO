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
package com.generallycloud.baseio.codec.http2.hpack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.generallycloud.baseio.codec.http2.Http2Header;

final class StaticTable {

    private static List<Http2Header>        STATIC_HEADER_TABLE = new ArrayList<>(62);

    private static Map<String, Http2Header> STATIC_MAP          = new HashMap<>();

    static {
        STATIC_HEADER_TABLE.add(new Http2Header(0, ":error", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(1, ":authority", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(2, ":method", "GET"));
        STATIC_HEADER_TABLE.add(new Http2Header(3, ":method", "POST"));
        STATIC_HEADER_TABLE.add(new Http2Header(4, ":path", "/"));
        STATIC_HEADER_TABLE.add(new Http2Header(5, ":path", "/index.html"));
        STATIC_HEADER_TABLE.add(new Http2Header(6, ":scheme", "http"));
        STATIC_HEADER_TABLE.add(new Http2Header(7, ":scheme", "https"));
        STATIC_HEADER_TABLE.add(new Http2Header(8, ":status", "200"));
        STATIC_HEADER_TABLE.add(new Http2Header(9, ":status", "204"));
        STATIC_HEADER_TABLE.add(new Http2Header(10, ":status", "206"));
        STATIC_HEADER_TABLE.add(new Http2Header(11, ":status", "304"));
        STATIC_HEADER_TABLE.add(new Http2Header(12, ":status", "400"));
        STATIC_HEADER_TABLE.add(new Http2Header(13, ":status", "404"));
        STATIC_HEADER_TABLE.add(new Http2Header(14, ":status", "500"));
        STATIC_HEADER_TABLE.add(new Http2Header(15, "accept-charset", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(16, "accept-encoding", "gzip, deflate"));
        STATIC_HEADER_TABLE.add(new Http2Header(17, "accept-language", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(18, "accept-ranges", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(19, "accept", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(20, "access-control-allow-origin", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(21, "age", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(22, "allow", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(23, "authorization", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(24, "cache-control", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(25, "content-disposition", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(26, "content-encoding", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(27, "content-language", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(28, "content-length", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(29, "content-location", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(30, "content-range", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(31, "content-type", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(32, "cookie", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(33, "date", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(34, "etag", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(35, "expect", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(36, "expires", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(37, "from", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(38, "host", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(39, "if-match", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(40, "if-modified-since", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(41, "if-none-match", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(42, "if-range", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(43, "if-unmodified-since", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(44, "last-modified", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(45, "link", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(46, "location", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(47, "max-forwards", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(48, "proxy-authenticate", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(49, "proxy-authorization", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(50, "range", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(51, "referer", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(52, "refresh", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(53, "retry-after", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(54, "server", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(55, "set-cookie", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(56, "strict-transport-security", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(57, "transfer-encoding", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(58, "user-agent", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(59, "vary", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(60, "via", ""));
        STATIC_HEADER_TABLE.add(new Http2Header(61, "www-authenticate", ""));

        for (Http2Header header : STATIC_HEADER_TABLE) {

            STATIC_MAP.put(header.getName(), header);
        }
    }

    static final int length = STATIC_HEADER_TABLE.size();

    static Http2Header getEntry(int index) {
        return STATIC_HEADER_TABLE.get(index);
    }

    // singleton
    private StaticTable() {}

    public static int getIndex(String name) {
        return getIndex(name, "");
    }

    public static int getIndex(String name, String value) {

        Http2Header header = STATIC_MAP.get(name);

        if (header == null) {
            return -1;
        }

        if (header.getValue().equals(value)) {
            return header.getIndex();
        }

        return 0;
    }
}
