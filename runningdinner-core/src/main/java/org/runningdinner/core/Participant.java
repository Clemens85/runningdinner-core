package org.runningdinner.core;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.runningdinner.core.model.AbstractEntity;

@Entity
public class Participant extends AbstractEntity implements Comparable<Participant> {

	private static final long serialVersionUID = -8062709434676386371L;

	public static final int UNDEFINED_SEATS = -1;
	public static final int UNDEFINED_AGE = -1;

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

	public boolean isHost() {
		return host;
	}

	public void setHost(boolean host) {
		this.host = host;
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
