package org.runningdinner.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
