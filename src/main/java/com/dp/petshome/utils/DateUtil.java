package com.dp.petshome.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

/**
 * @Description 时间工具类
 */
public class DateUtil {
	public static final String MINTIME = " 00:00:00";
	public static final String MAXTIME = " 23:59:59";

	public static final String LONGFMT19 = "yyyy-MM-dd HH:mm:ss";
	public static final String LONGFMT17 = "yyyyMMdd HH:mm:ss";
	public static final String LONGFMT16 = "yyyy-MM-dd HH:mm";

	public static final String SHORTFMTD10 = "yyyy-MM-dd";
	public static final String SHORTFMTD8 = "yyyyMMdd";
	public static final String SHORTFMTT8 = "HH:mm:ss";

	public static void main(String[] args) {
		Long dateStrToTimestamp = DateUtil.dateStrToTimestamp("2018-08-10 12:12", DateUtil.LONGFMT16);
		System.out.println(dateStrToTimestamp);
		String timestampToDateStr = DateUtil.timestampToDateStr(dateStrToTimestamp, "yyyy-MM-dd HH:mm");
		System.out.println(timestampToDateStr);
	}

	/**
	 * @Description 返回Long类型时间戳
	 */
	public static Long dateStrToTimestamp(String dateStr, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Long dateLong = date.getTime();
		return dateLong;
	}

	/**
	 * @Description Integer时间戳转String格式时间
	 */
	public static String timestampToDateStr(Long dateLong, String format) {
		Date date = new Date(dateLong);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		String dateStr = sdf.format(date);
		return dateStr;
	}

	/**
	 * @Description 获取SimpleDateFormat
	 */
	public static SimpleDateFormat getFormat(String pattern) {
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter;
	}

	/**
	 * @Description 獲取當前日期Date
	 */
	public static Date getNowDate(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = getFormat(format);
		String dateStr = sdf.format(date);

		ParsePosition pos = new ParsePosition(0);
		Date crtDate = sdf.parse(dateStr, pos);
		return crtDate;
	}

	/**
	 * @Description 獲取當前日期DateStr
	 */
	public static String getNowDateStr(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = getFormat(format);
		String dateStr = sdf.format(date);
		return dateStr;
	}

	/**
	 * @Description String格式转Date格式
	 */
	public static Date strToDate(String dateStr, String format) {
		SimpleDateFormat fmter = getFormat(format);
		ParsePosition parsePosition = new ParsePosition(0);
		Date date = fmter.parse(dateStr, parsePosition);
		return date;
	}

	/**
	 * @Description Date格式转String格式
	 */
	public static String dateToStr(Date date, String format) {
		String dateStr = "";
		if (date != null) {
			SimpleDateFormat sdf = getFormat(format);
			dateStr = sdf.format(date);
		}
		return dateStr;
	}

	/**
	 * @Description 日期格式转换
	 */
	public static String dateFormat(String dateStr, String format) {
		try {
			if (StringUtils.isNotBlank(dateStr)) {
				DateFormat df = DateFormat.getDateTimeInstance();
				Date d = df.parse(dateStr);
				dateStr = dateToStr(d, format);
				return dateStr;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * @Description 获取今天之前/之后的日期
	 */
	public static String getOffsetFromNow(int offset, String format) {
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (offset != 0)
			calendar.add(Calendar.DAY_OF_MONTH, offset);
		date = calendar.getTime();
		String dateStr = dateToStr(date, format);
		return dateStr;
	}

	/**
	 * @Description 得到现在小时
	 */
	public static String getHour() {
		Date date = new Date();
		SimpleDateFormat formatter = getFormat(LONGFMT19);
		String dateStr = formatter.format(date);
		String hour;
		hour = dateStr.substring(11, 13);
		return hour;
	}

	/**
	 * @Description 得到现在分钟
	 */
	public static String getMinute() {
		Date date = new Date();
		SimpleDateFormat sdf = getFormat(LONGFMT19);
		String dateStr = sdf.format(date);
		String min = dateStr.substring(14, 16);
		return min;
	}

	/**
	 * @Description 获取现在的時分(HH:mm)
	 */
	public static String getNowHHmm() {
		return DateUtil.getNowDateStr(DateUtil.SHORTFMTT8).substring(0, 5);
	}

	/**
	 * @Description 获取现在的時分秒(HH:mm:ss)
	 */
	public static String getNowHHmmss() {
		return getNowDateStr(SHORTFMTT8).substring(0, 8);// HH:mm
	}

	/**
	 * @Description 判断是否润年
	 */
	public static boolean isLeapYear(String dateStr) {
		/**
		 * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
		 * 4.能被4整除同时能被100整除则不是闰年
		 */
		Date date = strToDate(dateStr, SHORTFMTD10);
		GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
		gc.setTime(date);
		int year = gc.get(Calendar.YEAR);
		if ((year % 400) == 0)
			return true;
		else if ((year % 4) == 0) {
			if ((year % 100) == 0)
				return false;
			else
				return true;
		} else
			return false;
	}

	/**
	 * @Description 获取一个月的最后一天
	 */
	public static String getLastDateOfMonth(String date) {
		String str = date.substring(0, 8);
		String month = date.substring(5, 7);
		int mon = Integer.parseInt(month);
		if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8 || mon == 10 || mon == 12) {
			str += "31";
		} else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
			str += "30";
		} else {
			if (isLeapYear(date)) {
				str += "29";
			} else {
				str += "28";
			}
		}
		return str;
	}

	/**
	 * @Description 当前时间所在的年度是第几周
	 */
	public static String getSeqWeek() {
		Calendar c = Calendar.getInstance(Locale.CHINA);
		String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
		if (week.length() == 1)
			week = "0" + week;
		String year = Integer.toString(c.get(Calendar.YEAR));
		return year + week;
	}

	/**
	 * @Description 返回星期几
	 */
	public static String getWeek(String dateStr) {
		// 再转换为时间
		Date date = DateUtil.strToDate(dateStr, SHORTFMTD10);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour中存的就是星期几了，其范围 1~7
		// 1=星期日 7=星期六，其他类推
		return new SimpleDateFormat("EEEE").format(calendar.getTime());
	}

	/**
	 * @Description 获取两个时间之间的天数
	 */
	public static long getDaysBetween(String date1, String date2) {
		if (date1 == null || "".equals(date1))
			date1 = getNowDateStr(SHORTFMTD10);
		if (date2 == null || "".equals(date2))
			return 0;
		long day = 0;
		try {
			// 转换为标准时间
			SimpleDateFormat myFormatter = getFormat(SHORTFMTD10);
			java.util.Date d1 = myFormatter.parse(date1);
			java.util.Date d2 = myFormatter.parse(date2);
			day = (d1.getTime() - d2.getTime()) / (24 * 60 * 60 * 1000);
		} catch (Exception e) {
			day = 0;
		}
		return day;
	}

	/**
	 * @Description 获取两个时间之间的小时数
	 */
	public static long getHoursBetween(String date1, String date2) {
		if (date1 == null || "".equals(date1))
			date1 = getNowDateStr(LONGFMT19);
		if (date2 == null || "".equals(date2))
			return 0;
		long min = 0;
		try {
			// 转换为标准时间
			SimpleDateFormat myFormatter = getFormat(LONGFMT19);
			java.util.Date d1 = myFormatter.parse(date1);
			java.util.Date d2 = myFormatter.parse(date2);
			min = (d1.getTime() - d2.getTime()) / (1000 * 60 * 60);
		} catch (Exception e) {
			min = 0;
		}
		return min;
	}

	/**
	 * @Description 获取两个时间之间的分钟数
	 */
	public static long getMinutesBetween(String date1, String date2) {
		if (date1 == null || "".equals(date1))
			date1 = getNowDateStr(LONGFMT19);
		if (date2 == null || "".equals(date2))
			return 0;
		long min = 0;
		try {
			// 转换为标准时间
			SimpleDateFormat myFormatter = getFormat(LONGFMT19);
			java.util.Date d1 = myFormatter.parse(date1);
			java.util.Date d2 = myFormatter.parse(date2);
			min = (d1.getTime() - d2.getTime()) / (1000 * 60);
		} catch (Exception e) {
			min = 0;
		}
		return min;
	}

	/**
	 * @Description 获取两个时间之间的秒钟数
	 */
	public static long getSecondsBetween(String date1, String date2) {
		if (date1 == null || "".equals(date1))
			date1 = getNowDateStr(LONGFMT19);
		if (date2 == null || "".equals(date2))
			return 0;
		long scd = 0;
		try {
			// 转换为标准时间
			SimpleDateFormat myFormatter = getFormat(LONGFMT19);
			java.util.Date d1 = myFormatter.parse(date1);
			java.util.Date d2 = myFormatter.parse(date2);
			scd = (d1.getTime() - d2.getTime()) / 1000;
		} catch (Exception e) {
			scd = 0;
		}
		return scd;
	}

}
