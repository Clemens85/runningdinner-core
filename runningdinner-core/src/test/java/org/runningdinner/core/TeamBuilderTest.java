package org.runningdinner.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Test;

public class TeamBuilderTest {

	private RunningDinnerService runningDinnerSvc = new RunningDinnerService();

	private RunningDinnerConfig standardConfig = RunningDinnerConfig.newConfigurer().withEqualDistributedCapacityTeams(true).build();
	private RunningDinnerConfig standardConfigWithoutDistributing = RunningDinnerConfig.newConfigurer().withEqualDistributedCapacityTeams(
			false).withGenderAspects(GenderAspects.IGNORE_GENDER).build();
	private RunningDinnerConfig customConfig = RunningDinnerConfig.newConfigurer().havingMeal(MealClass.APPETIZER).havingMeal(
			MealClass.MAINCOURSE).build();

	@Test
	public void testInvalidConditionWithDefaults() {
		List<Participant> teamMembers = generateTeamMembers(2);
		try {
			runningDinnerSvc.generateTeams(standardConfig, teamMembers);
			fail("Should never reach here, because Exception should be thrown!");
		}
		catch (NoPossibleRunningDinnerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testTeamsWithoutDistributing() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateTeamMembers(12);

		GeneratedTeamsResult teamsResult = runningDinnerSvc.generateTeams(standardConfigWithoutDistributing, teamMembers);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(6, teamsResult.getRegularTeams().size());

		System.out.println("*** testTeamsWithoutDistributing ***");
		for (Team team : teamsResult.getRegularTeams()) {
			assertEquals(standardConfigWithoutDistributing.getTeamSize(), team.getTeamMembers().size());
			System.out.println(team);
		}
	}

	@Test
	public void testTeamsWithBalancedDistributing() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateTeamMembers(12);
		teamMembers.get(0).setNumSeats(6);
		teamMembers.get(1).setNumSeats(3);
		teamMembers.get(2).setNumSeats(4);
		teamMembers.get(3).setNumSeats(7);
		teamMembers.get(4).setNumSeats(10);
		teamMembers.get(5).setNumSeats(6);

		teamMembers.get(6).setNumSeats(6);
		teamMembers.get(7).setNumSeats(3);
		teamMembers.get(8).setNumSeats(4);
		teamMembers.get(9).setNumSeats(5);
		teamMembers.get(10).setNumSeats(3);
		teamMembers.get(11).setNumSeats(6);

		GeneratedTeamsResult teamsResult = runningDinnerSvc.generateTeams(standardConfig, teamMembers);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(6, teamsResult.getRegularTeams().size());

		System.out.println("*** testTeamsWithBalancedDistributing ***");
		for (Team team : teamsResult.getRegularTeams()) {

			assertEquals(true, isDistributionBalanced(team, standardConfig));

			System.out.println(team + " - canHouse: " + team.getHousingDump(standardConfig));
		}
	}

	@Test
	public void testTeamsWithUnbalancedDistributing() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateTeamMembers(12);
		teamMembers.get(0).setNumSeats(6);
		teamMembers.get(1).setNumSeats(6);
		teamMembers.get(2).setNumSeats(6);
		teamMembers.get(3).setNumSeats(1);
		teamMembers.get(4).setNumSeats(1);
		teamMembers.get(5).setNumSeats(1);

		teamMembers.get(6).setNumSeats(1);
		teamMembers.get(7).setNumSeats(1);
		teamMembers.get(8).setNumSeats(1);
		teamMembers.get(9).setNumSeats(1);
		teamMembers.get(10).setNumSeats(1);
		teamMembers.get(11).setNumSeats(6);

		GeneratedTeamsResult teamsResult = runningDinnerSvc.generateTeams(standardConfig, teamMembers);
		assertEquals(false, teamsResult.hasNotAssignedParticipants());
		assertEquals(6, teamsResult.getRegularTeams().size());

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

			System.out.println(team + " - canHouse :" + team.getHousingDump(standardConfig));
		}

		assertEquals(4, numBalancedTeams);
		assertEquals(2, numUnbalancedTeams);
	}

	@Test
	public void testNotAssignedTeamMembers() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateTeamMembers(7);
		GeneratedTeamsResult result = runningDinnerSvc.generateTeams(standardConfig, teamMembers);
		assertEquals(true, result.hasNotAssignedParticipants());
		assertEquals(1, result.getNotAssignedParticipants().size());
		assertEquals(3, result.getRegularTeams().size());

		Participant notAssignedMember = result.getNotAssignedParticipants().iterator().next();
		for (Team regularTeam : result.getRegularTeams()) {
			assertEquals(2, regularTeam.getTeamMembers().size());
			assertEquals(false, regularTeam.getTeamMembers().contains(notAssignedMember));
		}
	}

	@Test
	public void testCustomConfigTeamBuilding() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateTeamMembers(5);
		GeneratedTeamsResult teamsResult = runningDinnerSvc.generateTeams(customConfig, teamMembers);
		assertEquals(true, teamsResult.hasNotAssignedParticipants());
		assertEquals(1, teamsResult.getNotAssignedParticipants().size());
		assertEquals(5, teamsResult.getNotAssignedParticipants().get(0).getParticipantNumber()); // Ensure that last user is the one not
																									// assigned
		assertEquals(2, teamsResult.getRegularTeams().size());

		System.out.println("*** testCustomConfigTeamBuilding ***");
		for (Team team : teamsResult.getRegularTeams()) {
			assertEquals(customConfig.getTeamSize(), team.getTeamMembers().size());
			System.out.println(team);
		}
	}

	@Test
	public void testRandomMealClasses() throws NoPossibleRunningDinnerException {
		List<Participant> teamMembers = generateTeamMembers(12);
		GeneratedTeamsResult teamsResult = runningDinnerSvc.generateTeams(standardConfig, teamMembers);
		List<Team> teams = teamsResult.getRegularTeams();
		assertEquals(6, teams.size());

		for (Team team : teams) {
			assertEquals(null, team.getMealClass());
		}

		runningDinnerSvc.assignRandomMealClasses(teamsResult, standardConfig.getMealClasses());

		assertEquals(2, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass() == MealClass.APPETIZER;
			}
		}));

		assertEquals(2, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass() == MealClass.MAINCOURSE;
			}
		}));

		assertEquals(2, CollectionUtils.countMatches(teams, new Predicate() {
			@Override
			public boolean evaluate(Object obj) {
				Team team = (Team)obj;
				return team.getMealClass() == MealClass.DESSERT;
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
			runningDinnerSvc.assignRandomMealClasses(generatedTeamsResult, standardConfig.getMealClasses());
			fail("Should never reach here, because Exception should be thrown!");
		}
		catch (NoPossibleRunningDinnerException e) {
			assertTrue(true);
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

		FuzzyBoolean canHouse1 = teamMemberArr[0].canHouse(runningDinnerConfig);
		FuzzyBoolean canHouse2 = teamMemberArr[1].canHouse(runningDinnerConfig);

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
	 * @param numMembers
	 * @return
	 */
	protected List<Participant> generateTeamMembers(int numMembers) {
		List<Participant> result = new ArrayList<Participant>(numMembers);
		for (int i = 1; i <= numMembers; i++) {
			Participant member = new Participant(i);
			member.setName(ParticipantName.newBuilder().withSurname("name" + i).build());
			result.add(member);
		}
		return result;
	}
}
