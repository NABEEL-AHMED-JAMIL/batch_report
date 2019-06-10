package com.ballistic.batch_report.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DateUtil {

	private final String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

	public Date dateParser(Date date) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
		String dateStr = simpleDateFormat.format(date);
		Date date1 = simpleDateFormat.parse(dateStr);
		return date1;
	}

	public Date getDateInTimeZone(Date currentDate, String timeZoneId) {
		Calendar mbCal = new GregorianCalendar(TimeZone.getTimeZone(timeZoneId));
		mbCal.setTimeInMillis(currentDate.getTime());
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, mbCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, mbCal.get(Calendar.MONTH));
		cal.set(Calendar.DAY_OF_MONTH, mbCal.get(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, mbCal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, mbCal.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, mbCal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, mbCal.get(Calendar.MILLISECOND));

		return cal.getTime();
	}
	
	public Date getDateForPreviousDay(Date currentDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	public Date getDateForNextDay(Date currentDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.DATE, +1);
		return cal.getTime();
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * **
	 * Get the date+time 1 hr back from the current-date-time *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * **/
	public Date getDateTimeFromCurrentDateTime() {
		Date currentDate = new Date();
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(currentDate); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, -1); // - one hour
		return cal.getTime();
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * Get the date+time with 5 mint next fro the current-date+time  *
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public Date getDateTime(Date currentDate) {
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(currentDate); // sets calendar time/date
		cal.add(Calendar.MINUTE, 15); // 15 add
		return cal.getTime();
	}

	public Boolean isDateValid(Date startDate, Date endDate) {
		Boolean isValid = false;
		if((startDate != null && endDate != null) && (startDate.before(endDate) || startDate.equals(endDate))) {
			isValid = true;
		}
		return isValid;
	}

	public Boolean isDateValidEqual(Date startDate, Date endDate) {
		Boolean isValid = false;
		if((startDate != null && endDate != null) && startDate.equals(endDate)) { isValid = true; }
		return isValid;
	}

	public static void main(String args[]) {
		DateUtil dateUtil = new DateUtil();
		Date currentDateTime = new Date(); // current date+time
		Date previousDateTime = dateUtil.getDateTimeFromCurrentDateTime(); // previous date+time
		System.out.println("Current Date + Time :- " + currentDateTime);
		while (!dateUtil.isDateValidEqual(previousDateTime, currentDateTime)) {
			Date previousDateTimeIncrement = dateUtil.getDateTime(previousDateTime);
			System.out.println("Previous Date " + previousDateTime + " Increment " + previousDateTimeIncrement);
			previousDateTime = dateUtil.getDateTime(previousDateTime);
		}
	}
}
