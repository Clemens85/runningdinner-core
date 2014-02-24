package org.runningdinner.core;

import java.io.Closeable;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.runningdinner.core.model.AbstractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains some helper method used throughout the whole project
 * 
 * @author Clemens Stich
 * 
 */
public class CoreUtil {

	public static final String DEFAULT_DATEFORMAT_PATTERN = "dd.MM.yyyy";

	public static String NEWLINE = System.getProperty("line.separator");

	private static final Logger LOGGER = LoggerFactory.getLogger(CoreUtil.class);

	public static <T> boolean isEmpty(final Collection<T> collection) {
		return (collection == null || collection.isEmpty());
	}

	/**
	 * Distributes all elements of the middle-collection equally to the left- and right-collection.<br>
	 * Ideally the left-size equals to right-size after method invocation.
	 * 
	 * @param left The "left"-side collection in which to distribute elements
	 * @param middle The collection from which to take the elements for distribution.
	 * @param right The "right"-side collection in which to distribute elements
	 */
	public static <T> void distributeEqually(final Collection<T> left, final Collection<T> middle, final Collection<T> right) {
		for (T m : middle) {
			if (left.size() < right.size()) {
				left.add(m);
			}
			else {
				right.add(m);
			}
		}
	}

	/**
	 * Returns a new set with all objects that were passed by theSet except the object exclude.<br>
	 * In other words: Result = theSet - exclude
	 * 
	 * @param exclude The object that shall be excluded
	 * @param theSet Original Set that contains all objects. This set will not be modified.
	 * @return New constructed set with the result of the subtraction
	 */
	public static <T> Set<T> excludeFromSet(final T exclude, final Set<T> theSet) {
		Set<T> result = new HashSet<T>();

		for (T obj : theSet) {
			if (!obj.equals(exclude)) {
				result.add(obj);
			}
		}

		return result;
	}

	/**
	 * Closes the passed stream safely.<br>
	 * If the stream is null or an exception occurs the method gracefully returns without an error.
	 * 
	 * @param stream
	 */
	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			}
			catch (IOException e) {
				LOGGER.error("Failed to close stream", e);
			}
		}
	}

	/**
	 * Asserts that the passed testNumber is smaller as the passed comparingValue.
	 * 
	 * @param testNumber The number which shall be checked
	 * @param comparingValue The reference-value for comparison
	 * @param message The error message that shall be wrapped into the exception if the assertion fails
	 * @throws IllegalArgumentException If assertion fails
	 */
	public static void assertSmaller(int testNumber, int comparingValue, String message) {
		if (!(testNumber < comparingValue)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Asserts that the passed testNumber is not negative.
	 * 
	 * @param testNumber The number which shall be checked
	 * @param message The error message that shall be wrapped into the exception if the assertion fails
	 * @throws IllegalArgumentException If assertion fails
	 */
	public static void assertNotNegative(int testNumber, String message) {
		if (testNumber < 0) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Asserts that the passed object is not null
	 * 
	 * @param obj The object to test for
	 * @param message The error message that shall be wrapped into the exception if the assertion fails
	 * @throws IllegalArgumentException If assertion fails
	 */
	public static void assertNotNull(Object obj, String message) {
		if (null == obj) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Throws an exception if the passed string is null or empty.
	 * 
	 * @param s The string to test for
	 * @param message The error message that shall be wrapped into the exception if the assertion fails
	 * @throws IllegalArgumentException If assertion fails
	 */
	public static void assertNotEmpty(final String s, String message) {
		if (StringUtils.isEmpty(s)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Asserts that the passed testNumber is smaller or equal as the passed comparingValue.
	 * 
	 * @param testNumber The number which shall be checked
	 * @param comparingValue The reference-value for comparison
	 * @param message The error message that shall be wrapped into the exception if the assertion fails
	 * @throws IllegalArgumentException If assertion fails
	 */
	public static void assertSmallerOrEq(int testNumber, int comparingValue, String message) {
		if (!(testNumber <= comparingValue)) {
			throw new IllegalArgumentException(message);
		}
	}

	/**
	 * Safely converts a string to a number <br>
	 * If the passed string cannot be converted the passed fallback is returned.
	 * 
	 * @param str The string to convert
	 * @param fallback Fallback to return
	 * @return
	 */
	public static int convertToNumber(String str, int fallback) {
		try {
			if (StringUtils.isEmpty(str)) {
				return fallback;
			}
			return Integer.valueOf(str.trim());
		}
		catch (NumberFormatException ex) {
			LOGGER.error("Could not parse string {} as number", str, ex);
			return fallback;
		}
	}

	/**
	 * Safely converts the passed String to a FuzzyBoolean.<br>
	 * Mainly this method checks for occurrence like "true" or "false" in order to return the appropriate FuzzyBoolean. It used however also
	 * other patterns like "yes" or "no" for checking.
	 * 
	 * @param str The string to convert
	 * @param fallback Fallback to return
	 * @return
	 */
	public static FuzzyBoolean convertToBoolean(String str, FuzzyBoolean fallback) {
		if (StringUtils.isEmpty(str)) {
			return fallback;
		}
		String boolStr = str.trim();
		if ("true".equalsIgnoreCase(boolStr) || "x".equalsIgnoreCase(boolStr) || "ja".equalsIgnoreCase(boolStr)
				|| "yes".equalsIgnoreCase(boolStr)) {
			return FuzzyBoolean.TRUE;
		}

		if ("false".equalsIgnoreCase(boolStr) || "no".equalsIgnoreCase(boolStr) || "o".equalsIgnoreCase(boolStr)
				|| "nein".equalsIgnoreCase(boolStr)) {
			return FuzzyBoolean.FALSE;
		}
		return fallback;
	}

	/**
	 * Safely returns a formatted date string.
	 * 
	 * @param time The date to format
	 * @param timeFormat The dateformat which shall be used for formatting
	 * @param fallback The fallback to return if the date cannot be formatted
	 * @return
	 */
	public static String getFormattedTime(final Date time, final DateFormat timeFormat, final String fallback) {
		if (time == null) {
			return fallback;
		}
		try {
			return timeFormat.format(time);
		}
		catch (Exception ex) {
			LOGGER.error("Failed to format time-string {}", time, ex);
			return fallback;
		}
	}

	/**
	 * Gets app-global default date format instance
	 * 
	 * @return
	 */
	public static DateFormat getDefaultDateFormat() {
		return new SimpleDateFormat(DEFAULT_DATEFORMAT_PATTERN);
	}

	/**
	 * Gets app-global default time format string
	 * 
	 * @return
	 */
	public static String getDefaultTimeFormat() {
		return "HH:mm";
	}

	public static <T extends AbstractEntity> List<String> getNaturalKeysForEntities(final Collection<T> entities) {
		List<String> result = new ArrayList<String>(entities.size());
		for (T entity : entities) {
			result.add(entity.getNaturalKey());
		}
		return result;
	}
}
