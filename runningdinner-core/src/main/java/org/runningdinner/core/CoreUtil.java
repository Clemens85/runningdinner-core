package org.runningdinner.core;

import java.util.Collection;

public class CoreUtil {

	public static <T> boolean isEmpty(final Collection<T> collection) {
		return (collection == null || collection.isEmpty());
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
}
