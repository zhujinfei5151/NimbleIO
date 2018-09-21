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
package com.generallycloud.baseio.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ThrowableUtil {

    private ThrowableUtil() {}

    public static <T extends Throwable> T unknownStackTrace(T cause, Class<?> clazz,
            String method) {
        cause.setStackTrace(new StackTraceElement[] {
                new StackTraceElement(clazz.getName(), method, null, -1) });
        return cause;
    }

    public static String stackTraceToString(Throwable cause) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(out);
        cause.printStackTrace(pout);
        pout.flush();
        try {
            return new String(out.toByteArray());
        } finally {
            CloseUtil.close(out);
        }
    }

}
