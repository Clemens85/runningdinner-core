package org.runningdinner.core.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateTimeUtil {

	private DateTimeUtil() {

	}

	public static boolean isSameDateAndTime(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return true;
		}
		if (date1 != null && date2 != null) {
			return date1.compareTo(date2) == 0;
		}
		return false;
	}

	public static Date addDaysToDate(Date date, int days) {
		Instant timestamp = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp, getGermanTimeZone());
		return Date.from(localDateTime.plusDays(days).atZone(getGermanTimeZone()).toInstant());
	}

	public static Date setTime(Date date, int hour, int minute) {
		Instant timestamp = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(timestamp, getGermanTimeZone());
		return Date.from(localDateTime.withHour(hour).withMinute(minute).atZone(getGermanTimeZone()).toInstant());
	}

	public static Date createDate(int year, int month, int dayOfMonth, int hour, int minute) {
		LocalDateTime result = LocalDateTime.of(year, month, dayOfMonth, hour, minute);
		return Date.from(result.atZone(getGermanTimeZone()).toInstant());
	}

	static ZoneId getGermanTimeZone() {
		return ZoneId.of("Europe/Berlin");
	}

	public static Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(getGermanTimeZone()).toInstant());
	}

}
