package org.runningdinner.core;

import java.util.Set;

public class Team {

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + teamNumber;
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
		Team other = (Team)obj;
		if (teamNumber != other.teamNumber)
			return false;
		return true;
	}

}
