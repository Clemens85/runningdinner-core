package org.runningdinner.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.runningdinner.core.RunningDinnerService.BuildTeamsResult;

public class TeamBuilderTest {

	private RunningDinnerService runningDinnerSvc = new RunningDinnerService();

	private RunningDinnerConfig standardConfig = RunningDinnerConfig.newConfigurer().withEqualDistributedCapacityTeams(true).build();
	private RunningDinnerConfig standardConfigWithoutDistributing = RunningDinnerConfig.newConfigurer().withEqualDistributedCapacityTeams(
			false).withGenderAspects(GenderAspects.IGNORE_GENDER).build();
	private RunningDinnerConfig customConfig = RunningDinnerConfig.newConfigurer().havingMeal(MealClass.APPETIZER).havingMeal(
			MealClass.MAINCOURSE).build();

	@Test
	public void testInvalidConditionWithDefaults() {
		List<TeamMember> teamMembers = generateTeamMembers(2);
		try {
			runningDinnerSvc.buildTeams(standardConfig, teamMembers);
			fail("Should never reach here, because Exception should be thrown!");
		}
		catch (NoPossibleRunningDinnerException e) {
			assertTrue(true);
		}
	}

	@Test
	public void testEvenWorkedOutTeamsWithoutDistributing() throws NoPossibleRunningDinnerException {
		List<TeamMember> teamMembers = generateTeamMembers(12);

		BuildTeamsResult teamsResult = runningDinnerSvc.buildTeams(standardConfigWithoutDistributing, teamMembers);
		assertEquals(false, teamsResult.hasNotAssignedMembers());
		assertEquals(6, teamsResult.getRegularTeams().size());

		System.out.println("*** testEvenWorkedOutTeamsWithoutDistributing ***");
		for (Team team : teamsResult.getRegularTeams()) {
			assertEquals(standardConfigWithoutDistributing.getTeamSize(), team.getTeamMembers().size());
			System.out.println(team);
		}
	}

	@Test
	public void testEvenWorkedOutTeamsWithDistributing() throws NoPossibleRunningDinnerException {
		List<TeamMember> teamMembers = generateTeamMembers(12);
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

		BuildTeamsResult teamsResult = runningDinnerSvc.buildTeams(standardConfig, teamMembers);
		assertEquals(false, teamsResult.hasNotAssignedMembers());
		assertEquals(6, teamsResult.getRegularTeams().size());

		System.out.println("*** testEvenWorkedOutTeamsWithDistributing ***");
		for (Team team : teamsResult.getRegularTeams()) {

			Set<TeamMember> membersToTest = team.getTeamMembers();
			assertEquals(2, membersToTest.size()); // Should be 2

			TeamMember[] teamMemberArr = membersToTest.toArray(new TeamMember[2]);
			FuzzyBoolean canHouse1 = teamMemberArr[0].canHouse(standardConfig);
			FuzzyBoolean canHouse2 = teamMemberArr[1].canHouse(standardConfig);

			assertFalse("Should never return UNKNOWN, but either TRUE or FALSE", canHouse1 == FuzzyBoolean.UNKNOWN);
			assertFalse("Should never return UNKNOWN, but either TRUE or FALSE", canHouse2 == FuzzyBoolean.UNKNOWN);
			if (canHouse1 == FuzzyBoolean.TRUE) {
				assertEquals(FuzzyBoolean.FALSE, canHouse2);
			}
			else if (canHouse2 == FuzzyBoolean.TRUE) {
				assertEquals(FuzzyBoolean.FALSE, canHouse1);
			}
			else {
				throw new RuntimeException("Inconsistent state for canHouse1 " + canHouse1 + " and " + canHouse2);
			}

			System.out.println(team + " - canHouse (" + canHouse1 + ", " + canHouse2 + ")");
		}
	}

	@Test
	public void testNotAssignedTeamMembers() throws NoPossibleRunningDinnerException {
		List<TeamMember> teamMembers = generateTeamMembers(7);
		BuildTeamsResult result = runningDinnerSvc.buildTeams(standardConfig, teamMembers);
		assertEquals(true, result.hasNotAssignedMembers());
		assertEquals(1, result.getNotAssignedMembers().size());
		assertEquals(3, result.getRegularTeams().size());

		TeamMember notAssignedMember = result.getNotAssignedMembers().iterator().next();
		for (Team regularTeam : result.getRegularTeams()) {
			assertEquals(2, regularTeam.getTeamMembers().size());
			assertEquals(false, regularTeam.getTeamMembers().contains(notAssignedMember));
		}
	}

	@Test
	public void testCustomConfigTeamBuilding() {

	}

	protected List<TeamMember> generateTeamMembers(int numMembers) {
		List<TeamMember> result = new ArrayList<TeamMember>(numMembers);
		for (int i = 1; i <= numMembers; i++) {
			TeamMember member = new TeamMember(i);
			member.setName(MemberName.newBuilder().withSurname("name" + i).build());
			result.add(member);
		}
		return result;
	}
}
