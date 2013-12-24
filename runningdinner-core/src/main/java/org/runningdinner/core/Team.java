package org.runningdinner.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Team implements Comparable<Team> {

	private int teamNumber;

	private Set<Participant> teamMembers;

	private MealClass mealClass;

	private VisitationPlan visitationPlan;

	public Team(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	public Set<Participant> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(Set<Participant> teamMembers) {
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

	public VisitationPlan getVisitationPlan() {
		if (this.visitationPlan == null) {
			this.visitationPlan = new VisitationPlan(this);
		}
		return visitationPlan;
	}

	void setVisitationPlan(VisitationPlan visitationPlan) {
		this.visitationPlan = visitationPlan;
	}

	public List<FuzzyBoolean> getHousingDump(final RunningDinnerConfig runningDinnerConfig) {
		ArrayList<FuzzyBoolean> result = new ArrayList<FuzzyBoolean>(teamMembers.size());
		for (Participant member : teamMembers) {
			result.add(member.canHost(runningDinnerConfig));
		}
		return result;
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
		String mealClassStr = mealClass != null ? " - " + mealClass.toString() : "";
		return teamNumber + mealClassStr + " (" + teamMembers + ")";
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
