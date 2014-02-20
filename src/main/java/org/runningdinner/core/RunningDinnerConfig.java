package org.runningdinner.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

/**
 * Contains the options for a running dinner (e.g. which meals to cook, size of the teams, ...)
 * 
 * @author Clemens Stich
 * 
 */
@Embeddable
public class RunningDinnerConfig {

	@OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	@JoinColumn(name = "dinner_id")
	@OrderBy(value = "time")
	private Set<MealClass> mealClasses;

	private int teamSize;

	private boolean considerShortestPaths;

	private GenderAspect genderAspects;

	private boolean forceEqualDistributedCapacityTeams;

	protected RunningDinnerConfig() {
		// Needed for JPA
	}

	protected RunningDinnerConfig(ConfigBuilder builder) {
		this.considerShortestPaths = builder.considerShortestPaths;
		this.forceEqualDistributedCapacityTeams = builder.forceEqualDistributedCapacityTeams;
		this.mealClasses = builder.mealClasses;
		this.teamSize = builder.teamSize;
		this.genderAspects = builder.genderAspects;
	}

	/**
	 * Determines whether teams shall be mixed up by using certain gender aspects.<br>
	 * Currently not supported by calculation.
	 * 
	 * @return
	 */
	public GenderAspect getGenderAspects() {
		return genderAspects;
	}

	/**
	 * Determines whether teams shall be mixed up based on the hosting capabilities of single participants
	 * 
	 * @return
	 */
	public boolean isForceEqualDistributedCapacityTeams() {
		return forceEqualDistributedCapacityTeams;
	}

	/**
	 * Determines whether dinner-routes between teams shall be generated based up on nearest distances.<br>
	 * Currently not supported by calculation
	 * 
	 * @return
	 */
	public boolean isConsiderShortestPaths() {
		return considerShortestPaths;
	}

	/**
	 * Contains all meals to cook for a running dinner
	 * 
	 * @return
	 */
	public Set<MealClass> getMealClasses() {
		return mealClasses;
	}

	/**
	 * Determines how many participants are mixed up into one team. Typcially this should be 2.
	 * 
	 * @return
	 */
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

		int teamSegmentSize = numMeals * numMeals; // I needs this number of teams to get a valid running dinner team-combination

		if (teamSegmentSize > numberOfTeams) {
			throw new NoPossibleRunningDinnerException("Too few number of teams (" + numberOfTeams
					+ ") for performing a running dinner without violating the rules!");
		}

		int numRemaindingTeams = numberOfTeams % teamSegmentSize;

		return new TeamCombinationInfo(teamSegmentSize, numRemaindingTeams);
	}

	/**
	 * Checks whether the passed participant can act as host based upon the current dinner-options.<br>
	 * It may happen that the hosting-capabilities of the participant is not known. In such a case FuzzyBoolean.UNKNOWN will be returned.
	 * 
	 * @param participant
	 * @return
	 */
	public FuzzyBoolean canHost(final Participant participant) {
		if (participant.getNumSeats() == Participant.UNDEFINED_SEATS) {
			return FuzzyBoolean.UNKNOWN;
		}
		int numSeatsNeeded = getTeamSize() * getMealClasses().size();
		return participant.getNumSeats() >= numSeatsNeeded ? FuzzyBoolean.TRUE : FuzzyBoolean.FALSE;
	}

	/**
	 * Create a new running dinner configuration instance
	 * 
	 * @return
	 */
	public static ConfigBuilder newConfigurer() {
		return new ConfigBuilder();
	}

	public static class ConfigBuilder {

		// Defaults:
		private GenderAspect genderAspects = GenderAspect.IGNORE_GENDER;
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

		public ConfigBuilder havingMeals(final Collection<MealClass> meals) {
			if (this.mealClasses == null) {
				this.mealClasses = new HashSet<MealClass>(meals);
			}
			else {
				this.mealClasses.clear();
				this.mealClasses.addAll(meals);
			}

			return this;
		}

		public ConfigBuilder withEqualDistributedCapacityTeams(boolean forceEqualDistributedCapacityTeams) {
			this.forceEqualDistributedCapacityTeams = forceEqualDistributedCapacityTeams;
			return this;
		}

		public ConfigBuilder withGenderAspects(GenderAspect genderAspects) {
			this.genderAspects = genderAspects;
			return this;
		}

		public RunningDinnerConfig build() {
			if (mealClasses == null) {
				// Add standard courses:
				mealClasses = new HashSet<MealClass>(3);
				mealClasses.add(MealClass.APPETIZER());
				mealClasses.add(MealClass.MAINCOURSE());
				mealClasses.add(MealClass.DESSERT());
			}

			return new RunningDinnerConfig(this);
		}
	}
}
