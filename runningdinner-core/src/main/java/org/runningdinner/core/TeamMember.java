package org.runningdinner.core;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TeamMember implements Comparable<TeamMember> {

	public static final int UNDEFINED_SEATS = -1;
	public static final int UNDEFINED_AGE = -1;

	private int memberNumber;

	private MemberName name;

	private Object address;

	private Gender gender;

	private int age;

	private int numSeats;

	public TeamMember(int memberNr) {
		this.memberNumber = memberNr;
		this.numSeats = UNDEFINED_SEATS;
	}

	public MemberName getName() {
		return name;
	}

	public void setName(MemberName name) {
		this.name = name;
	}

	public Object getAddress() {
		return address;
	}

	public void setAddress(Object address) {
		this.address = address;
	}

	public Gender getGender() {
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

	public int getMemberNumber() {
		return memberNumber;
	}

	public FuzzyBoolean canHouse(final RunningDinnerConfig runningDinnerConfig) {
		if (getNumSeats() == UNDEFINED_SEATS) {
			return FuzzyBoolean.UNKNOWN;
		}
		int numSeatsNeeded = runningDinnerConfig.getTeamSize() * runningDinnerConfig.getMealClasses().size();
		return getNumSeats() >= numSeatsNeeded ? FuzzyBoolean.TRUE : FuzzyBoolean.FALSE;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 7).append(getMemberNumber()).hashCode();
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
		TeamMember other = (TeamMember)obj;
		return new EqualsBuilder().append(getMemberNumber(), other.getMemberNumber()).isEquals();
	}

	@Override
	public int compareTo(TeamMember o) {
		if (this.getMemberNumber() < o.getMemberNumber()) {
			return -1;
		}
		if (this.getMemberNumber() > o.getMemberNumber()) {
			return 1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return memberNumber + " (" + name + ")";
	}

}
