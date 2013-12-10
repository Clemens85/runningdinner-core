package org.runningdinner.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RunningDinnerConfig implements Serializable {

	private static final long serialVersionUID = -1988234790887238219L;

	private GenderAspects genderAspects;
	private boolean forceEqualDistributedCapacityTeams;
	private boolean considerShortestPaths;
	private Set<MealClass> mealClasses;
	private int teamSize;

	protected RunningDinnerConfig(ConfigBuilder builder) {
		this.considerShortestPaths = builder.considerShortestPaths;
		this.forceEqualDistributedCapacityTeams = builder.forceEqualDistributedCapacityTeams;
		this.mealClasses = builder.mealClasses;
		this.teamSize = builder.teamSize;
		this.genderAspects = builder.genderAspects;
	}

	public GenderAspects getGenderAspects() {
		return genderAspects;
	}

	public boolean isForceEqualDistributedCapacityTeams() {
		return forceEqualDistributedCapacityTeams;
	}

	public boolean isConsiderShortestPaths() {
		return considerShortestPaths;
	}

	public Set<MealClass> getMealClasses() {
		return mealClasses;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public static ConfigBuilder newConfigurer() {
		return new ConfigBuilder();
	}

	public static class ConfigBuilder {

		// Defaults:
		private GenderAspects genderAspects = GenderAspects.IGNORE_GENDER;
		private boolean forceEqualDistributedCapacityTeams = true;
		private boolean considerShortestPaths = true;
		private Set<MealClass> mealClasses = null;
		private int teamSize = 2;

		public ConfigBuilder() {
		}

		public ConfigBuilder withTeamSize(final int teamSize) {
			this.teamSize = teamSize;
			return this;
		}

		public ConfigBuilder havingMeal(final MealClass mealClass) {
			if (mealClasses == null) {
				mealClasses = new HashSet<MealClass>(3);
			}
			mealClasses.add(mealClass);

			return this;
		}

		public ConfigBuilder withEqualDistributedCapacityTeams(boolean forceEqualDistributedCapacityTeams) {
			this.forceEqualDistributedCapacityTeams = forceEqualDistributedCapacityTeams;
			return this;
		}

		public ConfigBuilder withGenderAspects(GenderAspects genderAspects) {
			this.genderAspects = genderAspects;
			return this;
		}

		public RunningDinnerConfig build() {
			if (mealClasses == null) {
				// Add standard courses:
				mealClasses = new HashSet<MealClass>(3);
				mealClasses.add(MealClass.APPETIZER);
				mealClasses.add(MealClass.MAINCOURSE);
				mealClasses.add(MealClass.DESSERT);
			}

			return new RunningDinnerConfig(this);
		}
	}
}
