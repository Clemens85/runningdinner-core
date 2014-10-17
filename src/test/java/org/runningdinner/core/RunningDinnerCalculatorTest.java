package org.runningdinner.core;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;
import org.runningdinner.core.dinnerplan.TeamRouteBuilder;
import org.runningdinner.core.test.helper.Configurations;

public class RunningDinnerCalculatorTest {

	private RunningDinnerCalculator runningDinnerCalculator = new RunningDinnerCalculator();

	@Test
	public void testInvalidConditionWithDefaults() {
		List<Participant> teamMembers = ParticipantGenerator.generateParticipants(2);
		try {
			runningDinnerCalculator.generateTeams(Configurations.standardConfig, teamMembers);
			fail("Should never reach here, because Exception should be thrown!");
		}
		catch (NoPossibleRunningDinnerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCustomConfigTeamBuilding() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = ParticipantGenerator.generateParticipants(13);
		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(Configurations.customConfig, teamMembers);
		assertEquals(true, teamsResult.hasNotAssignedParticipants());
		assertEquals(1, teamsResult.getNotAssignedParticipants().size());
		assertEquals(13, teamsResult.getNotAssignedParticipants().get(0).getParticipantNumber()); // Ensure that last user is the one not
																									// assigned
		assertEquals(6, teamsResult.getRegularTeams().size());

		System.out.println("*** testCustomConfigTeamBuilding ***");
		for (Team team : teamsResult.getRegularTeams()) {
			assertEquals(Configurations.customConfig.getTeamSize(), team.getTeamMembers().size());
			System.out.println(team);
		}
	}

	@Test
	public void testRandomMealClasses() throws NoPossibleRunningDinnerException {
		List<Participant> particiapnts = ParticipantGenerator.generateParticipants(18);
		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(Configurations.standardConfig, particiapnts);
		List<Team> teams = teamsResult.getRegularTeams();
		assertEquals(9, teams.size());

		for (Team team : teams) {
			assertEquals(null, team.getMealClass());
		}

		runningDinnerCalculator.assignRandomMealClasses(teamsResult, Configurations.standardConfig.getMealClasses());

		assertEquals(3, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass().equals(MealClass.APPETIZER());
			}
		}));

		assertEquals(3, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass().equals(MealClass.MAINCOURSE());
			}
		}));

		assertEquals(3, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass().equals(MealClass.DESSERT());
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
			runningDinnerCalculator.assignRandomMealClasses(generatedTeamsResult, Configurations.standardConfig.getMealClasses());
			fail("Should never reach here, because Exception should be thrown!");
		}
		catch (NoPossibleRunningDinnerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testBuildSingleVisitationPlan() throws NoPossibleRunningDinnerException {
		List<Participant> participants = ParticipantGenerator.generateEqualBalancedParticipants(0);
		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(Configurations.standardConfig, participants);

		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(9, teamsResult.getRegularTeams().size());

		runningDinnerCalculator.assignRandomMealClasses(teamsResult, Configurations.standardConfig.getMealClasses());

		runningDinnerCalculator.generateDinnerExecutionPlan(teamsResult, Configurations.standardConfig);

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
		List<Participant> participants = ParticipantGenerator.generateEqualBalancedParticipants(0);
		participants.addAll(ParticipantGenerator.generateEqualBalancedParticipants(18));
		participants.addAll(ParticipantGenerator.generateEqualBalancedParticipants(36));
		assertEquals(54, participants.size());

		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(Configurations.standardConfig, participants);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(27, teamsResult.getRegularTeams().size());

		runningDinnerCalculator.assignRandomMealClasses(teamsResult, Configurations.standardConfig.getMealClasses());
		runningDinnerCalculator.generateDinnerExecutionPlan(teamsResult, Configurations.standardConfig);

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

	@Test
	public void testBuildVisitationPlansWith21Teams() throws NoPossibleRunningDinnerException {

		int numTeams = 21;
		List<Participant> participants = ParticipantGenerator.generateParticipants(numTeams * 2);
		ParticipantGenerator.distributeSeatsEqualBalanced(participants, 6);

		RunningDinnerConfig config = RunningDinnerConfig.newConfigurer().withEqualDistributedCapacityTeams(true).withTeamSize(2).withGenderAspects(
				GenderAspect.FORCE_GENDER_MIX).build();
		GeneratedTeamsResult teamsResult = runningDinnerCalculator.generateTeams(config, participants);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(numTeams, teamsResult.getRegularTeams().size());

		runningDinnerCalculator.assignRandomMealClasses(teamsResult, config.getMealClasses());
		runningDinnerCalculator.generateDinnerExecutionPlan(teamsResult, config);
		List<Team> teams = teamsResult.getRegularTeams();

		for (Team team : teams) {
			// Ensure that every team visit two guest teams and that every team is hoster for 2 other teams.
			assertEquals(2, team.getVisitationPlan().getNumberOfGuests());
			assertEquals(2, team.getVisitationPlan().getNumberOfHosts());

			RunningDinnerCalculatorTest.assertDisjunctTeams(team.getVisitationPlan().getHostTeams(),
					team.getVisitationPlan().getGuestTeams(), team);

			Set<Team> guestTeams = team.getVisitationPlan().getGuestTeams();
			checkMealClassNotContained(team, guestTeams);
			Set<Team> hostTeams = team.getVisitationPlan().getHostTeams();
			checkMealClassNotContained(team, hostTeams);

			Collection<Team> crossedTeams = TeamRouteBuilder.getAllCrossedTeams(team);
			// Expect that this team sees 6 other teams (no dublettes)
			assertThat(new HashSet<Team>(crossedTeams), hasSize(6));
			// This team must not occur in the crossed teams
			assertThat(crossedTeams,not(hasItem(team)));
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
	 * Assert that passed teamToTest does not occur in the passed hostTeams and guestTeams
	 * 
	 * @param hostTeams
	 * @param guestTeams
	 * @param teamToTest
	 */
	public static void assertDisjunctTeams(Set<Team> hostTeams, Set<Team> guestTeams, Team teamToTest) {
		Set<Team> testSet = new HashSet<Team>();
		testSet.addAll(hostTeams);
		testSet.addAll(guestTeams);
		testSet.add(teamToTest);
		assertEquals("There exist at least one team duplicate in test-set for visitation-plan of team " + teamToTest, hostTeams.size()
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
