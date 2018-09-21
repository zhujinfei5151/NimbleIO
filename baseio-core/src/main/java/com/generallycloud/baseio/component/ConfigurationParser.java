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
package com.generallycloud.baseio.component;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

import com.generallycloud.baseio.common.ClassUtil;
import com.generallycloud.baseio.common.Properties;
import com.generallycloud.baseio.common.StringUtil;

/**
 * @author wangkai
 *
 */
public class ConfigurationParser {

    public static void parseConfiguration(String prefix, Object cfg, Properties properties)
            throws Exception {
        Class<?> clazz = cfg.getClass();
        for (;;) {
            if (clazz == Object.class) {
                break;
            }
            parseConfiguration(prefix, cfg, clazz, properties);
            clazz = clazz.getSuperclass();
        }
        if (cfg instanceof Configuration) {
            ((Configuration) cfg).configurationChanged(properties);
        }
    }

    private static void parseConfiguration(String prefix, Object cfg, Class<?> clazz,
            Properties properties) throws Exception {
        Field[] fs = clazz.getDeclaredFields();
        for (Field f : fs) {
            Class<?> type = f.getType();
            String name = f.getName();
            if (type == String.class) {
                String v = properties.getProperty(prefix + name);
                if (StringUtil.isNullOrBlank(v)) {
                    continue;
                }
                ClassUtil.trySetAccessible(f);
                f.set(cfg, v);
            } else if (type == int.class) {
                int v = properties.getIntegerProperty(prefix + name);
                if (v == 0) {
                    continue;
                }
                ClassUtil.trySetAccessible(f);
                f.set(cfg, v);
            } else if (type == double.class) {
                double v = properties.getDoubleProperty(prefix + name);
                if (v == 0) {
                    continue;
                }
                ClassUtil.trySetAccessible(f);
                f.set(cfg, v);
            } else if (type == boolean.class) {
                ClassUtil.trySetAccessible(f);
                f.set(cfg, properties.getBooleanProperty(prefix + name));
            } else if (type == long.class) {
                long v = properties.getLongProperty(prefix + name);
                if (v == 0) {
                    continue;
                }
                ClassUtil.trySetAccessible(f);
                f.set(cfg, v);
            } else if (type == Charset.class) {
                ClassUtil.trySetAccessible(f);
                f.set(cfg, Charset.forName(properties.getProperty(prefix + name, "GBK")));
            }
        }
    }

}
