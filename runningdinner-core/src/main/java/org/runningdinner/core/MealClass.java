package org.runningdinner.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

// TODO: Klasse muss trotzdem immer mit equals verglichen werden!

public final class MealClass {

	public static MealClass APPETIZER = new MealClass("Vorspeise");
	public static MealClass MAINCOURSE = new MealClass("Hauptgericht");
	public static MealClass DESSERT = new MealClass("Nachspeise");

	private String label;

	private int order; // TODO: Implement this for having appetizer -> MainCourse -> Dessert

	public MealClass(String label) {
		super();
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 11).append(getLabel()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		MealClass other = (MealClass)obj;
		return new EqualsBuilder().append(getLabel(), other.getLabel()).isEquals();
	}

	@Override
	public String toString() {
		return label;
	}

}
