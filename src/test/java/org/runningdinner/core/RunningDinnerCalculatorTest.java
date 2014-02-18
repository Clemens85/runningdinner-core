package org.runningdinner.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;

public class RunningDinnerCalculatorTest {

	private RunningDinnerCalculator runningDinnerCalculator = new RunningDinnerCalculator();

	private RunningDinnerConfig standardConfig = RunningDinnerConfig.newConfigurer().withEqualDistributedCapacityTeams(true).build();
	private RunningDinnerConfig standardConfigWithoutDistributing = RunningDinnerConfig.newConfigurer().withEqualDistributedCapacityTeams(
			false).withGenderAspects(GenderAspect.IGNORE_GENDER).build();
	private RunningDinnerConfig customConfig = RunningDinnerConfig.newConfigurer().havingMeals(
			Arrays.asList(MealClass.APPETIZER, MealClass.MAINCOURSE)).build();

	@Test
	public void testInvalidConditionWithDefaults() {
		List<Participant> teamMembers = generateParticipants(2);
		try {
			runningDinnerCalculator.generateTeams(standardConfig, teamMembers);
			fail("Should never reach here, because Exception should be thrown!");
		}
		catch (NoPossibleRunningDinnerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testTeamsWithoutDistributing() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateParticipants(18);

		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(standardConfigWithoutDistributing, teamMembers);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(9, teamsResult.getRegularTeams().size());

		System.out.println("*** testTeamsWithoutDistributing ***");
		for (Team team : teamsResult.getRegularTeams()) {
			assertEquals(standardConfigWithoutDistributing.getTeamSize(), team.getTeamMembers().size());
			System.out.println(team);
		}
	}

	@Test
	public void testTeamsWithBalancedDistributing() throws NoPossibleRunningDinnerException {
		List<Participant> participants = generateEqualBalancedParticipants(0);

		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(standardConfig, participants);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(9, teamsResult.getRegularTeams().size());

		System.out.println("*** testTeamsWithBalancedDistributing ***");
		for (Team team : teamsResult.getRegularTeams()) {

			assertEquals(true, isDistributionBalanced(team, standardConfig));

			System.out.println(team + " - canHost: " + team.getHostingCapability(standardConfig));
		}
	}

	private List<Participant> generateEqualBalancedParticipants(int participantNrOffset) {
		List<Participant> result = generateParticipants(18, participantNrOffset);
		result.get(0).setNumSeats(6);
		result.get(1).setNumSeats(3);
		result.get(2).setNumSeats(4);
		result.get(3).setNumSeats(7);
		result.get(4).setNumSeats(10);
		result.get(5).setNumSeats(6);

		result.get(6).setNumSeats(6);
		result.get(7).setNumSeats(3);
		result.get(8).setNumSeats(4);
		result.get(9).setNumSeats(5);
		result.get(10).setNumSeats(3);
		result.get(11).setNumSeats(6);

		result.get(12).setNumSeats(6);
		result.get(13).setNumSeats(3);
		result.get(14).setNumSeats(6);
		result.get(15).setNumSeats(5);
		result.get(16).setNumSeats(3);
		result.get(17).setNumSeats(6);

		return result;
	}

	@Test
	public void testTeamsWithUnbalancedDistributing() throws NoPossibleRunningDinnerException {
		List<Participant> particiapnts = generateParticipants(18);
		particiapnts.get(0).setNumSeats(6);
		particiapnts.get(1).setNumSeats(6);
		particiapnts.get(2).setNumSeats(6);
		particiapnts.get(3).setNumSeats(1);
		particiapnts.get(4).setNumSeats(1);
		particiapnts.get(5).setNumSeats(1);

		particiapnts.get(6).setNumSeats(1);
		particiapnts.get(7).setNumSeats(1);
		particiapnts.get(8).setNumSeats(1);
		particiapnts.get(9).setNumSeats(1);
		particiapnts.get(10).setNumSeats(1);
		particiapnts.get(11).setNumSeats(6);

		particiapnts.get(12).setNumSeats(1);
		particiapnts.get(13).setNumSeats(1);
		particiapnts.get(14).setNumSeats(1);
		particiapnts.get(15).setNumSeats(1);
		particiapnts.get(16).setNumSeats(1);
		particiapnts.get(17).setNumSeats(1);

		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(standardConfig, particiapnts);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(9, teamsResult.getRegularTeams().size());

		System.out.println("*** testTeamsWithUnbalancedDistributing ***");
		int numBalancedTeams = 0;
		int numUnbalancedTeams = 0;
		for (Team team : teamsResult.getRegularTeams()) {
			if (isDistributionBalanced(team, standardConfig)) {
				numBalancedTeams++;
			}
			else {
				numUnbalancedTeams++;
			}

			System.out.println(team + " - canHouse :" + team.getHostingCapability(standardConfig));
		}

		assertEquals(4, numBalancedTeams);
		assertEquals(5, numUnbalancedTeams);
	}

	@Test
	public void testNotAssignedTeamMembers() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateParticipants(19);
		GeneratedTeamsResult result = runningDinnerCalculator.generateTeams(standardConfig, teamMembers);
		assertEquals(true, result.hasNotAssignedParticipants());
		assertEquals(1, result.getNotAssignedParticipants().size());
		assertEquals(9, result.getRegularTeams().size());

		Participant notAssignedMember = result.getNotAssignedParticipants().iterator().next();
		for (Team regularTeam : result.getRegularTeams()) {
			assertEquals(2, regularTeam.getTeamMembers().size());
			assertEquals(false, regularTeam.getTeamMembers().contains(notAssignedMember));
		}
	}

	@Test
	public void testTooFewParticipants() {

		List<Participant> teamMembers = generateParticipants(5);

		// Assert that all participants are returned again as non assignable:
		List<Participant> notAssignableParticipants = runningDinnerCalculator.calculateNotAssignableParticipants(standardConfig,
				teamMembers);
		assertEquals(teamMembers.size(), notAssignableParticipants.size());

		try {
			runningDinnerCalculator.generateTeams(standardConfig, teamMembers);
			fail("Expected NoPossibleRunningDinnerException to be thrown");
		}
		catch (NoPossibleRunningDinnerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCustomConfigTeamBuilding() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateParticipants(13);
		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(customConfig, teamMembers);
		assertEquals(true, teamsResult.hasNotAssignedParticipants());
		assertEquals(5, teamsResult.getNotAssignedParticipants().size());
		assertEquals(9, teamsResult.getNotAssignedParticipants().get(0).getParticipantNumber()); // Ensure that last user is the one not
																									// assigned
		assertEquals(4, teamsResult.getRegularTeams().size());

		System.out.println("*** testCustomConfigTeamBuilding ***");
		for (Team team : teamsResult.getRegularTeams()) {
			assertEquals(customConfig.getTeamSize(), team.getTeamMembers().size());
			System.out.println(team);
		}
	}

	@Test
	public void testRandomMealClasses() throws NoPossibleRunningDinnerException {
		List<Participant> particiapnts = generateParticipants(18);
		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(standardConfig, particiapnts);
		List<Team> teams = teamsResult.getRegularTeams();
		assertEquals(9, teams.size());

		for (Team team : teams) {
			assertEquals(null, team.getMealClass());
		}

		runningDinnerCalculator.assignRandomMealClasses(teamsResult, standardConfig.getMealClasses());

		assertEquals(3, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass().equals(MealClass.APPETIZER);
			}
		}));

		assertEquals(3, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass().equals(MealClass.MAINCOURSE);
			}
		}));

		assertEquals(3, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass().equals(MealClass.DESSERT);
			}
		}));

		final MealClass dummy = new MealClass("dummy");
		assertEquals(0, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass().equals(dummy);
			}
		}));
	}

	@Test
	public void testInvalidRandomMealClasses() {
		Team team1 = new Team(1);
		Team team2 = new Team(2);
		ArrayList<Team> teamList = new ArrayList<Team>(2);
		teamList.add(team1);
		teamList.add(team2);

		GeneratedTeamsResult generatedTeamsResult = new GeneratedTeamsResult();
		generatedTeamsResult.setRegularTeams(teamList);
		try {
			runningDinnerCalculator.assignRandomMealClasses(generatedTeamsResult, standardConfig.getMealClasses());
			fail("Should never reach here, because Exception should be thrown!");
		}
		catch (NoPossibleRunningDinnerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testBuildSingleVisitationPlan() throws NoPossibleRunningDinnerException {
		List<Participant> participants = generateEqualBalancedParticipants(0);
		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(standardConfig, participants);

		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(9, teamsResult.getRegularTeams().size());

		runningDinnerCalculator.assignRandomMealClasses(teamsResult, standardConfig.getMealClasses());

		runningDinnerCalculator.generateDinnerExecutionPlan(teamsResult, standardConfig);

		List<Team> teams = teamsResult.getRegularTeams();
		for (Team team : teams) {
			assertEquals(2, team.getVisitationPlan().getNumberOfGuests());
			assertEquals(2, team.getVisitationPlan().getNumberOfHosts());
			// assertEquals(team, team.getVisitationPlan().getTeam());
			assertEquals(false, team.getVisitationPlan().getGuestTeams().contains(team));
			assertEquals(false, team.getVisitationPlan().getHostTeams().contains(team));

			Set<Team> guestTeams = team.getVisitationPlan().getGuestTeams();
			checkMealClassNotContained(team, guestTeams);
			Set<Team> hostTeams = team.getVisitationPlan().getHostTeams();
			checkMealClassNotContained(team, hostTeams);
		}
	}

	@Test
	public void testBuildMultipleVisitationPlans() throws NoPossibleRunningDinnerException {
		List<Participant> participants = generateEqualBalancedParticipants(0);
		participants.addAll(generateEqualBalancedParticipants(18));
		participants.addAll(generateEqualBalancedParticipants(36));
		assertEquals(54, participants.size());

		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(standardConfig, participants);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(27, teamsResult.getRegularTeams().size());

		runningDinnerCalculator.assignRandomMealClasses(teamsResult, standardConfig.getMealClasses());
		runningDinnerCalculator.generateDinnerExecutionPlan(teamsResult, standardConfig);

		List<Team> teams = teamsResult.getRegularTeams();
		for (Team team : teams) {
			assertEquals(2, team.getVisitationPlan().getNumberOfGuests());
			assertEquals(2, team.getVisitationPlan().getNumberOfHosts());

			RunningDinnerCalculatorTest.assertDisjunctTeams(team.getVisitationPlan().getHostTeams(),
					team.getVisitationPlan().getGuestTeams(), team);

			Set<Team> guestTeams = team.getVisitationPlan().getGuestTeams();
			checkMealClassNotContained(team, guestTeams);
			Set<Team> hostTeams = team.getVisitationPlan().getHostTeams();
			checkMealClassNotContained(team, hostTeams);

			Set<Team> testingTeams = new HashSet<Team>(hostTeams);
			testingTeams.add(team);
			assertDisjunctMealClasses(testingTeams);

			testingTeams.clear();
			testingTeams.add(team);
			testingTeams.addAll(guestTeams);
			assertDisjunctMealClasses(testingTeams);
		}
	}

	/**
	 * Asserts that the mealclass of the passed team is not contained in the mealclasses of the passed teamsToCheck
	 * 
	 * @param team
	 * @param teamsToCheck
	 */
	protected void checkMealClassNotContained(Team team, Set<Team> teamsToCheck) {
		MealClass referenceMealClass = team.getMealClass();

		for (Team teamToCheck : teamsToCheck) {
			assertFalse(referenceMealClass.equals(teamToCheck.getMealClass()));
		}
	}

	/**
	 * Assume team with team-size of 2
	 * 
	 * @param team
	 * @return
	 */
	protected boolean isDistributionBalanced(final Team team, final RunningDinnerConfig runningDinnerConfig) {
		Set<Participant> teamMembers = team.getTeamMembers();
		if (teamMembers.size() != 2) {
			throw new IllegalArgumentException("Team " + team + " must have exactly two members, but had " + teamMembers.size());
		}
		Participant[] teamMemberArr = teamMembers.toArray(new Participant[2]);

		FuzzyBoolean canHouse1 = runningDinnerConfig.canHost(teamMemberArr[0]);
		FuzzyBoolean canHouse2 = runningDinnerConfig.canHost(teamMemberArr[1]);

		if (canHouse1 == FuzzyBoolean.UNKNOWN && canHouse2 == FuzzyBoolean.UNKNOWN) {
			return false;
		}

		if (canHouse1 == FuzzyBoolean.TRUE) {
			if (canHouse2 == FuzzyBoolean.FALSE) {
				return true;
			}
		}
		else if (canHouse2 == FuzzyBoolean.TRUE) {
			if (canHouse1 == FuzzyBoolean.FALSE) {
				return true;
			}
		}
		else if (canHouse2 == canHouse1) {
			return false;
		}

		throw new RuntimeException("May never reach here!");
	}

	/**
	 * Helper method for quickly generating some arbitrary participants
	 * 
	 * @param numParticipants
	 * @return
	 */
	public static List<Participant> generateParticipants(int numParticipants, int participantNrOffset) {
		List<Participant> result = new ArrayList<Participant>(numParticipants);
		for (int i = 1; i <= numParticipants; i++) {
			int participantNr = i + participantNrOffset;
			Participant participant = new Participant(participantNr);
			participant.setName(ParticipantName.newName().withFirstname("first" + participantNr).andLastname("last" + participantNr));
			result.add(participant);
		}
		return result;
	}

	protected List<Participant> generateParticipants(int numParticipants) {
		return generateParticipants(numParticipants, 0);
	}

	public static void assertDisjunctTeams(Set<Team> hostTeams, Set<Team> guestTeams, Team team) {
		Set<Team> testSet = new HashSet<Team>();
		testSet.addAll(hostTeams);
		testSet.addAll(guestTeams);
		testSet.add(team);
		assertEquals("There exist at least one team duplicate in test-set for visitation-plan of team " + team, hostTeams.size()
				+ guestTeams.size() + 1, testSet.size());
	}

	public static void assertDisjunctMealClasses(Set<Team> teams) {
		Set<MealClass> foundMeals = new HashSet<MealClass>();
		for (Team team : teams) {
			if (foundMeals.contains(team.getMealClass())) {
				fail("Team " + team + " has mealclass which already existed");
			}
			foundMeals.add(team.getMealClass());
		}
	}
}
