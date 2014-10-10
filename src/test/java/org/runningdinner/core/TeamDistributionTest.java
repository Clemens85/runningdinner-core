package org.runningdinner.core;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import org.junit.Test;
import org.runningdinner.core.test.helper.GenderPredicate;
import org.runningdinner.core.test.helper.MinNumSeatsPredicate;

import com.google.common.collect.Collections2;

public class TeamDistributionTest {

	@Test
	public void testMixedGenderDistributionOnly() {
		List<Participant> participants = ParticipantGenerator.generateParticipants(8);
		ParticipantGenerator.distributeGender(participants, 3, 5);

		assertEquals(3, getNumParticipantsWithGender(participants, Gender.MALE));
		assertEquals(5, getNumParticipantsWithGender(participants, Gender.FEMALE));

		TeamDistributor teamDistributor = new TeamDistributor(participants);
		teamDistributor.distribute(getConfig(GenderAspect.FORCE_GENDER_MIX));
		Queue<Participant> categoryOneList = teamDistributor.getCategoryOneList();
		Queue<Participant> categoryTwoList = teamDistributor.getCategoryTwoList();

		// assertEquals(4, categoryOneList.size());
		// assertEquals(5, categoryTwoList.size());
		assertEquals(8, categoryOneList.size() + categoryTwoList.size());

		// The first 3 participants in each list have mixed genders
		for (int i = 0; i < 3; i++) {
			Participant p1 = categoryOneList.poll();
			Participant p2 = categoryTwoList.poll();
			assertEquals(false, p1.getGender().equals(p2.getGender()));
		}
	}

	@Test
	public void testSameGenderDistributionOnly() {

		List<Participant> participants = ParticipantGenerator.generateParticipants(8);
		ParticipantGenerator.distributeGender(participants, 3, 5);

		TeamDistributor teamDistributor = new TeamDistributor(participants);
		teamDistributor.distribute(getConfig(GenderAspect.FORCE_SAME_GENDER));
		Queue<Participant> categoryOneList = teamDistributor.getCategoryOneList();
		Queue<Participant> categoryTwoList = teamDistributor.getCategoryTwoList();

		assertEquals(8, categoryOneList.size() + categoryTwoList.size());

		int sameGenderCount = 0;
		while (true) {
			Participant p1 = categoryOneList.poll();
			Participant p2 = categoryTwoList.poll();
			if (p1 == null && p2 == null) {
				break;
			}
			if (p1 == null || p2 == null) {
				continue;
			}

			boolean sameGender = p1.getGender().equals(p2.getGender());
			if (sameGender) {
				sameGenderCount++;
			}
		}

		// 3 males and 5 females => we should have 3 same gender "teams"
		assertEquals("Expected 3 same gender teams", 3, sameGenderCount);
	}

	@Test
	public void testCapacityWithMixedGenderDistribution() {
		// Generate 10 participants which contain 6 participants that have enough seats
		List<Participant> participants = ParticipantGenerator.generateParticipants(10);
		ParticipantGenerator.distributeSeats(participants, 6, 6);

		// Ensure that we have 6 participants with at least 6 seats:
		assertEquals(6, Collections2.filter(participants, new MinNumSeatsPredicate(6)).size());

		RunningDinnerConfig config = getConfig(true, GenderAspect.FORCE_GENDER_MIX);

		// Now we have 6 hosting participants, 4 non-hosting participants

		int cnt = 0;
		int numFemalesOnNonHostingParticipants = 0;
		for (Participant p : participants) {
			// 3 males, 3 females:
			if (FuzzyBoolean.TRUE == config.canHost(p)) {
				p.setGender(cnt % 2 == 0 ? Gender.MALE : Gender.FEMALE);
				cnt++;
			}
			else {
				// Set three non-hosting participants to female, one to male:
				if (numFemalesOnNonHostingParticipants++ < 3) {
					p.setGender(Gender.FEMALE);
				}
				else {
					p.setGender(Gender.MALE);
				}
			}
		}

		// Add a little bit randomness:
		Collections.shuffle(participants);

		assertEquals(4, Collections2.filter(participants, GenderPredicate.MALE_GENDER_PREDICATE).size());
		assertEquals(6, Collections2.filter(participants, GenderPredicate.FEMALE_GENDER_PREDICATE).size());

		TeamDistributor teamDistributor = new TeamDistributor(participants);
		teamDistributor.distribute(config);

		// 6 hosting participants (3 males, 3 females), and 4 non hosting participants (3 females, 1 male):
		// This should give us 3 perfect teams with both criterias met up and, 1 team with the mixed gender condition met up, and 1 team
		// with two females:
		Queue<Participant> categoryOneList = teamDistributor.getCategoryOneList();
		Queue<Participant> categoryTwoList = teamDistributor.getCategoryTwoList();

		int numPerfectTeams = 0;
		int numMixedGenderTeams = 0;
		int numGoodHostingDistributionTeams = 0;

		StringBuilder teamsDump = new StringBuilder();

		while (true) {
			Participant p1 = categoryOneList.poll();
			Participant p2 = categoryTwoList.poll();
			if (p1 == null && p2 == null) {
				break;
			}

			if (p1 == null || p2 == null) {
				continue;
			}

			teamsDump.append(p1.getParticipantNumber()).append(" (").append(p1.getGender()).append(", ").append(p1.getNumSeats()).append(
					") <-> ");
			teamsDump.append(p2.getParticipantNumber()).append(" (").append(p2.getGender()).append(", ").append(p2.getNumSeats()).append(
					")");
			teamsDump.append("\r\n");

			boolean mixedGender = p1.getGender() != p2.getGender();
			boolean canHost = config.canHost(p1) == FuzzyBoolean.TRUE || config.canHost(p2) == FuzzyBoolean.TRUE;

			if (mixedGender && canHost) {
				numPerfectTeams++;
				continue;
			}

			if (mixedGender) {
				numMixedGenderTeams++;
				continue;
			}

			if (canHost) {
				numGoodHostingDistributionTeams++;
			}
		}

		System.out.println(teamsDump);

		assertEquals("Expected alltogehter 5 teams", 5, numPerfectTeams + numGoodHostingDistributionTeams + numMixedGenderTeams);
		assertEquals("Expected 4 perfect teams (distribution and gender mixed)", 4, numPerfectTeams);
		assertEquals("Expected 1 team that has at least a correct hosting distribution ", 1, numGoodHostingDistributionTeams);
		assertEquals("Expected 0 teams with only mixed gender distribution ", 0, numMixedGenderTeams);

	}

	protected RunningDinnerConfig getConfig(GenderAspect genderAspect) {
		RunningDinnerConfig config = RunningDinnerConfig.newConfigurer().withGenderAspects(genderAspect).havingMeals(
				Arrays.asList(MealClass.MAINCOURSE(), MealClass.APPETIZER(), MealClass.DESSERT())).withTeamSize(2).build();
		return config;
	}

	protected RunningDinnerConfig getConfig(boolean forceEqualDistributedTeams, GenderAspect genderAspect) {
		RunningDinnerConfig config = RunningDinnerConfig.newConfigurer().withGenderAspects(genderAspect).havingMeals(
				Arrays.asList(MealClass.MAINCOURSE(), MealClass.APPETIZER(), MealClass.DESSERT())).withTeamSize(2).withEqualDistributedCapacityTeams(
				forceEqualDistributedTeams).build();
		return config;
	}

	protected int getNumParticipantsWithGender(Collection<Participant> participants, Gender gender) {
		int result = 0;
		for (Participant p : participants) {
			if (p.getGender().equals(gender)) {
				result++;
			}
		}
		return result;
	}
}
