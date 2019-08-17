package cn.linuxcrypt.common.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
    private final static String DATE_TIME_FORMAT_1 = "yyyy.MM.dd HH:mm:ss";
    private final static DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT_1);

    public static String now() {
        return DateTime.now().toString(DATE_TIME_FORMAT_1);
    }

    /**
     * 获取传入时间与当前时间的时间差
     */
    public static String differTime(String actionTime) {
        if (StringUtils.isBlank(actionTime) || actionTime.length() < 13) {
            return "";
        }
        return getDifferTime(getCurrentTimestamp() / 1000, actionTime);
    }

    /**
     * 获取传入时间与当前时间的时间差
     */
    public static String differTime(Long actionTime) {
        return differTime(actionTime.toString());
    }

    /**
     * 时间比对的方法
     */
    private static String getDifferTime(long nowTotalTime, String actionTime) {
        return parseDifferTime(nowTotalTime, actionTime, "前");
    }

    public static Long localDate2Long(java.time.LocalDateTime localDateTime) {
        return localDateTime.toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
    }

    /**
     * 获取当前时间的16位时间戳
     */
    public static Long getCurrentTimestamp() {
        return System.currentTimeMillis() * 1000;
    }

    /**
     * 计算时间差
     */
    private static String parseDifferTime(Long nowTotalTime, String actionTime, String suffix) {
        if (StringUtils.isBlank(actionTime)) {
            return "";
        }
        long oldTotalTime = Long.parseLong(actionTime);
        oldTotalTime = oldTotalTime / 1000;
        int rate = 1000;

        if (nowTotalTime - oldTotalTime <= 60 * rate) {
            return "小于1分钟" + suffix;

        } else if (nowTotalTime > oldTotalTime
                && nowTotalTime - oldTotalTime < 60l * 60 * rate) {
            return ((nowTotalTime - oldTotalTime) / (60 * rate)) + "分钟"
                    + suffix;

        } else if (nowTotalTime > oldTotalTime
                && (nowTotalTime - oldTotalTime) < 60l * 60 * 24 * rate) {
            long h = (nowTotalTime - oldTotalTime) / (60 * 60 * rate);
            long min = (((nowTotalTime - oldTotalTime - h * 60 * 60 * rate) / (60 * rate)));
            if (min < 59) {
                min = min + 1;
            }
            return h + "小时" + min + "分钟" + suffix;

        } else if (nowTotalTime > oldTotalTime
                && (nowTotalTime - oldTotalTime) < 60l * 60 * 24 * 30 * rate) {
            long d = (nowTotalTime - oldTotalTime) / (60 * 60 * 24 * rate);
            long h = (nowTotalTime - oldTotalTime - d * 60l * 60 * 24 * rate)
                    / (60 * 60 * rate);
            if (h < 23) {
                h = h + 1;
            }
            return d + "天" + h + "小时" + suffix;

        } else {
            Date dt = new Date(Long.parseLong(actionTime.substring(0, 13)));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            int nowYear = Calendar.getInstance().get(Calendar.YEAR);
            int oldYear = Integer.parseInt(df.format(dt).substring(0,
                    df.format(dt).indexOf("-")));
            if (nowYear != oldYear && nowYear > oldYear) {
                df = new SimpleDateFormat("yyyy-MM-dd");
            }
            return df.format(dt);
        }
    }

    public static Long getBeginStamp(String dateStr, int offset) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(formatDay(dateStr));
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        cl.add(Calendar.DAY_OF_MONTH, offset);
        return cl.getTimeInMillis();
    }

    private static String formatDay(String date) {
        if (date.length() == 8) {
            return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6);
        }
        return date;
    }
}
