package org.runningdinner.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class RunningDinnerConfig implements Serializable {

	private static final long serialVersionUID = -1988234790887238219L;

	private Set<MealClass> mealClasses;

	private int teamSize;

	private boolean considerShortestPaths;
	private GenderAspects genderAspects;
	private boolean forceEqualDistributedCapacityTeams;

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

	/**
	 * Generates the TeamCombinationInfo object for the passed number of teams.
	 * 
	 * @param numberOfTeams
	 * @return
	 * @throws NoPossibleRunningDinnerException
	 */
	public TeamCombinationInfo generateTeamCombinationInfo(final int numberOfTeams) throws NoPossibleRunningDinnerException {
		Set<MealClass> mealClasses = getMealClasses();
		int numMeals = mealClasses.size();

		int teamSegmentSize = numMeals * 2; // I needs this number of teams to get a valid running dinner team-combination

		if (teamSegmentSize > numberOfTeams) {
			throw new NoPossibleRunningDinnerException("Too few number of teams (" + numberOfTeams
					+ ") for performing a running dinner without violating the rules!");
		}

		int numRemaindingTeams = numberOfTeams % teamSegmentSize;

		return new TeamCombinationInfo(teamSegmentSize, numRemaindingTeams);
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

		// TODO: das geht noch besser, mit neuem verschachteltem builder (-> arne limburg vortrag -> DDD)
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
