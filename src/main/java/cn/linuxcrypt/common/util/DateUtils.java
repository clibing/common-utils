package cn.linuxcrypt.common.util;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

/**
 * 功能：
 * 作者：刘柏勋
 * 联系：wmsjhappy@gmail.com
 * 时间：17-3-29 下午4:02
 * 更新：
 * 备注：
 */
public final class DateUtils {
    public final static String DATE_TIME_FORMAT_1 = "yyyy.MM.dd HH:mm:ss";
    public final static String DATE_TIME_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";
    private final static DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT_1);

    public static String now() {
        return DateTime.now().toString(DATE_TIME_FORMAT_1);
    }

    public static String format(String format) {
        return DateTime.now().toString(format);
    }

    public static Long plusDayMaxValueNanoByNow(int day) {
        // fixme joda-time 支持毫秒
        return DateTime.now().plusDays(day).millisOfDay().withMaximumValue().getMillis() * 1000;
    }

    public static String nanoTime(Long nanotime) {
        return new DateTime(nanotime / 1000L).toString(DATE_TIME_FORMAT_1);
    }

    public static String nanoTime(Long nanotime, String format) {
        return new DateTime(nanotime / 1000L).toString(DATE_TIME_FORMAT_1);
    }

    public static String millsTime(Long millstime) {
        return new DateTime(millstime).toString(DATE_TIME_FORMAT_1);
    }

    public static boolean checkDateTimeIsExpire(String strDateTime, Integer expireSeconds) {
        return DateTime.parse(strDateTime, formatter).plusSeconds(expireSeconds).isBeforeNow();
    }

    public static Long formatStrToNanoTime(String datetime) {
        return DateTime.parse(datetime, formatter).getMillis() * 1000;
    }

    public static int toSencond(int day, int hour) {
        DateTime start = DateTime.now();
        DateTime end = start.plusHours(hour).plusDays(day);
        int value = Seconds.secondsBetween(start, end).getSeconds();
        return value;
    }

    public static long plus(int year, int day, int hour) {
        return DateTime.now().plusYears(year).plusDays(day).plusHours(hour).getMillis();
    }

    /**
     * 获取当前时间离当天结束时间的剩下时间,单位:s
     *
     * @return
     */
    public static Long getLeftoverTimeTheDay() {
        Calendar calendar = Calendar.getInstance();
        long nowTime = calendar.getTimeInMillis() / 1000;
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long endTime = calendar.getTimeInMillis() / 1000;
        return endTime - nowTime;
    }
}
