package org.run.date.util;



import org.run.base.asserts.StringUtil;
import org.run.base.exception.RRException;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
    /** 时间格式(yyyy-MM-dd) */
    public final static String DATE_PATTERN = "yyyy-MM-dd";
    /** 时间格式(yyyy-MM-dd HH:mm:ss) */
    public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** 时间格式(HH:mm:ss) */
    public final static String TIME_PATTERN = "HH:mm:ss";
    public final static String yyyy_MM_dd_HH_mm="yyyy-MM-dd HH:mm";
    /**
     * 设每一台手机的平均生产时间为10分钟
     *///GENERATION
    public final static Long GENERATION_TIME = 60 * 60 * 10 * 1000L;

    public final static String MORING_WORK_TIME = " 09:00:00";
    public final static String MORING_WORK_END_TIME = " 12:00:00";
    public final static String AFTERNOON_WORK_TIME = " 13:00:00";
    public final static String AFTERNOON_WORK_END_TIME = " 18:00:00";
    public final static Long FOUR_WORK_TIME = 60 * 60 * 4 * 1000L ;

    public final static Long ONE_HOUR = 60 * 60 * 1000L;
    public final static String HOUR_PATTERN = "HH";
    /**
     * 一天的工作时间
     */
    public final static Long ONE_DAY_WORK_TIME =  60 * 60 * 8 * 1000L;

    public static String format(Date date) {
        if (date==null){
            return "";
        }
        return format(date, DATE_PATTERN);
    }

    public static String format(Date date, String pattern) {
        if(date != null){
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    public static String format(String date,String pattern) {
        return DateUtil.format(DateUtil.parse(date), pattern);
    }

    public static String format(String date) {
        return DateUtil.format(DateUtil.parse(date), DATE_PATTERN);
    }

    public static Date parse(Date date,String pattern){
        String strDate=format(date,pattern);
        Date d= parse(strDate,pattern);
        return d;
    }
    public static Date parse(String strDate,String pattern){
        if(strDate==null||strDate.trim().length()==0) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date=null;
        try {
            date = sdf.parse(strDate);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 把string类型的日期转为Date类型，格式默认为yyyy-MM-dd
     * @param strDate 日期
     * @return Date类型的日期
     * @throws ParseException
     */
    public static Date parse(String strDate) {
        return parse(strDate,DATE_PATTERN);
    }
    /**
     * 返回当前时间
     */
    public static String getCurrentTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
        return sdf.format(d);
    }
    /**
     * 返回今天日期

     */
    public static String getCurrentDate() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(d);
    }

    public static Timestamp getCurrentTimstamp() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        return ts;
    }

    public static Date[] getStartAndEndDate(String startTime,String endTime) {
        if(!StringUtil.isEmpty(startTime) && !StringUtil.isEmpty(endTime)) {
            Date[] date = new Date[2];
            Date start =	parse(startTime);
            Date end =	DateUtil.parse(endTime);
            Long longTime =	end.getTime();
            longTime = longTime + (24 * 60 * 60 * 1000L) - 1;
            date[0] = start;
            date[1] = new Date(longTime);
            return date;
        }
        return null;
    }
    //获得某年某月的最后一天
    public static String getLastDayOfMonth(int year,int month){
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(cal.getTime())+" 23:59:59";
    }
    //获得末年某月的第一天
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));
        return new   SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime()) + " 00:00:00";
    }
    /**
     * 获得以本月为终点的12个月的时间段
     *
     * @return
     */
    public static String[][] getLastTwelveMonth(){
        Date date = new Date();
        Integer year = Integer.valueOf(format(date, "yyyy"));
        Integer month = Integer.valueOf(format(date, "MM"));
        String[][] months = new String[12][2];
        for(int i = 0 ; i < 12 ; i++) {
            if(month > 0) {
                String start = getFirstDayOfMonth(year,month);
                String end = getLastDayOfMonth(year, month);
                months[11-i][0] = start;
                months[11-i][1] = end;
                month -= 1;
            }else {
                year -= 1;
                month = 12;
                String start = getFirstDayOfMonth(year,month);
                String end = getLastDayOfMonth(year, month);
                months[11-i][0] = start;
                months[11-i][1] = end;
                month -= 1;
            }

        }
        return months;
    }

    /**
     * 获得某一年的所有月份的第一天和最后一天
     * @param year
     * @return
     */
    public static String[][] getYearMonth(Integer year){
        String[][] months = new String[12][2];
        if(year == null) year = Integer.valueOf(format(new Date(),"yyyy"));
        for(int i = 0 ; i < 12 ; i++) {
            months[i][0] =	getFirstDayOfMonth(year,i+1);
            months[i][1] =  getLastDayOfMonth(year,i+1);
        }
        return months;
    }
    public static String[][] getYearMonth(){
        return	getYearMonth(null);
    }

    //获得星期1
    private static Calendar  preWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // System.out.println("要计算日期为:" + sdf.format(cal.getTime())); // 输出要计算日期
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal;
    }
    /**
     * 获得星期天
     * @param date
     * @return
     */
    public static String getSunday(Date date) {
        Calendar cal =	preWeek(date);
        cal.add(Calendar.DATE, 6);
        return format(cal.getTime());
    }

    /**
     * 获得这个日期是周几
     * @param date
     * @return
     */
    public static Integer getWeek(String date) {
        Calendar cal = Calendar.getInstance();
        Date parse = parse(date, DATE_TIME_PATTERN);
        cal.setTime(parse);
        int i = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(i <= 0) {
            i = 7;
        }
        return i;
    }

    public static String getDifferentTime(String date,Integer different){
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(date));
        cal.add(Calendar.DAY_OF_MONTH, different);
        return  format(cal.getTime(), DATE_PATTERN)+ " 23:59:59";
    }

    /**
     * 获得本周的星期1
     * @param date
     * @return
     */
    public static String getMonday(Date date) {
        Calendar cal =	preWeek(date);
        return format(cal.getTime());
    }

    public static String getDateAdd(String date,int day,boolean isLast) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse(date));
        cal.add(Calendar.DAY_OF_MONTH, day);
        String dateTime = null;
        if(isLast) {
            dateTime = format(cal.getTime()) + " 23:59:59";
        }else {
            dateTime = format(cal.getTime()) + " 00:00:00";
        }
        return dateTime;
    }

    /**
     * 获得某一个月的开始时间和结束时间
     * @param month
     * @return
     */
    public static String[] getDate(Integer year,Integer month){
        if(month == null) {
            month = Integer.parseInt(format(new Date(),"MM"));
        }
        if(month < 1 || month > 12) {
            throw new RRException("月份错误.");
        }
        month -= 1;
        String[][] yearMonth = DateUtil.getYearMonth(year);

        return yearMonth[month];
    }
    /**
     * 获得某一个月所有周的开始时间和结束时间
     * @param month
     * @return
     */
    public static String[][] getDateMonthArea(Integer year,Integer month){
        int totalWeek = 0;
        String[] date = getDate(year,month);
        String startDate = date[0];
        Integer week = DateUtil.getWeek(startDate);
        Long dayDifference = DateUtil.getDayDifference(DateUtil.parse(startDate), DateUtil.parse(date[1],DateUtil.DATE_TIME_PATTERN));
        Long total = dayDifference + 1; //这个月一共有多少天.
        int different =  7 - week;
        if(week != 1) {
            total = total - different - 1; // 减去前面的第一个星期
            totalWeek += 1;
        }
        totalWeek += (total/7);
        if(total % 7 != 0) {
            totalWeek += 1;
        }

        String differentTime = DateUtil.getDifferentTime(startDate, different);

        String[][] dates = new String[totalWeek][2]; //一共有多少个星期,每个星期都有区间
        for(int i = 0 ; i < totalWeek ; i++) {
            String lastDate = null;
            if(i != 0) {
                lastDate = dates[i - 1][1];
            }
            if(i == 0) {
                dates[i][0] = startDate;
                dates[i][1] = differentTime;
            }else if(i == totalWeek - 1) {
                dates[i][0] =  DateUtil.getDateAdd(lastDate, 1, false);
                dates[i][1] = date[1];
            }else {
                dates[i][0] = DateUtil.getDateAdd(lastDate, 1, false);
                dates[i][1] = DateUtil.getDateAdd(lastDate, 7, true);
            }

        }

        return dates;
    }
    /**
     * 获取周几的日期
     * @param
     * @return
     */
    public static String getSomeDayWeek(int day) {
        if(day <= 0) return null;
        Calendar cal =	preWeek(new Date());
        cal.add(Calendar.DAY_OF_WEEK, day - 1);
        return format(cal.getTime());
    }
    /**
     * 获得一周的所有时间
     * @return
     */
    public static String[][] getWeekAllDay(){
        String[][] days = new String[7][2];
        for(int i = 1; i < 8 ; i++) {
            String strDate = 	getSomeDayWeek(i);
            days[i-1][0] = strDate + " 00:00:00";
            days[i-1][1] = strDate+" 23:59:59";
        }

        return days;
    }
    /**
     * 获得两个时间点之间的所有以天为单位的时间段
     * ex:
     * 0: 2019-10-10
     * 1: 2019-10-10 23:59:59
     * @param start
     * @param end
     * @return
     */
    public static String[][] getDayDifferenceAllDay(String start,String end){
    /*	String startNumber =  format(parse(start), "MM");
    	String endNumber =  format(parse(end), "MM");
    	Integer init = Integer.valueOf(startNumber) - Integer.valueOf(endNumber);
    	*/
        Date startDate =  parse(start, DateUtil.DATE_PATTERN);
        Date endDate =  parse(end, DateUtil.DATE_PATTERN);

        Long init =  getDayDifference(startDate,endDate);
        String[][] days = new String[Integer.valueOf(init.toString()) + 1][2];
        long oneDay = 24 * 60 * 60 * 1000L;
        String andOne = " 23:59:59";
        for(int i = 0 ; i < days.length ; i ++) {
            Long time = startDate.getTime() + (oneDay * i);
            days[i][0]  = format(new Date(time), DATE_PATTERN);
            days[i][1] = format(new Date(time), DATE_PATTERN) + andOne;
        }

        return days;
    }
    /**
     * 返回d2-d1的时间差,以天表示
     * @param d1
     * @param d2
     * @return
     */
    public static Long getDayDifference(Date d1,Date d2){
        String time1=DateUtil.format(d1,DATE_PATTERN);
        String time2=DateUtil.format(d2,DATE_PATTERN);
        SimpleDateFormat df = new SimpleDateFormat(DATE_PATTERN);
        try {
            long t1=df.parse(time1).getTime();
            long t2=df.parse(time2).getTime();
            long dif=t2-t1;
            if(dif<0){
                return null;
            }
            return dif/24/60/60/1000;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 获得两个时间的所有月份
     * @param startDate
     * @param endDate
     * @return
     */
    public static String[][] getDayDifferenceAllMonth(Date startDate,Date endDate){


        Integer startYear = Integer.valueOf(DateUtil.format(startDate, "yyyy"));
        Integer endYear = Integer.valueOf(DateUtil.format(endDate, "yyyy"));

        Integer startMonth = Integer.valueOf(DateUtil.format(startDate, "MM"));
        Integer endMonth = Integer.valueOf(DateUtil.format(endDate, "MM"));

        // 例如 : 2019 - 2020 年的情况
        Integer value = endYear - startYear;

        String[][] date = null;

        if(value != 0) { //跨年的
            List<Integer> years = new ArrayList<>();
            for(int i  = 0 ;  i <= value ; i++) { //获得所有的年
                years.add(startYear+i);
            }
            Integer firstYear = years.remove(0);  // 跨年时间的第一个年分份
            Integer lastYear = years.remove(years.size()-1); //跨年时间的最后一个年份
            int init = (years.size() * 12) + (12 - startMonth + 1) + endMonth; //总共有多少个月
            date = new String[init][2];
            int index = 0;//记录 数组放到哪个位置了
            String[][] firstYearMonth = DateUtil.getYearMonth(firstYear);
            //第一年的
            for(int i = 0 ; i <= 12-startMonth ; i ++) {
                date[index][0] = firstYearMonth[startMonth+i-1][0];
                date[index][1] = firstYearMonth[startMonth+i-1][1];
                index++;
            }
            for(Integer i : years) { //中间的跨年的
                String[][] midYearMonths =  DateUtil.getYearMonth(i);
                for(int j = 0 ; j <= midYearMonths.length ; j++) {
                    date[index][0] = midYearMonths[i][0];
                    date[index][1] = midYearMonths[i][1];
                    index++;
                }
            }
            //最后一年的
            String[][] lastYearMonth = DateUtil.getYearMonth(lastYear);
            for(int i = 0 ; i < endMonth ; i++) {
                date[index][0] = lastYearMonth[i][0];
                date[index][1] = lastYearMonth[i][1];
                index++;
            }

        }else {  //不跨年的
            //这时  要考虑到两个时间中间跨了几个月
            String[][] dates = DateUtil.getYearMonth(startYear);

            Integer init = endMonth - startMonth;
            date = new String[init+1][2];
            for(int i = 0 ; i <= init ; i ++) {
                date[i][0] = dates[startMonth+i-1][0];
                date[i][1] = dates[startMonth+i-1][1];
            }
        }
        return date;
    }

    public static String[][] getDayDifferenceAllMonth(String startDate,String endDate){
        Date startTime = DateUtil.parse(startDate, DateUtil.DATE_PATTERN);
        Date endTime = DateUtil.parse(endDate, DateUtil.DATE_PATTERN);
        return getDayDifferenceAllMonth(startTime,endTime);
    }

    /**
     * 获得现在是几点
     * @return
     */
    public static Integer getNowHour() {
        String hour = DateUtil.format(new Date(), "HH");
        return Integer.valueOf(hour);
    }


    /**
     * 登录时长
     * @return
     */
    public static Date loginTime() {
        long oneHour = 60 * 60 * 1000 * 12;
        return new Date(new Date().getTime() + oneHour);
    }

    public static Timestamp getWorkTime(Timestamp old, String workTime,Integer addDay) {
        Calendar calendar =  Calendar.getInstance();
        calendar.setTime(old);
        calendar.add(Calendar.DAY_OF_YEAR, addDay);
        return new Timestamp(DateUtil.parse(DateUtil.format(calendar.getTime(), DateUtil.DATE_PATTERN)+workTime,  DateUtil.DATE_TIME_PATTERN).getTime());
    }

    public static String getDateCode(String old) {
        String code = "";
        if(StringUtil.isEmpty(old)) { //说明是第一条
            code = "01";
        }else {
            String oldTime = old.substring(0, 8);
            String nowTime = format(new Date(), "yyyyMMdd");
            if(oldTime.equals(nowTime)) { //说明是今天的
                Integer newNumber = Integer.valueOf(old.substring(14)) + 1;
                if(newNumber < 10) {
                    code = "0"+newNumber;
                }else {
                    code = newNumber.toString();
                }
            }else {
                code = "01";
            }
        }
        return format(new Date(), "yyyyMMddHHmmss") + code;
    }
    /**
     * 获得今天结束的时间
     * ex: 2019-10-10 23:59:59
     * @return
     */
    public static Date getTodayEnd() {
        String date =  format(getTodayStart())+" 23:59:59";
        return parse(date, DATE_TIME_PATTERN);
    }
    /**
     * 获得今天开始的时间
     * ex: 2019-10-10 00:00:00
     * @return
     */
    public static Date getTodayStart() {
        return parse(new Date(),DATE_PATTERN);
    }
    /**
     * 以当前时间为例
     * -1 ：表示当前时间 大于下午 18点 ,小于明天早上的9点
     *  0：表示休息时间: 12 <= x <= 13
     *  1:表示当前时间是在早上的工作时间内 , 即： 9<= x < 12
     *  2: 表示当前时间是在下午的工作时间内 |  13 <= x < 16
     * @param time
     * @return
     */
    public static int isWorkingTime(Timestamp time) {
        Integer  x = nowHour(time);
        if(x < 9 || x >= 18) {
            return -1;
        }else if(9 <= x && x < 12) {
            return 1;
        }else if(12 == x) {
            return 0;
        }else if(13 <= x && x < 18) {
            return 2;
        }
        return 0;
    }

    public static int nowHour (Timestamp time) {
        return Integer.valueOf(format(new Date(time.getTime()), "HH"));
    }

    public static Timestamp addOneHour(Timestamp time) {
        return new Timestamp(time.getTime() + ONE_HOUR);
    }

    public static boolean getStartTime(Timestamp time) {

        return false;
    }
}
