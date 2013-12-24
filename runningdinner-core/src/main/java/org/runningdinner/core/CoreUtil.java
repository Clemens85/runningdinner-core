package org.runningdinner.core;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoreUtil {

	public static String NEWLINE = System.getProperty("line.separator");

	private static final Logger LOGGER = LoggerFactory.getLogger(CoreUtil.class);

	public static <T> boolean isEmpty(final Collection<T> collection) {
		return (collection == null || collection.isEmpty());
	}

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
	 * @param exclude
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

	public static void assertSmaller(int testNumber, int comparingValue, String message) {
		if (!(testNumber < comparingValue)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void assertNotNegative(int a, String message) {
		if (a < 0) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void assertNotNull(Object obj, String message) {
		if (null == obj) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void assertNotEmpty(final String s, String message) {
		if (StringUtils.isEmpty(s)) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void assertSmallerOrEq(int testNumber, int comparingValue, String message) {
		if (!(testNumber <= comparingValue)) {
			throw new IllegalArgumentException(message);
		}
	}

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
}
