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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final ThreadLocal<DateUtil> dateUtils = new ThreadLocal<>();

    public static DateUtil get() {
        DateUtil d = dateUtils.get();
        if (d == null) {
            d = new DateUtil();
            dateUtils.set(d);
        }
        return d;
    }

    private final DateFormat HH_mm_ss                = new SimpleDateFormat("HH:mm:ss");
    private final DateFormat yyyy_MM_dd              = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat yyyy_MM_dd_HH_mm_ss     = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DateFormat yyyy_MM_dd_HH_mm_ss_SSS = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss.SSS");
    private final DateFormat yyyyMMdd_HH_mm_ss       = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    private final DateFormat yyyyMMdd                = new SimpleDateFormat("yyyyMMdd");
    private final DateFormat yyMMdd                  = new SimpleDateFormat("yyMMdd");
    private final DateFormat yyyyMMddHHmmss          = new SimpleDateFormat("yyyyMMddHHmmss");

    public Date parseHH_mm_ss(String source) {
        try {
            return HH_mm_ss.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date parseYyyy_MM_dd(String source) {
        try {
            return yyyy_MM_dd.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date parseYyyy_MM_dd_HH_mm_ss(String source) {
        try {
            return yyyy_MM_dd_HH_mm_ss.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date parseYyyyMMdd_HH_mm_ss(String source) {
        try {
            return yyyyMMdd_HH_mm_ss.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date parseYyyyMMdd(String source) {
        try {
            return yyyyMMdd.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date parseYyMMdd(String source) {
        try {
            return yyMMdd.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date parseYyyyMMddHHmmss(String source) {
        try {
            return yyyyMMddHHmmss.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public Date parseYyyy_MM_dd_HH_mm_ss_SSS(String source) {
        try {
            return yyyy_MM_dd_HH_mm_ss_SSS.parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    //  --------------------------------------------------------------------------------

    public String formatHH_mm_ss(Date date) {
        return HH_mm_ss.format(date);
    }

    public String formatYyyy_MM_dd(Date date) {
        return yyyy_MM_dd.format(date);
    }

    public String formatYyyy_MM_dd_HH_mm_ss(Date date) {
        return yyyy_MM_dd_HH_mm_ss.format(date);
    }

    public String formatYyyyMMdd_HH_mm_ss(Date date) {
        return yyyyMMdd_HH_mm_ss.format(date);
    }

    public String formatYyyyMMdd(Date date) {
        return yyyyMMdd.format(date);
    }

    public String formatYyMMdd(Date date) {
        return yyMMdd.format(date);
    }

    public String formatYyyyMMddHHmmss(Date date) {
        return yyyyMMddHHmmss.format(date);
    }

    public String formatYyyy_MM_dd_HH_mm_ss_SSS(Date date) {
        return yyyy_MM_dd_HH_mm_ss_SSS.format(date);
    }

    //  --------------------------------------------------------------------------------

    public String formatHH_mm_ss() {
        return formatHH_mm_ss(new Date());
    }

    public String formatYyyy_MM_dd() {
        return formatYyyy_MM_dd(new Date());
    }

    public String formatYyyy_MM_dd_HH_mm_ss() {
        return formatYyyy_MM_dd_HH_mm_ss(new Date());
    }

    public String formatYyyyMMdd_HH_mm_ss() {
        return formatYyyyMMdd_HH_mm_ss(new Date());
    }

    public String formatYyyyMMdd() {
        return formatYyyyMMdd(new Date());
    }

    public String formatYyMMdd() {
        return formatYyMMdd(new Date());
    }

    public String formatYyyyMMddHHmmss() {
        return formatYyyyMMddHHmmss(new Date());
    }

    public String formatYyyy_MM_dd_HH_mm_ss_SSS() {
        return formatYyyy_MM_dd_HH_mm_ss_SSS(new Date());
    }

    //  --------------------------------------------------------------------------------

    private static final TimeZone GTM = TimeZone.getTimeZone("GTM");

    private int parseInt(String cs, int begin, int end) {
        int sum = 0;
        for (int i = begin; i < end; i++) {
            sum = sum * 10 + (cs.charAt(i) - 48);
        }
        return sum;
    }

    public Date parseHttp(String source) {
        int day = parseInt(source, 5, 7);
        int year = parseInt(source, 12, 16);
        int hour = parseInt(source, 17, 19);
        int minute = parseInt(source, 20, 22);
        int second = parseInt(source, 23, 25);
        int month = getMonth(source, 8, 11);

        Calendar calendar = Calendar.getInstance(GTM);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static void main(String[] args) {
        Date d = new Date();
        System.out.println(DateUtil.get().formatYyyy_MM_dd_HH_mm_ss(d));
        String str = get().formatHttp(d.getTime());
        System.out.println(str);
        d = get().parseHttp(str);
        System.out.println(DateUtil.get().formatYyyy_MM_dd_HH_mm_ss(d));
        
        System.out.println(new String(get().formatHttpBytes()));
    }

    private int getMonth(String month, int begin, int end) {
        char c1 = month.charAt(begin);
        char c2 = month.charAt(begin + 1);
        char c3 = month.charAt(begin + 2);
        int c = (c1 << 16) | (c2 << 8) | c3;
        switch (c) {
            case ('J' << 16) | ('a' << 8) | ('n'):
                return 0;
            case ('F' << 16) | ('e' << 8) | ('b'):
                return 1;
            case ('M' << 16) | ('a' << 8) | ('r'):
                return 2;
            case ('A' << 16) | ('p' << 8) | ('r'):
                return 3;
            case ('M' << 16) | ('a' << 8) | ('y'):
                return 4;
            case ('J' << 16) | ('u' << 8) | ('n'):
                return 5;
            case ('J' << 16) | ('u' << 8) | ('l'):
                return 6;
            case ('A' << 16) | ('u' << 8) | ('g'):
                return 7;
            case ('S' << 16) | ('e' << 8) | ('p'):
                return 8;
            case ('O' << 16) | ('c' << 8) | ('t'):
                return 9;
            case ('N' << 16) | ('o' << 8) | ('v'):
                return 10;
            case ('D' << 16) | ('e' << 8) | ('v'):
                return 11;
            default:
                return -1;
        }
    }

    private static final String[] WEEK_DAYS = new String[] { "", "Sun", "Mon", "Tue", "Wed", "Thu",
            "Fri", "Sat" };

    private static final String[] MONTHS    = new String[] { "Jan", "Feb", "Mar", "Apr", "May",
            "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };

    public String formatHttp() {
        return formatHttp(System.currentTimeMillis());
    }

    public String formatHttp(long time) {
        calendar.setTimeInMillis(time);

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        StringBuilder b = new StringBuilder(26);
        b.append(WEEK_DAYS[weekDay]);
        b.append(',');
        b.append(' ');
        if (day < 10) {
            b.append('0');
        }
        b.append(day);
        b.append(' ');
        b.append(MONTHS[month]);
        b.append(' ');
        b.append(year);
        b.append(' ');
        if (hour < 10) {
            b.append('0');
        }
        b.append(hour);
        b.append(':');
        if (minute < 10) {
            b.append('0');
        }
        b.append(minute);
        b.append(':');
        if (second < 10) {
            b.append('0');
        }
        b.append(second);
        b.append(" GTM");

        return b.toString();
    }

    public byte[] formatHttpBytes() {
        return formatHttpBytes(System.currentTimeMillis());
    }

    private static final byte[][] WEEK_DAYS_BYTES = new byte[][] { "".getBytes(), "Sun".getBytes(),
            "Mon".getBytes(), "Tue".getBytes(), "Wed".getBytes(), "Thu".getBytes(),
            "Fri".getBytes(), "Sat".getBytes() };

    private static final byte[][] MONTHS_BYTES    = new byte[][] { "Jan".getBytes(),
            "Feb".getBytes(), "Mar".getBytes(), "Apr".getBytes(), "May".getBytes(),
            "Jun".getBytes(), "Jul".getBytes(), "Aug".getBytes(), "Sep".getBytes(),
            "Oct".getBytes(), "Nov".getBytes(), "Dec".getBytes() };

    private static final byte[]   NS              = new byte[10];

    static {
        for (int i = 0; i < NS.length; i++) {
            NS[i] = (byte) String.valueOf(i).charAt(0);
        }
    }

    private Calendar calendar = Calendar.getInstance(GTM);
    
    public byte[] formatHttpBytes(long time) {
        calendar.setTimeInMillis(time);

        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        byte[] b = new byte[29];
        byte[] days = WEEK_DAYS_BYTES[weekDay];
        byte[] months = MONTHS_BYTES[month];
        b[0] = days[0];
        b[1] = days[1];
        b[2] = days[2];
        b[3] = ',';
        b[4] = ' ';
        b[5] = NS[day / 10];
        b[6] = NS[day % 10];
        b[7] = ' ';
        b[8] = months[0];
        b[9] = months[1];
        b[10] = months[2];
        b[11] = ' ';
        b[12] = NS[year / 1000];
        b[13] = NS[(year / 100) % 10];
        b[14] = NS[(year / 10) % 10];
        b[15] = NS[year % 10];
        b[16] = ' ';
        b[17] = NS[hour / 10];
        b[18] = NS[hour % 10];
        b[19] = ':';
        b[20] = NS[minute / 10];
        b[21] = NS[minute % 10];
        b[22] = ':';
        b[23] = NS[second / 10];
        b[24] = NS[second % 10];
        b[25] = ' ';
        b[26] = 'G';
        b[27] = 'T';
        b[28] = 'M';
        return b;
    }

}
