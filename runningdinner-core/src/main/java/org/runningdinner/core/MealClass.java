package org.runningdinner.core;

public final class MealClass {

	public static MealClass APPETIZER = new MealClass("Vorspeise");
	public static MealClass MAINCOURSE = new MealClass("Hauptgericht");
	public static MealClass DESSERT = new MealClass("Nachspeise");

	private String label;

	public MealClass(String label) {
		super();
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MealClass other = (MealClass)obj;
		if (label == null) {
			if (other.label != null)
				return false;
		}
		else if (!label.equals(other.label))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MealClass [label=" + label + "]";
	}

}
