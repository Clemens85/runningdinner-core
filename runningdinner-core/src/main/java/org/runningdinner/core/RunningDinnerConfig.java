package org.runningdinner.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RunningDinnerConfig implements Serializable {

	private GenderAspects genderAspects = GenderAspects.IGNORE_GENDER;

	private boolean forceEqualDistributedCapacityTeams = true;

	private boolean considerShortestPaths = true;

	private Set<MealClass> mealClasses = new HashSet<MealClass>(3);

	private int teamSize = 2;

	public RunningDinnerConfig() {
		mealClasses.add(MealClass.APPETIZER);
		mealClasses.add(MealClass.MAINCOURSE);
		mealClasses.add(MealClass.DESSERT);
	}

	public GenderAspects getGenderAspects() {
		return genderAspects;
	}

	public void setGenderAspects(GenderAspects genderAspects) {
		this.genderAspects = genderAspects;
	}

	public boolean isForceEqualDistributedCapacityTeams() {
		return forceEqualDistributedCapacityTeams;
	}

	public void setForceEqualDistributedCapacityTeams(boolean forceEqualDistributedCapacityTeams) {
		this.forceEqualDistributedCapacityTeams = forceEqualDistributedCapacityTeams;
	}

	public boolean isConsiderShortestPaths() {
		return considerShortestPaths;
	}

	public void setConsiderShortestPaths(boolean considerShortestPaths) {
		this.considerShortestPaths = considerShortestPaths;
	}

	public Set<MealClass> getMealClasses() {
		return mealClasses;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

}
