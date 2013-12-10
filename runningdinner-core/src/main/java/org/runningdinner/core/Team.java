package org.runningdinner.core;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Team implements Comparable<Team> {

	private Set<TeamMember> teamMembers;
	private MealClass mealClass;
	private int teamNumber;

	public Team(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	public Set<TeamMember> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(Set<TeamMember> teamMembers) {
		this.teamMembers = teamMembers;
	}

	public MealClass getMealClass() {
		return mealClass;
	}

	public void setMealClass(MealClass mealClass) {
		this.mealClass = mealClass;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(31, 7).append(getTeamNumber()).hashCode();
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

		Team other = (Team)obj;
		return new EqualsBuilder().append(getTeamNumber(), other.getTeamNumber()).isEquals();
	}

	@Override
	public String toString() {
		return teamNumber + ": " + teamMembers;
	}

	@Override
	public int compareTo(Team o) {
		if (this.getTeamNumber() < o.getTeamNumber()) {
			return -1;
		}
		if (this.getTeamNumber() > o.getTeamNumber()) {
			return 1;
		}
		return 0;
	}
}
