package org.runningdinner.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.runningdinner.core.dinnerplan.DinnerPlanGenerator;
import org.runningdinner.core.dinnerplan.StaticTemplateDinnerPlanGenerator;
import org.runningdinner.core.util.CoreUtil;

/**
 * Stateless object for calculating running dinner scenarios.<br>
 * All needed items like the configuration of a dinner (meals, team-size, etc.) must be passed into the according methods.<br>
 * Main purpose is on the one hand to generate random teams out of the passed participants and on the other hand to create visitation-plans
 * (= dinner-routes) for each generated team.
 * 
 * @author Clemens Stich
 * 
 */
public class RunningDinnerCalculator {

	private DinnerPlanGenerator dinnerPlanGenerator;

	public RunningDinnerCalculator() {
		this(new StaticTemplateDinnerPlanGenerator());
	}

	public RunningDinnerCalculator(DinnerPlanGenerator dinnerPlanGenerator) {
		this.dinnerPlanGenerator = dinnerPlanGenerator;
	}

	/**
	 * Main entry point for calculation of a running dinner.<br>
	 * Based upon the passed participants and the dinner-options (e.g. team size, number of meals, ...) there is tried to assign each
	 * participant into teams.<br>
	 * If there are too few participants for at least one valid combination of teams then a NoPossiblerunningDinnerException is thrown. Thus
	 * the caller knows
	 * that it was not possible to assign any single passed participant into teams.<br>
	 * The returned GeneratedTeamsResult must be used afterwards for further operations like assigning meals to the teams or like finally
	 * building the dinner-execution-plan. <br>
	 * Note: The objects inside the GeneratedTeamsResult may be changed during other operations!
	 * 
	 * @param runningDinnerConfig The options for the running dinner
	 * @param participants All participants of the dinner
	 * @return
	 * @throws NoPossibleRunningDinnerException If there are too few participants
	 */
	public GeneratedTeamsResult generateTeams(final RunningDinnerConfig runningDinnerConfig, final List<Participant> participants)
			throws NoPossibleRunningDinnerException {

		int teamSize = runningDinnerConfig.getTeamSize();
		int numParticipants = participants.size();

		if (teamSize >= numParticipants) {
			throw new NoPossibleRunningDinnerException("There must be more participants as a team's size");
		}

		TeamCombinationInfo teamCombinationInfo = generateTeamCombinationInfo(participants, runningDinnerConfig);

		GeneratedTeamsResult result = new GeneratedTeamsResult();
		result.setTeamCombinationInfo(teamCombinationInfo);

		List<Participant> participantsToAssign = splitRegularAndIrregularParticipants(participants, result, runningDinnerConfig);

		List<Team> regularTeams = buildRegularTeams(runningDinnerConfig, participantsToAssign);
		result.setRegularTeams(regularTeams);

		return result;
	}

	/**
	 * Returns a list with all participants that cannot be assigned into regular teams based upon the current dinner configuration.<br>
	 * It all participants can successfully be assigned, then an empty list is returned.<br>
	 * If not any participant can be assigned (e.g. too few participants) all passed participants are returned<br>
	 * 
	 * @param runningDinnerConfig
	 * @param participants
	 * @return
	 */
	public List<Participant> calculateNotAssignableParticipants(final RunningDinnerConfig runningDinnerConfig,
			final List<Participant> participants) {
		try {
			TeamCombinationInfo teamCombinationInfo = generateTeamCombinationInfo(participants, runningDinnerConfig);
			int numOfNotAssignableParticipants = calculateNumberOfNotAssignableParticipants(participants, teamCombinationInfo,
					runningDinnerConfig);
			if (numOfNotAssignableParticipants <= 0) {
				return Collections.emptyList();
			}

			int splitIndex = participants.size() - numOfNotAssignableParticipants;
			CoreUtil.assertNotNegative(splitIndex, "SplitIndex may never be negative, but was " + splitIndex);
			return new ArrayList<Participant>(participants.subList(splitIndex, participants.size()));
		}
		catch (NoPossibleRunningDinnerException e) {
			// Return all participants
			return new ArrayList<Participant>(participants);
		}
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
	 * @throws IllegalArgumentException If there occurs computation errors when splitting the list (should never happen actually)
	 */
	private List<Participant> splitRegularAndIrregularParticipants(final List<Participant> allParticipants,
			final GeneratedTeamsResult generatedTeamsResult, final RunningDinnerConfig runningDinnerConfig) {

		TeamCombinationInfo teamCombinationInfo = generatedTeamsResult.getTeamCombinationInfo();

		// This will be the count of participants that cannot be assigned and must therefore be substracted from the participant-list before
		// building teams:
		int numIrregularParticipants = calculateNumberOfNotAssignableParticipants(allParticipants, teamCombinationInfo, runningDinnerConfig);

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

	protected TeamCombinationInfo generateTeamCombinationInfo(final List<Participant> allParticipants,
			final RunningDinnerConfig runningDinnerConfig) throws NoPossibleRunningDinnerException {

		int numberOfTeams = allParticipants.size() / runningDinnerConfig.getTeamSize();
		TeamCombinationInfo teamCombinationInfo = runningDinnerConfig.generateTeamCombinationInfo(numberOfTeams);
		return teamCombinationInfo;
	}

	protected int calculateNumberOfNotAssignableParticipants(final List<Participant> allParticipants,
			final TeamCombinationInfo teamCombinationInfo, final RunningDinnerConfig runningDinnerConfig) {

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

		return numIrregularParticipants;
	}

	/**
	 * Uses the passed mealClasses and assigns randomly each team one mealClass.
	 * 
	 * @param generatedTeams Contains all regular teams that shall be assigned random meals
	 * @param mealClasses The Meals to be assigned
	 * @throws NoPossibleRunningDinnerException Thrown if number of meals and number of teams are incompatible
	 */
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
		// [0..8] => APPETIZER, [9..17] => MAINCOURSE, [18..26] => DESSERT) for 18 teams and a segmentionSize of 3:
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

		TeamDistributor teamDistributor = new TeamDistributor(participantsToAssign);
		teamDistributor.distribute(runningDinnerConfig);
		Queue<Participant> categoryOneList = teamDistributor.getCategoryOneList();
		Queue<Participant> categoryTwoList = teamDistributor.getCategoryTwoList();

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

			setHostingParticipant(team, runningDinnerConfig);

			result.add(team);
		}

		CoreUtil.assertHasSize(
				result,
				numTeamsToBuild,
				String.format("Fatal Error: Expected %d teams to be built, but actually there were built %d teams", numTeamsToBuild,
						result.size()));
		return result;
	}

	private void setHostingParticipant(Team team, RunningDinnerConfig runningDinnerConfig) {
		Participant participantWithUnknownHostingStatus = null;

		for (Participant teamMember : team.getTeamMembers()) {
			FuzzyBoolean canHost = runningDinnerConfig.canHost(teamMember);
			if (FuzzyBoolean.TRUE == canHost) {
				teamMember.setHost(true);
				return;
			}
			if (FuzzyBoolean.UNKNOWN == canHost) {
				participantWithUnknownHostingStatus = teamMember;
			}
		}

		// First fallback: Take one participant with unknown hosting status:
		if (participantWithUnknownHostingStatus != null) {
			participantWithUnknownHostingStatus.setHost(true);
			return;
		}

		// Last fallback, just pick up the first matching participant:
		team.getTeamMembers().iterator().next().setHost(true);
	}

	/**
	 * Final (and main) method which assigns every regular team a VisitationPlan which indicats which teams are guests and hosts for every
	 * regular team.
	 * TODO: This method must completely be rewritten as it contains some crap code and wrong (and/or hard-coded) assumptions !!!
	 * 
	 * @param generatedTeams
	 * @param runningDinnerConfig
	 * @throws NoPossibleRunningDinnerException
	 * @throws IllegalArgumentException If some pre-condition is not met in the passed parameters
	 */
	public void generateDinnerExecutionPlan(final GeneratedTeamsResult generatedTeams, final RunningDinnerConfig runningDinnerConfig)
			throws NoPossibleRunningDinnerException {

		dinnerPlanGenerator.generateDinnerExecutionPlan(generatedTeams, runningDinnerConfig);
	}
}
