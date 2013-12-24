package org.runningdinner.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Participant implements Comparable<Participant> {

	public static final int UNDEFINED_SEATS = -1;
	public static final int UNDEFINED_AGE = -1;

	private int participantNumber;

	private ParticipantName name;

	private ParticipantAddress address;

	private Gender gender;

	private int age;

	private int numSeats;

	public Participant(final int participantNumber) {
		this.participantNumber = participantNumber;
		this.numSeats = UNDEFINED_SEATS;
	}

	public ParticipantName getName() {
		return name;
	}

	public void setName(ParticipantName name) {
		this.name = name;
	}

	public ParticipantAddress getAddress() {
		return address;
	}

	public void setAddress(ParticipantAddress address) {
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

	public int getParticipantNumber() {
		return participantNumber;
	}

	public FuzzyBoolean canHost(final RunningDinnerConfig runningDinnerConfig) {
		if (getNumSeats() == UNDEFINED_SEATS) {
			return FuzzyBoolean.UNKNOWN;
		}
		int numSeatsNeeded = runningDinnerConfig.getTeamSize() * runningDinnerConfig.getMealClasses().size();
		return getNumSeats() >= numSeatsNeeded ? FuzzyBoolean.TRUE : FuzzyBoolean.FALSE;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 7).append(getParticipantNumber()).hashCode();
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
