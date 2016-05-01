package org.runningdinner.core;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.runningdinner.core.model.AbstractEntity;

/**
 * Represents a meal-type in a running diner like e.g. dessert.
 * 
 * @author Clemens Stich
 * 
 */
@Entity
@Access(AccessType.FIELD)
// Simple workaround for ignoring JPA basic entity attributes when serializing/deserializing to/from JSON Format.
// Currently this is sufficient, in future it may be more suitable to adapt the JSON serializer/deserializer API.
@JsonIgnoreProperties(value = { "id", "naturalKey", "versionNo", "createdAt", "modifiedAt", "new" })
public final class MealClass extends AbstractEntity {

	private static final long serialVersionUID = 8167694721190832584L;

	private String label;

	@Temporal(TemporalType.TIMESTAMP)
	private Date time;

	protected MealClass() {
		// JPA
	}

	public MealClass(String label) {
		this.label = label;
	}

	public MealClass(String label, Date time) {
		this.label = label;
		this.time = time;
	}

	/**
	 * Returns the name of this meal (the meal is actually identified by this name).
	 * 
	 * @return
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * The time for ingesting this meal.<br>
	 * This time is returned as a whole date, although the time is the actual important data.
	 * 
	 * @return
	 */
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
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

	// Convenience methods for standalone scenario

	public static MealClass APPETIZER() {
		return new MealClass("Vorspeise");
	}

	public static MealClass MAINCOURSE() {
		return new MealClass("Hauptgericht");
	}

	public static MealClass DESSERT() {
		return new MealClass("Nachspeise");
	}
		
}
