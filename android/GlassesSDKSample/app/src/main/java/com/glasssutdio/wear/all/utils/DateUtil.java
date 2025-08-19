package com.glasssutdio.wear.all.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class DateUtil {
    public static long Hour_S_Min = 3600;
    public static final String dFyyyyMMdd1 = "yyyy-MM-dd";
    public static final String yyyyMMdd_HHmm = "yyyy-MM-dd HH:mm";
    public static final String yyyyMMdd_HHmmss = "yyyy-MM-dd HH:mm:ss";

    /* renamed from: c */
    private Calendar f168c;
    public static final Locale localeObject = new Locale("en");
    private static final ThreadLocal<SimpleDateFormat> dFMMdd = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd", DateUtil.localeObject);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dFMMdd_HHmm = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.2
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MM-dd HH:mm", DateUtil.localeObject);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dFyyyyMM = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.3
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM", DateUtil.localeObject);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dFyyyyMMdd = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.4
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", DateUtil.localeObject);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dFyyyyMMdd_HHmm = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.5
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", DateUtil.localeObject);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dFyyyyMMdd_HHmmss = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.6
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", DateUtil.localeObject);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dFHHmm = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.7
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm", DateUtil.localeObject);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dFHHmmss = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.8
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss", DateUtil.localeObject);
        }
    };
    private static final ThreadLocal<SimpleDateFormat> dFSyyyyMMdd = new ThreadLocal<SimpleDateFormat>() { // from class: com.glasssutdio.wear.all.utils.DateUtil.9
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd", DateUtil.localeObject);
        }
    };
    public static SimpleDateFormat yyyyMMdd_HHmmssF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat dFyyyyMMddF = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat dFyyyyMMddmmF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public enum DateFormater {
        MMdd,
        MMdd_HHmm,
        yyyyMM,
        yyyyMMdd,
        yyyyMMdd_HHmm,
        yyyyMMdd_HHmmss,
        HHmm,
        HHmmss,
        yyyyMMddHHmm,
        SyyyyMMdd,
        dFyyyyMMdd,
        dFHHmm
    }

    public long getZeroTime() {
        return new DateUtil(getYear(), getMonth(), getDay()).getUnixTimestamp();
    }

    public String getZeroTimeYyyyMMdd_HHmmssDate() {
        return new DateUtil(getYear(), getMonth(), getDay()).getYyyyMMdd_HHmmssDate();
    }

    public long getZeroTime1() {
        return new DateUtil(getYear(), getMonth(), getDay()).getTimestamp();
    }

    public static long getFirstDayMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, 0);
        calendar.set(5, 1);
        return calendar.getTimeInMillis();
    }

    public static long getLastDayMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(2, 0);
        calendar.set(5, calendar.getActualMaximum(5));
        return calendar.getTimeInMillis();
    }

    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(5);
    }

    public int getTodayMin() {
        return Math.round((this.f168c.getTimeInMillis() - getZeroTime1()) / 60000) + 1;
    }

    public int getTodayMinNoPlus() {
        return Math.round((this.f168c.getTimeInMillis() - getZeroTime1()) / 60000);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar.get(1) == calendar2.get(1) && calendar.get(2) == calendar2.get(2) && calendar.get(5) == calendar2.get(5);
    }

    public boolean isSameDay(long compare_time, boolean isUnix) {
        DateUtil dateUtil = new DateUtil(compare_time, isUnix);
        return dateUtil.getYear() == getYear() && dateUtil.getMonth() == getMonth() && dateUtil.getDay() == getDay();
    }

    public static boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp1);
        calendar2.setTimeInMillis(timestamp2);
        return calendar.get(1) == calendar2.get(1) && calendar.get(2) == calendar2.get(2) && calendar.get(5) == calendar2.get(5);
    }

    public static boolean isWithin7Days(long timestamp1, long timestamp2) {
        return Math.abs(timestamp1 - timestamp2) <= 604800000;
    }

    public static boolean isSameMonth(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        return calendar.get(1) == calendar2.get(1) && calendar.get(2) == calendar2.get(2);
    }

    public static long getPreOrNextTimeByDay(long marignSize) {
        return System.currentTimeMillis() - (marignSize * 86400000);
    }

    public static long getGMTDate(long record_date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String str = simpleDateFormat.format(new Date(1000 * record_date));
            return new DateUtil(Integer.parseInt(str.substring(0, 4)), Integer.parseInt(str.substring(5, 7)), Integer.parseInt(str.substring(8, 10)), Integer.parseInt(str.substring(11, 13)), Integer.parseInt(str.substring(14, 16)), 0).getUnixTimestamp();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            DateUtil dateUtil = new DateUtil(record_date, true);
            dateUtil.setHour(0);
            dateUtil.setMinute(0);
            dateUtil.setSecond(0);
            return dateUtil.getUnixTimestamp();
        }
    }

    public static DateUtil valueOf(String sdate) {
        Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}");
        try {
            if (Pattern.compile("[0-9]{2}-[0-9]{2}").matcher(sdate).matches()) {
                return parse(sdate, DateFormater.MMdd);
            }
            if (Pattern.compile("[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}").matcher(sdate).matches()) {
                return parse(sdate, DateFormater.MMdd_HHmm);
            }
            if (Pattern.compile("[0-9]{4}-[0-9]{2}").matcher(sdate).matches()) {
                return parse(sdate, DateFormater.yyyyMM);
            }
            if (Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}").matcher(sdate).matches()) {
                return parse(sdate, DateFormater.yyyyMMdd);
            }
            if (Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}").matcher(sdate).matches()) {
                return parse(sdate, DateFormater.yyyyMMdd_HHmm);
            }
            if (Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}").matcher(sdate).matches()) {
                return parse(sdate, DateFormater.yyyyMMdd_HHmmss);
            }
            if (Pattern.compile("[0-9]{2}:[0-9]{2}").matcher(sdate).matches()) {
                return parse(sdate, DateFormater.HHmm);
            }
            if (Pattern.compile("[0-9]{2}:[0-9]{2}:[0-9]{2}").matcher(sdate).matches()) {
                return parse(sdate, DateFormater.HHmmss);
            }
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DateUtil parse(String sdate, DateFormater formater) throws ParseException {
        Date date;
        int iOrdinal = formater.ordinal();
        if (iOrdinal != 10) {
            switch (iOrdinal) {
                case 0:
                    date = dFMMdd.get().parse(sdate);
                    break;
                case 1:
                    date = dFMMdd_HHmm.get().parse(sdate);
                    break;
                case 2:
                    date = dFyyyyMM.get().parse(sdate);
                    break;
                case 3:
                    date = dFSyyyyMMdd.get().parse(sdate);
                    break;
                case 4:
                    date = dFyyyyMMdd_HHmm.get().parse(sdate);
                    break;
                case 5:
                    date = dFyyyyMMdd_HHmmss.get().parse(sdate);
                    break;
                case 6:
                    date = dFHHmm.get().parse(sdate);
                    break;
                case 7:
                    date = dFHHmmss.get().parse(sdate);
                    break;
                default:
                    date = null;
                    break;
            }
        } else {
            date = dFyyyyMMdd.get().parse(sdate);
        }
        return new DateUtil(date);
    }

    public DateUtil() {
        this.f168c = Calendar.getInstance();
    }

    public DateUtil(long timestamp, boolean isUnix) {
        Calendar calendar = Calendar.getInstance();
        this.f168c = calendar;
        if (isUnix) {
            calendar.setTimeInMillis(timestamp * 1000);
        } else {
            calendar.setTimeInMillis(timestamp);
        }
    }

    public DateUtil(Date date) {
        Calendar calendar = Calendar.getInstance();
        this.f168c = calendar;
        calendar.setTime(date);
    }

    public DateUtil(int year, int month, int day) {
        this(year, month, day, 0, 0, 0);
    }

    public DateUtil(int year, int month, int day, int hour, int minute) {
        this(year, month, day, hour, minute, 0);
    }

    public DateUtil(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        this.f168c = calendar;
        calendar.set(1, year);
        this.f168c.set(2, month - 1);
        this.f168c.set(5, day);
        this.f168c.set(11, hour);
        this.f168c.set(12, minute);
        this.f168c.set(13, second);
    }

    public DateUtil(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        this.f168c = calendar;
        calendar.set(11, hour);
        this.f168c.set(12, minute);
    }

    public boolean isToday() {
        DateUtil dateUtil = new DateUtil();
        return getYear() == dateUtil.getYear() && getMonth() == dateUtil.getMonth() && getDay() == dateUtil.getDay();
    }

    public boolean isSameWeek(int number) {
        return number == new DateUtil(new Date()).getWeekOfYear();
    }

    public boolean isSameMonth(int month, int year) {
        return month == getMonth() && getYear() == year;
    }

    public int daysBetweenMe(DateUtil dateUtil) {
        return (int) (Math.abs(getUnixTimestamp() - dateUtil.getUnixTimestamp()) / 86400);
    }

    public Date toDate() {
        return this.f168c.getTime();
    }

    public String toFormatString(DateFormater formater) {
        Date date = toDate();
        switch (formater) {
            case MMdd:
                return dFMMdd.get().format(date);
            case MMdd_HHmm:
                return dFMMdd_HHmm.get().format(date);
            case yyyyMM:
                return dFyyyyMM.get().format(date);
            case yyyyMMdd:
                return dFyyyyMMdd.get().format(date);
            case yyyyMMdd_HHmm:
                return dFyyyyMMdd_HHmm.get().format(date);
            case yyyyMMdd_HHmmss:
                return dFyyyyMMdd_HHmmss.get().format(date);
            case HHmm:
                return dFHHmm.get().format(date);
            case HHmmss:
                return dFHHmmss.get().format(date);
            case yyyyMMddHHmm:
            default:
                return "Unknown";
            case SyyyyMMdd:
                return dFSyyyyMMdd.get().format(date);
            case dFyyyyMMdd:
                return dFyyyyMMdd.get().format(date);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0035  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static Date String2Date(String formater, String dateString) {
        char c;
        try {
            int iHashCode = formater.hashCode();
            if (iHashCode != -1172057030) {
                if (iHashCode != -159776256) {
                    c = (iHashCode == 1333195168 && formater.equals("yyyy-MM-dd HH:mm:ss")) ? (char) 0 : (char) 65535;
                } else if (formater.equals("yyyy-MM-dd")) {
                    c = 2;
                }
            } else if (formater.equals("yyyy-MM-dd HH:mm")) {
                c = 1;
            }
            if (c == 0) {
                return yyyyMMdd_HHmmssF.parse(dateString);
            }
            if (c == 1) {
                return dFyyyyMMddmmF.parse(dateString);
            }
            if (c != 2) {
                return null;
            }
            return dFyyyyMMddF.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getMMddDate() {
        return toFormatString(DateFormater.MMdd);
    }

    public String getMMdd_HHmmDate() {
        return toFormatString(DateFormater.MMdd_HHmm);
    }

    public String getY_M_D() {
        return toFormatString(DateFormater.dFyyyyMMdd);
    }

    public String getY_M_D_H_M_S() {
        return toFormatString(DateFormater.yyyyMMdd_HHmmss);
    }

    public String getY_M_D_H_M() {
        return toFormatString(DateFormater.yyyyMMdd_HHmm);
    }

    public String getYyyyMMDate() {
        return toFormatString(DateFormater.yyyyMM);
    }

    public String getYyyyMMddDate() {
        return toFormatString(DateFormater.yyyyMMdd);
    }

    public String getYyyyMMdd_HHmmDate() {
        return toFormatString(DateFormater.yyyyMMdd_HHmm);
    }

    public String getYyyyMMdd_HHmmssDate() {
        return toFormatString(DateFormater.yyyyMMdd_HHmmss);
    }

    public String getHHmmDate() {
        return toFormatString(DateFormater.HHmm);
    }

    public String getHHmmssDate() {
        return toFormatString(DateFormater.HHmmss);
    }

    public String getSyyyyMMddDate() {
        return toFormatString(DateFormater.SyyyyMMdd);
    }

    public String getyyyyMMddDate() {
        return toFormatString(DateFormater.yyyyMMdd);
    }

    public int getYear() {
        return this.f168c.get(1);
    }

    public void setYear(int year) {
        this.f168c.set(1, year);
    }

    public int getMonth() {
        return this.f168c.get(2) + 1;
    }

    public void setMonth(int month) {
        this.f168c.set(2, month - 1);
    }

    public int getDay() {
        return this.f168c.get(5);
    }

    public int getDaysOfThisMonth() {
        return this.f168c.get(5);
    }

    public void setDay(int day) {
        this.f168c.set(5, day);
    }

    public void addDay(int day) {
        this.f168c.add(5, day);
    }

    public void addMonth(int month) {
        this.f168c.add(2, month);
    }

    public int getHour() {
        return this.f168c.get(11);
    }

    public void setHour(int hour) {
        this.f168c.set(11, hour);
    }

    public int getMinute() {
        return this.f168c.get(12);
    }

    public void setMinute(int minute) {
        this.f168c.set(12, minute);
    }

    public int getSecond() {
        return this.f168c.get(13);
    }

    public void setSecond(int second) {
        this.f168c.set(13, second);
    }

    public long getTimestamp() {
        return this.f168c.getTimeInMillis();
    }

    public void setTimestamp(long timestamp) {
        this.f168c.setTimeInMillis(timestamp);
    }

    public long getUnixTimestamp() {
        return this.f168c.getTimeInMillis() / 1000;
    }

    public void setUnixTimestamp(long unix_timestamp) {
        this.f168c.setTimeInMillis(unix_timestamp * 1000);
    }

    public int getDayOfWeek() {
        return this.f168c.get(7);
    }

    public int getWeekOfYear() {
        return this.f168c.get(3);
    }

    public int getWeekOfMonth() {
        return this.f168c.get(4);
    }

    public String getMonDate() {
        int dayOfWeek = getDayOfWeek();
        Calendar calendar = this.f168c;
        calendar.add(5, calendar.getFirstDayOfWeek() - dayOfWeek);
        return new SimpleDateFormat("yyyyMMdd").format(this.f168c.getTime());
    }

    public String toString() {
        return getYyyyMMdd_HHmmssDate();
    }

    public static String getTime(long time) {
        long jCurrentTimeMillis = (System.currentTimeMillis() - time) / 86400000;
        DateUtil dateUtil = new DateUtil(time, false);
        if (jCurrentTimeMillis > 0) {
            return dateUtil.getYyyyMMddDate();
        }
        return dateUtil.getHHmmDate();
    }

    public static long getSunDayTimeFromWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar.getTime().getTime() - ((calendar.get(7) - 1) * 86400000);
    }

    public static Date getDateByWeekMagin(int size) {
        return new Date(getSunDayTimeFromWeek() + (size * 86400000));
    }

    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return (int) ((date2.getTime() - date1.getTime()) / 86400000);
    }

    public static long dateStr2Stamp(String date) {
        try {
            return Long.parseLong(String.valueOf(new SimpleDateFormat("yyyyMMdd").parse(date).getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static long dateY_M_D2Stamp(String date) {
        try {
            return Long.parseLong(String.valueOf(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static int dateY_M_D2StampSecond(String date) {
        try {
            return (int) (Long.parseLong(String.valueOf(new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime())) / 1000);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static String getMarginMin(long start, long startTime) {
        return ((start - startTime) / 60) + "";
    }

    public static int getAgeByBirthday(Date birthday) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.before(birthday)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }
        int i = calendar.get(1);
        int i2 = calendar.get(2) + 1;
        int i3 = calendar.get(5);
        calendar.setTime(birthday);
        int i4 = calendar.get(1);
        int i5 = calendar.get(2) + 1;
        int i6 = i - i4;
        return i2 <= i5 ? (i2 != i5 || i3 < calendar.get(5)) ? i6 - 1 : i6 : i6;
    }
}
