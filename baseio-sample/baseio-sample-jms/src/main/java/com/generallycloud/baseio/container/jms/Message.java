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
package com.generallycloud.baseio.container.jms;

//增加消息时效
public interface Message {

    public static final int TYPE_ERROR     = 0;
    public static final int TYPE_NULL      = 1;
    public static final int TYPE_TEXT      = 2;
    public static final int TYPE_TEXT_BYTE = 3;
    public static final int TYPE_MAP       = 4;
    public static final int TYPE_MAP_BYTE  = 5;

    public abstract String getQueueName();

    public abstract String getMsgId();

    public abstract int getMsgType();

    public abstract long getTimestamp();
}
