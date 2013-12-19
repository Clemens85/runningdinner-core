package org.runningdinner.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

public class RunningDinnerService {

	public GeneratedTeamsResult generateTeams(final RunningDinnerConfig runningDinnerConfig, final List<Participant> participants)
			throws NoPossibleRunningDinnerException {

		int teamSize = runningDinnerConfig.getTeamSize();
		int numParticipants = participants.size();

		if (teamSize >= numParticipants) {
			throw new NoPossibleRunningDinnerException("There must be more participants than a team's size");
		}

		GeneratedTeamsResult result = new GeneratedTeamsResult();

		List<Participant> participantsToAssign = splitRegularAndIrregularParticipants(participants, result, runningDinnerConfig);

		List<Team> regularTeams = buildRegularTeams(runningDinnerConfig, participantsToAssign);
		result.setRegularTeams(regularTeams);

		return result;
	}

	/**
	 * Takes all participants and splits them (only if necessary) so that e.g. for an odd participant-list the last participant is excluded
	 * (if teamSize is e.g. 2).<br>
	 * Furthermore it is computed how many teams are needed for building a valid dinner execution plan that satifies all rules for a running
	 * dinner. It may happen that this also results in splitting some
	 * participants, so that the correct number of participants is returned for building the needed number of teams.
	 * 
	 * @param allParticipants All participants that were given in for building a running dinner
	 * @param generatedTeamsResult Is enriched with all participants that cannot be assigend to teams (if any).
	 * @param runningDinnerConfig
	 * @return
	 * @throws NoPossibleRunningDinnerException If there are too few participants
	 * @throws IllegalArgumentException If there occurs computation errors when splitting the list (should never happen actually)
	 */
	private List<Participant> splitRegularAndIrregularParticipants(final List<Participant> allParticipants,
			final GeneratedTeamsResult generatedTeamsResult, final RunningDinnerConfig runningDinnerConfig)
			throws NoPossibleRunningDinnerException {

		int numberOfTeams = allParticipants.size() / runningDinnerConfig.getTeamSize();

		TeamCombinationInfo teamCombinationInfo = null;
		try {
			teamCombinationInfo = runningDinnerConfig.generateTeamCombinationInfo(numberOfTeams);
			generatedTeamsResult.setTeamCombinationInfo(teamCombinationInfo);
		}
		catch (NoPossibleRunningDinnerException ex) {
			generatedTeamsResult.setNotAssignedParticipants(allParticipants);
			return new ArrayList<Participant>(0);
		}

		// This will be the count of participants that cannot be assigned and must therefore be substracted from the participant-list before
		// building teams:
		int numIrregularParticipants = 0;

		// This is the number of teams that cannot be correctly assigned to a dinner execution plan without violating the rules:
		int numRemaindingTeams = teamCombinationInfo.getNumRemaindingTeams();
		if (numRemaindingTeams > 0) {
			int numRemaindingParticipants = numRemaindingTeams * runningDinnerConfig.getTeamSize();
			numIrregularParticipants = numRemaindingParticipants;
		}

		// This will typically be 0 (= all participants can put into teams => even number of participants) or 1 (odd number of
		// participants), or any other number for any teamSize != 2:
		int numParticipantOffset = allParticipants.size() % runningDinnerConfig.getTeamSize();
		if (numParticipantOffset > 0) {
			numIrregularParticipants += numParticipantOffset;
		}

		if (numIrregularParticipants > 0) {
			// Split participant list in participants that can be assigned to teams and those who must be excluded:
			List<Participant> participantsToAssign = allParticipants;
			int splitIndex = allParticipants.size() - numIrregularParticipants;
			CoreUtil.assertNotNegative(splitIndex, "SplitIndex may never be negative, but was " + splitIndex);
			participantsToAssign = allParticipants.subList(0, splitIndex);
			CoreUtil.assertSmaller(splitIndex, allParticipants.size(), "SplitIndex (" + splitIndex
					+ ") must be smaller as complete participant list-size (" + allParticipants.size() + ")");
			List<Participant> notAssignedParticipants = new ArrayList<Participant>(allParticipants.subList(splitIndex,
					allParticipants.size()));
			generatedTeamsResult.setNotAssignedParticipants(notAssignedParticipants);
			return participantsToAssign;
		}
		else {
			return allParticipants;
		}
	}

	public void assignRandomMealClasses(final GeneratedTeamsResult generatedTeams, final Set<MealClass> mealClasses)
			throws NoPossibleRunningDinnerException {

		List<Team> regularTeams = generatedTeams.getRegularTeams();

		int numTeams = regularTeams.size();
		int numMealClasses = mealClasses.size();

		if (numTeams % numMealClasses != 0) {
			throw new NoPossibleRunningDinnerException("Size of passed teams (" + numTeams + ") doesn't match expected size ("
					+ numMealClasses + " x N)");
		}

		if (numMealClasses == 0) {
			throw new NoPossibleRunningDinnerException("Need at least one mealClass for assigning mealClasses to teams");
		}

		int segmentionSize = numTeams / numMealClasses;

		Collections.shuffle(regularTeams); // Randomize List

		// Now, with the randomized list, we iterate this list, and assign one mealClass to the current iterating list-segment (e.g.:
		// [0..5] => APPETIZER, [6..11] => MAINCOURSE, [12..17] => DESSERT) for 18 teams and a segmentionSize of 3:
		int startIndex = 0;
		int endIndex = segmentionSize;
		for (MealClass mealClassToAssign : mealClasses) {
			for (int teamIndex = startIndex; teamIndex < endIndex; teamIndex++) {
				Team team = regularTeams.get(teamIndex);
				team.setMealClass(mealClassToAssign);
			}

			startIndex = endIndex;
			endIndex = endIndex + segmentionSize;
		}

		// Sort list by teamNumber as the list is currently passed in already sorted by teamNumber
		Collections.sort(regularTeams);
	}

	private List<Team> buildRegularTeams(final RunningDinnerConfig runningDinnerConfig, final List<Participant> participantsToAssign) {

		if (CoreUtil.isEmpty(participantsToAssign)) {
			return new ArrayList<Team>(0);
		}

		int teamSize = runningDinnerConfig.getTeamSize();
		int numTeamsToBuild = participantsToAssign.size() / teamSize;

		List<Team> result = new ArrayList<Team>(numTeamsToBuild);

		Collections.shuffle(participantsToAssign); // Sort list randomly!

		Queue<Participant> categoryOneList = new ArrayDeque<Participant>();
		Queue<Participant> categoryTwoList = new ArrayDeque<Participant>();
		Queue<Participant> uncategeorizedList = new ArrayDeque<Participant>();

		if (runningDinnerConfig.isForceEqualDistributedCapacityTeams()) {
			// Distribute team-members based on whether they have enough seats or not:
			for (Participant teamMember : participantsToAssign) {

				FuzzyBoolean canHouse = teamMember.canHouse(runningDinnerConfig);

				if (FuzzyBoolean.TRUE == canHouse) {
					// Enough space
					categoryOneList.offer(teamMember);
				}
				else if (FuzzyBoolean.UNKNOWN == canHouse) {
					// We don't know...
					uncategeorizedList.offer(teamMember);
				}
				else {
					// Not enough space
					categoryTwoList.offer(teamMember);
				}
			}

			CoreUtil.distributeEqually(categoryOneList, uncategeorizedList, categoryTwoList);
			uncategeorizedList.clear();
		}
		else {
			// Equally distribute all team-members over the two category-lists:
			CoreUtil.distributeEqually(categoryOneList, participantsToAssign, categoryTwoList);
		}

		// Build teams based upon the previously created category-queues:
		for (int i = 0; i < numTeamsToBuild; i++) {
			Set<Participant> teamMembersForOneTeam = new HashSet<Participant>();

			Queue<Participant> currentQueueToPoll = categoryOneList;

			// Try to equally take elements from both category-queues to equally distribute the needed members into one team:
			for (int j = 0; j < teamSize; j++) {

				Participant teamMember = currentQueueToPoll.poll();

				if (teamMember != null) {
					teamMembersForOneTeam.add(teamMember);
				}

				// Swap polling queue:
				if (currentQueueToPoll == categoryOneList) {
					currentQueueToPoll = categoryTwoList;
				}
				else {
					currentQueueToPoll = categoryOneList;
				}

				// If previous queue could not return a member try it now again, because queues have been swapped:
				if (teamMember == null) {
					teamMember = currentQueueToPoll.poll();
					if (teamMember == null) {
						// TODO: Strange state, we have to abort loop, maybe throw exception?
						break;
					}
					teamMembersForOneTeam.add(teamMember);
				}
			}

			Team team = new Team(i + 1);
			team.setTeamMembers(teamMembersForOneTeam);
			result.add(team);
		}

		return result;
	}

	public void generateDinnerExecutionPlan(final GeneratedTeamsResult generatedTeams, final RunningDinnerConfig runningDinnerConfig)
			throws NoPossibleRunningDinnerException {
		List<Team> regularTeams = generatedTeams.getRegularTeams();

		TeamCombinationInfo teamCombinationInfo = generatedTeams.getTeamCombinationInfo();
		final int teamSegmentSize = teamCombinationInfo.getTeamSegmentSize();

		// Segment teams by meal to cook:
		Map<MealClass, Queue<Team>> completeMealTeamMapping = new HashMap<MealClass, Queue<Team>>();
		for (Team team : regularTeams) {
			addTeamToMealMapping(team, completeMealTeamMapping);
		}

		// completeTealMapping contains now MealClass A, MealClass B, MealClass C, ... as keys.
		// For every key there is mapped the list with teams that are assigned to the MealClass which is backed by the key.

		final int numMealClasses = runningDinnerConfig.getMealClasses().size();
		CoreUtil.assertSmaller(0, numMealClasses, "There must exist more than zero MealClasses");

		// This should always equal to 2:
		final int numTeamsNeededPerMealClass = teamSegmentSize / numMealClasses;

		// Ensure that all teams are run through:
		for (int totalUsedTeamCounter = 0; totalUsedTeamCounter < regularTeams.size();) {

			// Take teams per meal-class, so that we reach #teamSegmentSize teams:
			int usedTeamCounterPerSegment = 0;
			Map<MealClass, Queue<Team>> segmentedMealTeamMapping = new HashMap<MealClass, Queue<Team>>(); // TODO: Actually we don't need a
																											// queue in here...
			for (MealClass mealClass : completeMealTeamMapping.keySet()) {

				Queue<Team> mappedTeamList = completeMealTeamMapping.get(mealClass);

				// This a very small loop, as we have typically a very small numnber (in almost any case it should be 2)
				for (int i = 0; i < numTeamsNeededPerMealClass; i++) {
					// remove() throws exception if queue is empty, which should however not occur. Nevertheless we prevent endless loops by
					// using this method:
					Team team = mappedTeamList.remove();
					addTeamToMealMapping(team, segmentedMealTeamMapping);
					totalUsedTeamCounter++;
					usedTeamCounterPerSegment++;
				}

				CoreUtil.assertSmallerOrEq(usedTeamCounterPerSegment, teamSegmentSize, "Number of used teams (" + usedTeamCounterPerSegment
						+ ") may never exceed the teamSegmentSize which is " + teamSegmentSize);

				if (usedTeamCounterPerSegment == teamSegmentSize) {
					buildVisitationPlans(segmentedMealTeamMapping, runningDinnerConfig);
					usedTeamCounterPerSegment = 0;
				}

			}
		}

		validateAllTeamsAreConsumed(completeMealTeamMapping);
	}

	private <T extends Collection<Team>> void buildVisitationPlans(final Map<MealClass, ? extends Collection<Team>> teamMealMapping,
			final RunningDinnerConfig runningDinnerConfig) throws NoPossibleRunningDinnerException {

		// Algorithm:
		// Iterate through all teams:
		// Rule #1: References between two teams are not permitted to be bidirectional (no parallel housing and guest reference)
		// Rule #2: For every iterated team: Consider only teams from other meal-classes
		// Rule #3 and goal: Every Team must have exactly #(meal-classes.size() -1) references in each direction (meaning e.g. 2
		// host-references and 2 guest-references)

		Set<MealClass> allMealClasses = teamMealMapping.keySet();
		// Every team needs this number for both outgoing references (= active visits of other teams) and incoming references (= hosting of
		// other teams)
		final int numReferencesNeeded = allMealClasses.size() - 1; // for rule #3
		if (numReferencesNeeded <= 0) {
			throw new NoPossibleRunningDinnerException("There must be at least two meal types for having a running-dinner!");
		}

		// Iterate thorough all teams by meal-class
		for (Entry<MealClass, ? extends Collection<Team>> entry : teamMealMapping.entrySet()) {
			MealClass currentMealClass = entry.getKey();
			Collection<Team> teamsOfCurrentMealClass = entry.getValue();

			Set<MealClass> otherMealClasses = CoreUtil.excludeFromSet(currentMealClass, allMealClasses); // for rule #2

			// Iterate through all teams of current meal-class
			for (Team teamOfCurrentMealClass : teamsOfCurrentMealClass) {
				VisitationPlan currentTeamVisitationPlan = teamOfCurrentMealClass.getVisitationPlan();

				// Rule #3 is satisfied for this team:
				if (numReferencesNeeded == currentTeamVisitationPlan.getNumberOfGuests()
						&& numReferencesNeeded == currentTeamVisitationPlan.getNumberOfHosts()) {
					// Visitation plan for this team is already complete
					break;
				}

				// Iterate through all teams of other meal-classes:
				for (MealClass otherMealClass : otherMealClasses) {

					boolean hasOneGuestReference = false;
					boolean hasOneHostReference = false;

					Collection<Team> teamsOfOtherMealClass = teamMealMapping.get(otherMealClass);
					for (Team teamOfOtherMealClass : teamsOfOtherMealClass) {

						VisitationPlan otherClassifiedTeamVisitationPlan = teamOfOtherMealClass.getVisitationPlan();
						if (otherClassifiedTeamVisitationPlan.containsGuestOrHostReference(teamOfCurrentMealClass)) {
							continue; // Rule #1
						}

						// for rule #3
						if (currentTeamVisitationPlan.getNumberOfHosts() != numReferencesNeeded
								&& otherClassifiedTeamVisitationPlan.getNumberOfGuests() != numReferencesNeeded) {
							currentTeamVisitationPlan.addHostTeam(teamOfOtherMealClass);
							hasOneHostReference = true;
							if (hasOneGuestReference) {
								break;
							}
							else {
								continue;
							}
						}

						if (currentTeamVisitationPlan.getNumberOfGuests() != numReferencesNeeded
								&& otherClassifiedTeamVisitationPlan.getNumberOfHosts() != numReferencesNeeded) {
							otherClassifiedTeamVisitationPlan.addHostTeam(teamOfCurrentMealClass);
							hasOneGuestReference = true;
							if (hasOneHostReference) {
								break;
							}
							else {
								continue;
							}
						}
					}

				} // End iteration through teams with other meal-classes

			} // End iteration through all teams of current meal-class
		}
	}

	private void addTeamToMealMapping(final Team team, final Map<MealClass, Queue<Team>> teamMealMapping) {
		MealClass mealClass = team.getMealClass();
		CoreUtil.assertNotNull(mealClass, "Team must have an assigned MealClass, but was null");
		Queue<Team> mappedTeamList = teamMealMapping.get(mealClass);
		if (mappedTeamList == null) {
			mappedTeamList = new ArrayDeque<Team>();
			teamMealMapping.put(mealClass, mappedTeamList);
		}
		mappedTeamList.add(team);
	}

	private static void validateAllTeamsAreConsumed(final Map<MealClass, Queue<Team>> completeMealTeamMapping) {
		for (Entry<MealClass, Queue<Team>> entry : completeMealTeamMapping.entrySet()) {
			Queue<Team> teamList = entry.getValue();
			if (teamList.size() > 0) {
				throw new RuntimeException("All teams must be consume when building dinner visitation plans, but there still exist "
						+ teamList.size() + " teams for MealClass " + entry.getKey());
			}
		}
	}
}
