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

@Entity
@Access(AccessType.FIELD)
// Ignore JPA basic entity attributes, as we don't need them currently
// TODO: Currently this is sufficient, if we need different cases, then the JSON serializer/deserializer must be adapted
@JsonIgnoreProperties(value = { "id", "naturalKey", "versionNo", "createdAt", "modifiedAt", "new" })
public final class MealClass extends AbstractEntity {

	private static final long serialVersionUID = 8167694721190832584L;

	public static MealClass APPETIZER = new MealClass("Vorspeise");
	public static MealClass MAINCOURSE = new MealClass("Hauptgericht");
	public static MealClass DESSERT = new MealClass("Nachspeise");

	private String label;

	@Temporal(TemporalType.TIMESTAMP)
	private Date time;

	protected MealClass() {
	}

	public MealClass(String label) {
		super();
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

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

}
