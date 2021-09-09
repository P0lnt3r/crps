package zy.pointer.crps.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	
	static SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String getCurrentTime(){
		return format.format(new Date());
	}
	
	public static String getDateString( Date date ){
		return format.format(date);
	}
	
	public static Date parse( String time ){
		try {
			return format.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getDateString( Date date , String format ){
		SimpleDateFormat _format = new SimpleDateFormat(format);
		return _format.format(date);
	}
	
	public static String getDateString( LocalDateTime time ){
		return time.format(formatter);
	}
	
	public static LocalDateTime format( String time ){
		return LocalDateTime.parse(time , formatter);
	}
	
	public static Date getSimpleDate(String date){
		try {
			return date_format.parse(date);
		} catch (Exception e) {
			return new Date();
		}
	}

	public static Date getDateTime(String date){
		try {
			return format.parse(date);
		} catch (Exception e) {
			return new Date();
		}
	}

	/**
	 * 获取YEAR
	 * @return
	 */
	public static String getYear(){
		Calendar c = Calendar.getInstance();
		String year = c.get(Calendar.YEAR)+"";
		year = year.substring(year.length()-1);
		return year;
	}

	/**
	 * 获取 MM-DD
	 * @return
	 */
	public static String getDate(){
		Calendar c = Calendar.getInstance();
		String month = "";
		String day = "";
		int _month = c.get(Calendar.MONTH) + 1;
		if( _month < 10 ){
			month = "0" + _month;
		}else month = "" + _month;
		int _day = c.get( Calendar.DATE );
		if( _day < 10 ){
			day = "0" + _day;
		}else day = "" + _day;
		return month + day;
	}
	
	public static void main(String[] args) throws Exception {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		System.out.println( sdf.parse("2020/7/2 14:14") );
		
	}
	
}
