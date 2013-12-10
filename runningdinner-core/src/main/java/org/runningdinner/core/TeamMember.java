package org.runningdinner.core;

public class TeamMember {

	public static final int UNDEFINED_SEATS = -1;
	public static final int UNDEFINED_AGE = -1;

	private Object name;

	private Object address;

	private Gender gender;

	private int age;

	private int numSeats;

	public TeamMember() {
		this.numSeats = UNDEFINED_SEATS;
	}

	public Object getName() {
		return name;
	}

	public void setName(Object name) {
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

}
