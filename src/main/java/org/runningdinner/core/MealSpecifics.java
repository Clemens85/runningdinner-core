package org.runningdinner.core;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Embeddable
public class MealSpecifics {

	private boolean lactose;

	private boolean gluten;

	private boolean vegetarian;

	private boolean vegan;

	private String note = StringUtils.EMPTY;
	
	public static MealSpecifics NONE = new MealSpecifics();
	
	public MealSpecifics(boolean lactose, boolean gluten, boolean vegetarian, boolean vegan, String note) {
		this.lactose = lactose;
		this.gluten = gluten;
		this.vegetarian = vegetarian;
		this.vegan = vegan;
		this.setNote(note);
	}

	public MealSpecifics() {
	}

	public boolean isLactose() {
		return lactose;
	}

	public void setLactose(boolean lactose) {
		this.lactose = lactose;
	}

	public boolean isGluten() {
		return gluten;
	}

	public void setGluten(boolean gluten) {
		this.gluten = gluten;
	}

	public boolean isVegetarian() {
		return vegetarian;
	}

	public void setVegetarian(boolean vegetarian) {
		this.vegetarian = vegetarian;
	}

	public boolean isVegan() {
		return vegan;
	}

	public void setVegan(boolean vegan) {
		this.vegan = vegan;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		if (note == null) {
			this.note = StringUtils.EMPTY;
		}
		else {
			this.note = StringUtils.trim(note);
		}
	}
	
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 113).append(isLactose()).append(isGluten()).append(isVegan()).append(isVegetarian()).append(
				getNote()).toHashCode();
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
		MealSpecifics other = (MealSpecifics)obj;
		return new EqualsBuilder().append(isGluten(), other.isGluten()).append(isLactose(), other.isLactose()).append(isVegan(),
				other.isVegan()).append(isVegetarian(), other.isVegetarian()).append(getNote(), other.getNote()).isEquals();
	}

	@Override
	public String toString() {
		return "lactose=" + lactose + ", gluten=" + gluten + ", vegetarian=" + vegetarian + ", vegan=" + vegan + ", notes=" + note;
	}
	
	
}
