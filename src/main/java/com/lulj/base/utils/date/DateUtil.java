package com.lulj.base.utils.date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * 日期处理工具类
 *
 * @author lu
 */
public class DateUtil {
    private static String defaultDatePattern = "yyyy-MM-dd";// 默认的年月日

    public static final String hour12HMSPattern = "yyyy-MM-dd hh:mm:ss";// 年月日
    // 时分秒12小时制
    public static final String hour12HMPattern = "yyyy-MM-dd hh:mm"; // ...年月日
    // 时分..12小时制
    public static final String hour12HPattern = "yyyy-MM-dd hh";// ........年月日
    // 时....12小时制
    public static final String hour12HPatternMillisecond = "yyyy-MM-dd-hh-mm-ss-SSS";// ........年月日
    // 时分秒毫秒....24小时制

    public static final String hour24HMSPattern = "yyyy-MM-dd HH:mm:ss";// 年月日

    // 时分秒24小时制
    public static final String hour24HMPattern = "yyyy-MM-dd HH:mm";// ....年月日
    // 时分秒24小时制
    public static final String hour24Pattern = "HH:mm:ss";// ....时分秒
    // 时分..24小时制
    public static final String hour24HPattern = "yyyy-MM-dd HH";// ........年月日
    // 时....24小时制
    public static final String hour24HPatternMillisecond = "yyyy-MM-dd-HH-mm-ss-SSS";// ........年月日
    // 时分秒毫秒....24小时制

    public static final String hour24HPatternMillisecondStr = "yyyyMMddHHmmssSSS";


    public static final String days = "dd";

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static SimpleDateFormat sdfMinute = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");


    public static final FastDateFormat simpleDateFormat_default = FastDateFormat.getInstance("yyyy/MM/dd");
    public static final FastDateFormat simpleDateFormat_ym = FastDateFormat.getInstance("yyyy/MM");
    public static final FastDateFormat simpleDateFormat_no_slash = FastDateFormat.getInstance("yyyyMMdd");
    public static final FastDateFormat simpleDateFormat_ymr_slash = FastDateFormat.getInstance("yyyy-MM-dd");
    public static final FastDateFormat simpleDateFormat_ym_no_slash = FastDateFormat.getInstance("yyyyMM");
    public static final FastDateFormat simpleDateFormat_date_time = FastDateFormat.getInstance("yyyy/MM/dd HH:mm:ss");
    public static final FastDateFormat simpleDateFormat_date_time_no_slash = FastDateFormat.getInstance("yyyyMMddHHmmss");
    public static final FastDateFormat simpleDateFormat_date_hm = FastDateFormat.getInstance("yyyy/MM/dd HH:mm");
    public static final FastDateFormat simpleDateFormat_time = FastDateFormat.getInstance("HH:mm:ss");
    public static final FastDateFormat simpleDateFormat_hm = FastDateFormat.getInstance("HH:mm");
    public static final FastDateFormat simpleDateFormat_long_time = FastDateFormat.getInstance("HHmmss");
    public static final FastDateFormat simpleDateFormat_short_time = FastDateFormat.getInstance("HHmm");
    public static final FastDateFormat simpleDateFormat_date_time_line = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");


    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd
     *
     * @since 1.0
     */
    public static final int DEFAULT = 0;

    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM
     *
     * @since 1.0
     */
    public static final int YM = 1;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd
     *
     * @since 1.0
     */
    public static final int YMR_SLASH = 11;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMMdd
     *
     * @since 1.0
     */
    public static final int NO_SLASH = 2;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMM
     *
     * @since 1.0
     */
    public static final int YM_NO_SLASH = 3;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd HH:mm:ss
     *
     * @since 1.0
     */
    public static final int DATE_TIME = 4;

    /**
     * 变量：日期格式化类型 - 格式:yyyyMMddHHmmss
     *
     * @since 1.0
     */
    public static final int DATE_TIME_NO_SLASH = 5;

    /**
     * 变量：日期格式化类型 - 格式:yyyy/MM/dd HH:mm
     *
     * @since 1.0
     */
    public static final int DATE_HM = 6;

    /**
     * 变量：日期格式化类型 - 格式:HH:mm:ss
     *
     * @since 1.0
     */
    public static final int TIME = 7;

    /**
     * 变量：日期格式化类型 - 格式:HH:mm
     *
     * @since 1.0
     */
    public static final int HM = 8;

    /**
     * 变量：日期格式化类型 - 格式:HHmmss
     *
     * @since 1.0
     */
    public static final int LONG_TIME = 9;

    /**
     * 变量：日期格式化类型 - 格式:HHmm
     *
     * @since 1.0
     */
    public static final int SHORT_TIME = 10;

    /**
     * 变量：日期格式化类型 - 格式:yyyy-MM-dd HH:mm:ss
     *
     * @since 1.0
     */
    public static final int DATE_TIME_LINE = 12;

    /**
     * 变量：1小时的毫秒数
     *
     * @since 1.0
     */
    public static final double HOUR_MILLISECOND = 1000 * 60 * 60;

    /**
     * 星期常量
     */
    public static final String[] WEEK = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    /**
     * 常用日期格式化
     */
    public static final String[] DATE_PATTERN = new String[]{"yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.S"};

    public static final String[] DATE_PATTERN2 = new String[]{"yyyyMMddHHmmss"};


    public static final String[] DATE_PATTERN3 = new String[]{"yyyy-MM-dd HH:mm:ss"};

    /**
     * 返回默认格式的当前日期
     */
    public static String getToday() {
        Date today = new Date();
        return format(today);
    }

    /**
     * 返回默认格式的当前时间戳字符串格式
     */
    public static String getCurrentDate() {
        return format(new Date(), defaultDatePattern);
    }

    /**
     * 返回默认格式的当前时间戳字符串格式
     */
    public static String getCurrentTime() {
        return format(new Date(), hour24HMSPattern);
    }

    /**
     * 返回默认格式的当前时间上一秒戳字符串格式
     */
    public static String getCurrentTimeLastSecond() {
        return format(new Date(System.currentTimeMillis() - 1000), hour24HMSPattern);
    }

    /**
     * 返回默认格式的当前时间上N秒戳字符串格式
     */
    public static String getCurrentTimeLastNSecond(int second) {
        return format(new Date(System.currentTimeMillis() - second * 1000), hour24HMSPattern);
    }

    /**
     * 日志调用，获取时间戳字符串
     */
    public static String logTime() {
        String logTime = format(new Date(), hour24HMSPattern);
        return logTime + " --> ";
    }

    /**
     * 返回指定 格式的当前时间戳字符串格式
     */
    public static String getCurrentDate(String pattern) {
        return format(new Date(), pattern);
    }

    /**
     * 返回默认格式的当前时间戳
     */
    public static Timestamp getCurrentTimestamp() {
        String timestamp = format(new Date(), hour24HMSPattern);
        return Timestamp.valueOf(timestamp);
    }

    /**
     * 返回 默认格式下的前一天的日期
     */
    public static String getYestoday() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal1.add(Calendar.DATE, -1);
        return format(cal1.getTime());
    }

    /**
     * 取得当前日期所在月份的第一天
     */
    public static String getFirstOfMonth() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal1.set(Calendar.DATE, 1);// 日，设为一号
        return format(cal1.getTime());
    }

    /**
     * 取得当前日期所在月份的最后一天
     */
    public static String getEndOfMonth() {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(new Date());
        cal1.set(Calendar.DATE, cal1.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format(cal1.getTime());
    }

    /**
     * 取得指定日期所在月份的第一天
     *
     * @param strDate 指定的日期
     * @return
     * @throws ParseException
     */
    public static String getFirstOfMonth(String strDate) throws ParseException {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(parse(strDate));
        cal1.set(Calendar.DATE, 1);// 日，设为一号
        return format(cal1.getTime());
    }

    /**
     * 取得指定日期所在月份的最后一天
     *
     * @param strDate 指定的日期
     * @return
     * @throws ParseException
     */
    public static String getEndOfMonth(String strDate) throws ParseException {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(parse(strDate));
        cal1.set(Calendar.DATE, cal1.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format(cal1.getTime());
    }

    /**
     * 取得某年某月的最后一天的日期
     *
     * @param year
     * @param month
     * @return
     */
    public static String getEndOfMonth(int year, int month) {
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.YEAR, year);// 年
        cal1.set(Calendar.MONTH, month - 1);// 月，因为Calendar里的月是从0开始，所以要减1
        cal1.set(Calendar.DATE, 1);// 日，设为一号
        cal1.add(Calendar.MONTH, 1);// 月份加一，得到下个月的一号
        cal1.add(Calendar.DATE, -1);// 下一个月减一为本月最后一天
        return format(cal1.getTime());// 获得月末是几号
    }

    // ******第三部分******************在String和Date之间转换*******************************

    /**
     * 使用默认格式转换Date成字符串
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        return format(date, defaultDatePattern);
    }

    /**
     * 使用指定格式转换Date成字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String format(Date date, String pattern) {
        String returnValue = "";
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    /**
     * 使用默认格式将字符串转为Date
     *
     * @param strDate
     * @return
     * @throws ParseException
     */
    public static Date parse(String strDate) throws ParseException {
        return parse(strDate, defaultDatePattern);
    }

    /**
     * 返回格式化时间
     */
    public static Date parseTime(String strTime) throws Exception {
        Date dateTime = parse(strTime, hour24HMSPattern);
        return dateTime;
    }

    /**
     * 使用指定格式将字符串转为Date
     *
     * @param strDate
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parse(String strDate, String pattern) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.parse(strDate);
    }

    /**
     * 转换日期字符串为Timestamp类型
     *
     * @param strTimestamp
     * @return
     */
    public static Timestamp convertStringToTimestamp(String strTimestamp) {
        return Timestamp.valueOf(strTimestamp);
    }

    @SuppressWarnings("deprecation")
    public static String convertTimestampToString(Timestamp timestamp) {
        Date date = new Date(timestamp.getYear(), timestamp.getMonth(), timestamp.getDay(), timestamp.getHours(),
                timestamp.getMinutes(), timestamp.getSeconds());
        return format(date, defaultDatePattern);
    }

    /**
     * 在日期上月份加减(当n为负数时即为减)
     *
     * @param date
     * @param n
     * @return
     */
    public static Date addMonths(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    /**
     * 在日期上天数加减(当n为负数时即为减)
     *
     * @param date
     * @param n
     * @return
     */
    public static Date addDays(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, n);
        return cal.getTime();
    }

    /**
     * 在日期上天数加减(当n为负数时即为减)
     *
     * @param sdate
     * @param n
     * @return
     * @throws ParseException
     */
    public static String addDays(String sdate, int n) throws ParseException {
        Date inDate = parse(sdate);
        Date outDate = addDays(inDate, n);
        return format(outDate);
    }

    /**
     * 返回当前年
     *
     * @return
     */
    public static int getYear() {
        Calendar c = Calendar.getInstance();
        int yy = c.get(Calendar.YEAR);
        return yy;
    }

    /**
     * 返回当前月
     *
     * @return
     */
    public static int getMonth() {
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        return month + 1;
    }

    /**
     * 返回当前日
     *
     * @return
     */
    public static int getDate() {
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        return date;
    }

    /**
     * 返回指定日期天数
     *
     * @return
     */
    public static String getDateDay(Date dates) throws Exception {
        return format(dates, days);
    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1 date1
     * @param date2 date2
     * @return
     */
    public static boolean isSameDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        boolean isSameMonth = isSameYear && cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH);
        boolean isSameDate = isSameMonth && cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
        return isSameDate;
    }

    /**
     * 计算两个日期之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int getDays(Date date1, Date date2) {
        // date2应大于date1
        int days = 0;
        days = (int) ((date2.getTime() - date1.getTime()) / (24 * 60 * 60 * 1000));
        return days;
    }

    /**
     * 获取当前天数的起始时间
     *
     * @return
     */
    public static String getDayOfStart() {
        return DateUtil.getCurrentDate("yyyy-MM-dd") + " 00:00:00";

    }

    /**
     * 获取当前天数的结束时间
     *
     * @return
     */
    public static String getDayOfEnd() {
        return DateUtil.getCurrentDate("yyyy-MM-dd") + " 23:59:59";

    }

    /**
     * 获取前一天的起始时间
     *
     * @return
     */
    public static String getYesterdayOfStart() {
        return DateUtil.getYestoday() + " 00:00:00";

    }

    /**
     * 获取前一天的结束时间
     *
     * @return
     */
    public static String getYesterdayOfEnd() {
        return DateUtil.getYestoday() + " 23:59:59";

    }

    /**
     * 获取当前月份第一天起始时间
     *
     * @return
     */
    public static String getTheMonthFirstDayTime() {
        return DateUtil.getFirstOfMonth() + " 00:00:00";

    }

    /**
     * 获取当前月份最后一天时间
     *
     * @return
     */
    public static String getTheMonthEndDayTime() {
        return DateUtil.getEndOfMonth() + " 23:59:59";

    }

    /**
     * 获取指定月份第一天起始时间
     *
     * @return
     * @throws ParseException
     */
    public static String getFirstDayTime(String startTime) throws ParseException {
        return DateUtil.getFirstOfMonth(startTime) + " 00:00:00";

    }

    /**
     * 获取指定月份最后一天时间
     *
     * @return
     * @throws ParseException
     */
    public static String getEndDayTime(String endTime) throws ParseException {
        return DateUtil.getEndOfMonth(endTime) + " 23:59:59";

    }

    /**
     * 判断是否符合日期格式 yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static boolean isFormatTime2(String time) {
        try {
            Date d = DateUtils.parseDate(time, DATE_PATTERN3);
            return d != null ? true : false;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 判断是否符合日期格式 yyyyMMddHHmmss
     *
     * @param time
     * @return
     */
    public static boolean isFormatTime(String time) {
        try {
            Date d = DateUtils.parseDate(time, DATE_PATTERN2);
            return d != null ? true : false;
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * 取得时间 yyyyMMddHHmmss
     *
     * @param time
     * @return
     */
    public static String formatTimestamp(long time) {
        return DateFormatUtils.format(time, "yyyyMMddHHmmss");
    }

    public static String formatTimestamp(Integer time) {
        if (time == null) {
            return null;
        }

        String s = String.valueOf(time);
        if (s.length() < 10) {
            return s;
        }

        long t = Long.parseLong(s + "000");
        return formatTimestamp(t);
    }

    /**
     * 获取年和月
     *
     * @param date
     * @return
     */
    public static String getYearMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        String monthStr = month < 10 ? "0" + month : "" + month;
        return year + monthStr;
    }

    /**
     * 取得时间 yyyy-MM-dd HH:mm:ss
     *
     * @return 时间字符串
     */
    public static String getDateTime(Date date) {
        if (date == null) {
            return null;
        }

        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getTextReceiveDate(String time) {
        // 2012 05 17 18 49 03
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(time.substring(0, 4) + "-");
        sbBuffer.append(time.substring(4, 6) + "-");
        sbBuffer.append(time.substring(6, 8) + " ");

        sbBuffer.append(time.substring(8, 10) + ":");
        sbBuffer.append(time.substring(10, 12) + ":");
        sbBuffer.append(time.substring(12, time.length()));

        return sbBuffer.toString();
    }

    /**
     * @param date
     * @return
     */
    public static Date strToDate(String date) {
        try {
            Date d = DateUtils.parseDate(date, DATE_PATTERN);
            return d;
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * @param date
     * @param type
     * @return
     */
    public static String dateToStr(Date date, int type) {
        if (date == null) {
            return null;
        }
        switch (type) {
            case DEFAULT:
                return simpleDateFormat_default.format(date);
            case YM:
                return simpleDateFormat_ym.format(date);
            case NO_SLASH:
                return simpleDateFormat_no_slash.format(date);
            case YMR_SLASH:
                return simpleDateFormat_ymr_slash.format(date);
            case YM_NO_SLASH:
                return simpleDateFormat_ym_no_slash.format(date);
            case DATE_TIME:
                return simpleDateFormat_date_time.format(date);
            case DATE_TIME_NO_SLASH:
                return simpleDateFormat_date_time_no_slash.format(date);
            case DATE_HM:
                return simpleDateFormat_date_hm.format(date);
            case TIME:
                return simpleDateFormat_time.format(date);
            case HM:
                return simpleDateFormat_hm.format(date);
            case LONG_TIME:
                return simpleDateFormat_long_time.format(date);
            case SHORT_TIME:
                return simpleDateFormat_short_time.format(date);
            case DATE_TIME_LINE:
                return simpleDateFormat_date_time_line.format(date);
            default:
                throw new IllegalArgumentException("Type undefined: " + type);
        }
    }

    /**
     * 判定给定的年是否为闰年
     *
     * @param year
     * @return
     */
    private static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * 取得给定的月份的天数
     *
     * @param year
     * @param month
     * @return
     */
    public static int getDaysOfMonth(int year, int month) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (isLeapYear(year)) {
                    return 29;
                }
                return 28;
            default:
                return -1;
        }
    }

    /**
     * 获取Timestamp格式的当前系统时间
     *
     * @return
     */
    public static Timestamp getDateOfTimestamp() {
        Timestamp timeStamp = null;
        String dateTime = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_LINE);
        if (!"".equals(dateTime) && dateTime != null) {
            timeStamp = Timestamp.valueOf(dateTime);
        }
        return timeStamp;
    }

    /**
     * 获取当前系统时间 格式: yyyy-MM-dd
     *
     * @return
     */
    public static String getDateOfString(Timestamp date, int i) {
        String dateTime = dateToStr(date, i);
        return dateTime;
    }


    /**
     * 获取年月日时分秒，格式：yyyyMMddHHmmss
     *
     * @return
     */
    public static String getTimeOfToday() {
        String dateTime = dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
        return dateTime;
    }

    /**
     * 获取本月第一天
     *
     * @return now 当前日期
     */
    public static String getFristMonth() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.set(Calendar.DAY_OF_MONTH, 1);
        String now = simpleDateFormat_ymr_slash.format(ca.getTime());
        return now;
    }

    /**
     * 获取本周第一天
     *
     * @return firstMonday
     */
    public static String getFristWeek() {
        int mondayPlus;
        Calendar cd = Calendar.getInstance();
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 1) {
            mondayPlus = 0;
        } else {
            mondayPlus = 1 - dayOfWeek;
        }
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus);
        Date monday = currentDate.getTime();
        String firstMonday = simpleDateFormat_ymr_slash.format(monday);
        return firstMonday;
    }

    /**
     * 获取当前日期 yyyy-MM-dd
     *
     * @return
     */
    public static String getNowDay() {
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd");
    }

    /**
     * 呼叫开始的时间，单位秒，用1970/1/1 0:00:00以来的秒数来表示，类型为long
     *
     * @param nowDay
     * @return
     */
    public static long getTimestamp(String nowDay) {
        try {
            Date d = DateUtils.parseDate(nowDay, DATE_PATTERN2);
            return d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前时间的时间戳
     *
     * @return
     */
    public static long getTimestamp() {
        long time = System.currentTimeMillis();
        return time;
    }

    /**
     * 获取下一天的时间戳
     *
     * @return
     */
    public static long getNextDayTimestamp(String nowDay) {
        try {
            Date d = DateUtils.addDays(DateUtils.parseDate(nowDay, new String[]{"yyyy-MM-dd"}), 1);
            return d.getTime();
        } catch (ParseException e) {
            return System.currentTimeMillis();
        }
    }

    /**
     * 获取前一天的日期
     *
     * @return
     */
    public static String getPrevDate() {
        Calendar calendar = Calendar.getInstance();// 此时打印它获取的是系统当前时间
        calendar.add(Calendar.DATE, -1); // 得到前一天
        String dayBefore = simpleDateFormat_ymr_slash.format(calendar.getTime());
        return dayBefore;
    }

    /**
     * 获取当前yyyy-MM-dd-HH格式日期
     *
     * @return
     */
    public static String getPrevHour() {
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd-HH");
    }

    /**
     * 判断输入的字符串是否满足时间格式 ： yyyy-MM-dd HH:mm:ss
     *
     * @param patternString 需要验证的字符串
     * @return 合法返回 true ; 不合法返回false
     */
    public static boolean isTimeLegal(String patternString) {

        Pattern a = compile("^((((1[6-9]|[2-9]\\d)\\d{2})-(0?[13578]|1[02])-(0?[1-9]|[12]\\d|3[01]))|(((1[6-9]|[2-9]\\d)\\d{2})-(0?[13456789]|1[012])-(0?[1-9]|[12]\\d|30))|(((1[6-9]|[2-9]\\d)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|(((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))-0?2-29-)) (20|21|22|23|[0-1]?\\d):[0-5]?\\d:[0-5]?\\d$");

        Matcher b = a.matcher(patternString);
        if (b.matches()) {
            return true;
        } else {
            return false;
        }
    }


    public static long get13Timestamp(int time) {
        String s = String.valueOf(time);
        if (s.length() == 10) {
            s = s + "000";
        }
        long t = Long.parseLong(s);
        return t;
    }

    public static Date getDate(long time) {
        String s = String.valueOf(time);
        if (s.length() == 10) {
            s = s + "000";
            long t = Long.parseLong(s);
            return new Date(t);
        } else {
            return new Date(time);
        }
    }


    //当前日期所在的分钟数
    public static final String hour24HMSPatternMinute = "yyyy-MM-dd HH:mm:00";// 年月日
    public static final String hour24HMSPatternMinuteEnd = "yyyy-MM-dd HH:mm:59";// 年月日

    //当前日期所在的小时数
    public static final String hour24HMSPatternHour = "yyyy-MM-dd HH:00:00";// 年月日
    public static final String hour24HMSPatternHourEnd = "yyyy-MM-dd HH:59:59";// 年月日

    //当前日期所在的天数
    public static final String hour24HMSPatternDay = "yyyy-MM-dd 00:00:00";// 年月日
    public static final String hour24HMSPatternDayEnd = "yyyy-MM-dd 23:59:59";// 年月日

    /**
     * 返回默认格式的当前时间戳字符串格式 所在的分钟数
     */
    public static String getCurrentTimeMinute() {
        return format(new Date(), hour24HMSPatternMinute);
    }

    public static String getCurrentTimeMinuteEnd() {
        return format(new Date(), hour24HMSPatternMinuteEnd);
    }

    /**
     * 返回默认格式的当前时间戳字符串格式 所在的小时数
     */
    public static String getCurrentTimeHour() {
        return format(new Date(), hour24HMSPatternHour);
    }

    public static String getCurrentTimeHourEnd() {
        return format(new Date(), hour24HMSPatternHourEnd);
    }

    /**
     * 返回默认格式的当前时间戳字符串格式 所在的天数
     */
    public static String getCurrentTimeDay() {
        return format(new Date(), hour24HMSPatternDay);
    }

    public static String getCurrentTimeDayEnd() {
        return format(new Date(), hour24HMSPatternDayEnd);
    }


    /**
     * 判断当前时间是否在开始时间和结束时间之内
     *
     * @param dateStart
     * @param dateEnd
     * @return
     * @throws ParseException
     */
    public static boolean compareTime(String dateStart, String dateEnd) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.hour24Pattern);
        Date dateS = null;
        Date currTime = null;
        Date dateE = null;
        try {
            dateS = simpleDateFormat.parse(dateStart);
            dateE = simpleDateFormat.parse(dateEnd);
            currTime = simpleDateFormat.parse(dateToStr(new Date(), 7));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currTime.compareTo(dateS) >= 0 && dateE.compareTo(dateE) <= 0 ? true : false;
    }

    /**
     * 比较当前时间是否大于指定时间 当前 > 指定 =true
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static boolean compareCurrTime(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.hour24HMSPattern);
        Date dateS = null;
        Date currTime = null;
        try {
            dateS = simpleDateFormat.parse(time);
            currTime = simpleDateFormat.parse(dateToStr(new Date(), 12));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currTime.compareTo(dateS) > 0 ? true : false;
    }

    /**
     * 当前时间增加参数分钟数后的时间数据
     *
     * @return
     */
    public static String getCurrTimeAddMinutes(int minutes) {
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.MINUTE, minutes);
        return sdf.format(nowTime.getTime());
    }

    /**
     * 获取当前时间前几分钟数（整点分钟数）
     *
     * @param minutes
     * @return
     */
    public static String getCurrTimeBeforeMinutes(int minutes) {
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.add(Calendar.MINUTE, -minutes);
        Date beforeD = beforeTime.getTime();
        String before5 = sdfMinute.format(beforeD);
        return before5;
    }

    /**
     * 获取当前时间的上一天数据
     *
     * @param time
     * @return
     */
    public static String getCurrLastDay(String time) {
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        int day = calendar.get(Calendar.DATE);
        //                      此处修改为+1则是获取后一天
        calendar.set(Calendar.DATE, day - 1);

        String lastDay = sdf.format(calendar.getTime());
        return lastDay;
    }


    public static void main(String[] args) {
//        Timestamp nowTime2 = Timestamp.valueOf(simpleDateFormat_date_time_line.format(new Date(System.currentTimeMillis() + 1000)));
//        System.out.println(nowTime2);
//        Timestamp nowTime3 = Timestamp.valueOf(simpleDateFormat_date_time_line.format(new Date(System.currentTimeMillis())));
//        System.out.println(nowTime3);
//        System.out.println(getCurrentTime());
//        System.out.println(getCurrentTimeLastSecond());
//        System.out.println(getCurrentTimeLastNSecond(100));

//        System.out.println(getCurrentTimeMinute());
//        System.out.println(getCurrentTimeMinuteEnd());
//        System.out.println(getCurrentTimeHour());
//        System.out.println(getCurrentTimeHourEnd());
//        System.out.println(getCurrentTimeDay());
        System.out.println(getCurrentTimeMinute());
//        System.out.println(getCurrentTime());

        System.out.println(getCurrLastDay("2020-01-01 19:00:01"));
    }

}