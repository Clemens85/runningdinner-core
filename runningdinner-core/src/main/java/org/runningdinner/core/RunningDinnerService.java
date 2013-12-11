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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

public class RunningDinnerService {

	public GeneratedTeamsResult generateTeams(final RunningDinnerConfig runningDinnerConfig, final List<Participant> teamMembers)
			throws NoPossibleRunningDinnerException {

		int teamSize = runningDinnerConfig.getTeamSize();
		int numParticipants = teamMembers.size();

		if (teamSize >= numParticipants) {
			throw new NoPossibleRunningDinnerException("There must be more participants than a team's size");
		}

		GeneratedTeamsResult result = new GeneratedTeamsResult();

		int numTeams = numParticipants / teamSize;

		List<Participant> teamMembersToAssign = teamMembers;
		int teamOffset = numParticipants % teamSize;
		if (teamOffset > 0) {
			teamMembersToAssign = teamMembers.subList(0, teamMembers.size() - teamOffset);
			List<Participant> notAssignedMembers = new ArrayList<Participant>(teamMembers.subList(teamMembers.size() - teamOffset,
					teamMembers.size()));
			result.setNotAssignedParticipants(notAssignedMembers);
		}

		List<Team> regularTeams = buildRegularTeams(runningDinnerConfig, teamMembersToAssign, numTeams);
		result.setRegularTeams(regularTeams);

		return result;
	}

	public void assignRandomMealClasses(final GeneratedTeamsResult generatedTeams, final Set<MealClass> mealClasses)
			throws NoPossibleRunningDinnerException {

		List<Team> regularTeams = generatedTeams.getRegularTeams();

		int numTeams = regularTeams.size();
		int numMealClasses = mealClasses.size();

		if (numTeams % numMealClasses != 0) {
			throw new NoPossibleRunningDinnerException("Size of passed teams (" + numTeams + ") doesn't match expted size ("
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

	private List<Team> buildRegularTeams(final RunningDinnerConfig runningDinnerConfig, final List<Participant> teamMembersToAssign,
			final int numTeamsToBuild) {

		List<Team> result = new ArrayList<Team>(numTeamsToBuild);

		int teamSize = runningDinnerConfig.getTeamSize();

		Collections.shuffle(teamMembersToAssign); // Sort list randomly!

		Queue<Participant> categoryOneList = new ArrayDeque<Participant>();
		Queue<Participant> categoryTwoList = new ArrayDeque<Participant>();
		Queue<Participant> uncategeorizedList = new ArrayDeque<Participant>();

		if (runningDinnerConfig.isForceEqualDistributedCapacityTeams()) {
			// Distribute team-members based on whether they have enough seats or not:
			for (Participant teamMember : teamMembersToAssign) {

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

			distributeEqually(categoryOneList, uncategeorizedList, categoryTwoList);
			uncategeorizedList.clear();
		}
		else {
			// Equally distribute all team-members over the two category-lists:
			distributeEqually(categoryOneList, teamMembersToAssign, categoryTwoList);
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

	public Object buildDinnerExecutionPlan(final GeneratedTeamsResult generatedTeams, final RunningDinnerConfig runningDinnerConfig)
			throws NoPossibleRunningDinnerException {
		List<Team> regularTeams = generatedTeams.getRegularTeams();

		TeamCombinationInfo teamCombinationInfo = generatedTeams.getTeamCombinationInfo();
		int teamSegmentSize = teamCombinationInfo.getTeamSegmentSize();

		// Segment teams by meal to cook:
		Map<MealClass, Queue<Team>> completeMealTeamMapping = new HashMap<MealClass, Queue<Team>>();
		for (Team team : regularTeams) {
			addTeamToMealMapping(team, completeMealTeamMapping);
		}

		int usedTeamCounter = 0;
		Map<MealClass, Collection<Team>> segmentedMealTeamMapping = new HashMap<MealClass, Collection<Team>>();
		for (MealClass mealClass : completeMealTeamMapping.keySet()) {

			Queue<Team> mappedTeamList = completeMealTeamMapping.get(mealClass);
			Team team = mappedTeamList.poll();

			addTeamToMealMapping(team, segmentedMealTeamMapping);

			if (usedTeamCounter++ > teamSegmentSize) {
				buildVisitationPlans(segmentedMealTeamMapping, runningDinnerConfig);
				usedTeamCounter = 0;
			}
		}

		throw new UnsupportedOperationException("nyi");
	}

	private void buildVisitationPlans(final Map<MealClass, Collection<Team>> teamMealMapping, final RunningDinnerConfig runningDinnerConfig)
			throws NoPossibleRunningDinnerException {

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
		for (Entry<MealClass, Collection<Team>> entry : teamMealMapping.entrySet()) {
			MealClass mealClass = entry.getKey();
			Collection<Team> teams = entry.getValue();

			Set<MealClass> otherMealClasses = getFilteredMealClasses(mealClass, allMealClasses); // for rule #2

			// Iterate through all teams of current meal-class
			for (Team team : teams) {
				VisitationPlan currentTeamVisitationPlan = team.getVisitationPlan();

				// Rule #3 is satisfied for this team:
				if (numReferencesNeeded == currentTeamVisitationPlan.getNumberOfGuests()
						&& numReferencesNeeded == currentTeamVisitationPlan.getNumberOfHosts()) {
					// Visitation plan for this team is already complete
					break;
				}

				// Iterate through all teams of other meal-classes:
				for (MealClass otherMealClass : otherMealClasses) {

					Collection<Team> otherClassifiedTeams = teamMealMapping.get(otherMealClass);
					for (Team otherClassifiedTeam : otherClassifiedTeams) {
						VisitationPlan otherClassifiedTeamVisitationPlan = otherClassifiedTeam.getVisitationPlan();
						if (otherClassifiedTeamVisitationPlan.containsGuestOrHostReference(team)) {
							continue; // Rule #1
						}

						// for rule #3
						if (currentTeamVisitationPlan.getNumberOfHosts() != numReferencesNeeded
								&& otherClassifiedTeamVisitationPlan.getNumberOfGuests() != numReferencesNeeded) {
							currentTeamVisitationPlan.addHostTeam(otherClassifiedTeam);
							break;
						}
					}

				} // End iteration through teams with other meal-classes

			} // End iteration through all teams of current meal-class
		}
	}

	private void addTeamToMealMapping(final Team team, final Map<MealClass, Collection<Team>> teamMealMapping) {
		MealClass mealClass = team.getMealClass();
		Collection<Team> mappedTeamList = teamMealMapping.get(mealClass);
		if (mappedTeamList == null) {
			mappedTeamList = new ArrayDeque<Team>();
			teamMealMapping.put(mealClass, mappedTeamList);
		}
		mappedTeamList.add(team);
	}

	private Set<MealClass> getFilteredMealClasses(final MealClass mealClassToExclude, final Set<MealClass> allMealClasses) {
		Set<MealClass> result = new HashSet<MealClass>();

		CollectionUtils.filter(result, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				MealClass mealClass = (MealClass)obj;
				if (mealClass.equals(mealClassToExclude)) {
					return false;
				}
				return true;
			}
		});
		return result;
	}

	private <T> void distributeEqually(final Collection<T> left, final Collection<T> middle, final Collection<T> right) {
		for (T m : middle) {
			if (left.size() < right.size()) {
				left.add(m);
			}
			else {
				right.add(m);
			}
		}
	}
}
