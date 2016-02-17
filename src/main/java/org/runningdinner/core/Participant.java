package org.runningdinner.core;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.runningdinner.core.model.AbstractEntity;

/**
 * Represents a participant of a running dinner.<br>
 * Each participant is identified by his participantNumber which is unique inside <b>one</b> running-dinner.
 * The participant number is also used for comparing participants and is thus also used for giving participants with a lower number a higher
 * precedence when they are assigned into teams.
 * 
 * @author Clemens Stich
 * 
 */
@Entity
@Access(AccessType.FIELD)
public class Participant extends AbstractEntity implements Comparable<Participant> {

	private static final long serialVersionUID = -8062709434676386371L;

	/**
	 * Number which represents an undefined (=unknown) number of seats of a participant
	 */
	public static final int UNDEFINED_SEATS = -1;

	/**
	 * Number which represents an undefined (=unknown) age of a participant
	 */
	public static final int UNDEFINED_AGE = -1;

	@Column(nullable = false)
	private int participantNumber;

	@Embedded
	private ParticipantName name;

	@Embedded
	private ParticipantAddress address;

	private String email;

	private String mobileNumber;

	private Gender gender;

	private int age;

	private int numSeats;

	private boolean host;

	@Embedded
	@AttributeOverride(name = "notes", column = @Column(name = "mealspecificsnote"))
	private MealSpecifics mealSpecifics;

	protected Participant() {
		// JPA
	}

	/**
	 * Constructs a new participant with his participant-number.<br>
	 * 
	 * @param participantNumber
	 */
	public Participant(final int participantNumber) {
		this.participantNumber = participantNumber;
		this.numSeats = UNDEFINED_SEATS;
	}

	/**
	 * Returns the name of a participant
	 * 
	 * @return Participant's name. Is never null.
	 */
	public ParticipantName getName() {
		return name;
	}

	public void setName(ParticipantName name) {
		if (name == null) {
			throw new NullPointerException("Null value for name is not allowed!");
		}
		this.name = name;
	}

	/**
	 * Returns the address of a participant
	 * 
	 * @return Participant's address. Is never null.
	 */
	public ParticipantAddress getAddress() {
		return address;
	}

	public void setAddress(ParticipantAddress address) {
		if (address == null) {
			throw new NullPointerException("Null value for address is not allowed!");
		}
		this.address = address;
	}

	public Gender getGender() {
		if (gender == null) {
			return Gender.UNDEFINED;
		}
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getNumSeats() {
		return numSeats;
	}

	public void setNumSeats(int numSeats) {
		this.numSeats = numSeats;
	}

	public String getEmail() {
		if (email == null) {
			return StringUtils.EMPTY;
		}
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNumber() {
		if (mobileNumber == null) {
			return StringUtils.EMPTY;
		}
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public int getParticipantNumber() {
		return participantNumber;
	}

	/**
	 * If true a participant is marked as the host within a team.<br>
	 * There exist only one host inside one team.
	 * 
	 * @return
	 */
	public boolean isHost() {
		return host;
	}

	public void setHost(boolean host) {
		this.host = host;
	}

	public MealSpecifics getMealSpecifics() {
		if (mealSpecifics == null) {
			return MealSpecifics.NONE;
		}
		return mealSpecifics;
	}

	public boolean hasMealSpecifics() {
		if (mealSpecifics == null || MealSpecifics.NONE.equals(getMealSpecifics())) {
			return false;
		}
		return true;
	}

	public void setMealSpecifics(MealSpecifics mealSpecifics) {
		this.mealSpecifics = mealSpecifics;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 7).append(getParticipantNumber()).toHashCode();
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
		Participant other = (Participant)obj;
		return new EqualsBuilder().append(getParticipantNumber(), other.getParticipantNumber()).isEquals();
	}

	@Override
	public int compareTo(Participant o) {
		if (this.getParticipantNumber() < o.getParticipantNumber()) {
			return -1;
		}
		if (this.getParticipantNumber() > o.getParticipantNumber()) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return participantNumber + " (" + name + ")";
	}

}
