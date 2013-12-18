package org.runningdinner.core;

import java.util.Collection;

public class CoreUtil {

	public static String NEWLINE = System.getProperty("line.separator");

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

	public static void assertNotNull(MealClass mealClass, String message) {
		if (null == mealClass) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void assertSmallerOrEq(int testNumber, int comparingValue, String message) {
		if (!(testNumber <= comparingValue)) {
			throw new IllegalArgumentException(message);
		}
	}
}
