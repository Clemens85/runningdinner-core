package org.runningdinner.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.runningdinner.core.util.CoreUtil;

// TODO Comment
// TODO: Use more generic chain model!
public final class TeamDistributor {

	private List<Participant> participants;

	// We cannot directly remove participants randomly while iterating over the participant list (=> ConcExceptions)
	// Thus we use this set to mark the participants as being deleted:
	private Set<Participant> removedParticipantsTmp = new HashSet<Participant>();

	private ArrayDeque<Participant> categoryOneList = new ArrayDeque<Participant>();
	private ArrayDeque<Participant> categoryTwoList = new ArrayDeque<Participant>();

	// private ArrayDeque<Participant> uncategeorizedList = new ArrayDeque<Participant>();
	// private ArrayDeque<Participant> currentCategoryList = categoryOneList;

	public TeamDistributor(List<Participant> participants) {
		this.participants = new ArrayList<Participant>(participants);
	}

	public void distribute(RunningDinnerConfig runningDinnerConfig) {

		boolean hasDistributionPolicy = runningDinnerConfig.isForceEqualDistributedCapacityTeams()
				|| runningDinnerConfig.getGenderAspects() != GenderAspect.IGNORE_GENDER;

		if (!hasDistributionPolicy) {
			// Equally distribute all team-members over the two category-lists:
			CoreUtil.distributeEqually(categoryOneList, participants, categoryTwoList);
			return;
		}

		final int numParticipants = participants.size();
		final int teamSize = runningDinnerConfig.getTeamSize();

		// FuzzyBoolean canHost = runningDinnerConfig.isForceEqualDistributedCapacityTeams() ? FuzzyBoolean.TRUE : FuzzyBoolean.UNKNOWN; //
		// TODO:
		// // Random
		// Gender gender = runningDinnerConfig.getGenderAspects() == GenderAspect.IGNORE_GENDER ? Gender.UNDEFINED : Gender.MALE; // TODO:
		// // Rando

		removedParticipantsTmp.clear();

		for (Iterator<Participant> iter = participants.iterator(); iter.hasNext();) {
			if (teamSize != 2) {
				throw new RuntimeException("Currently we support only team sizes of 2!");
			}

			Participant participant = iter.next();
			if (isParticipantDeleted(participant)) {
				continue;
			}

			removeParticipant(participant); // This participant can now not be used any longer!

			Gender gender = participant.getGender();
			FuzzyBoolean canHost = runningDinnerConfig.canHost(participant);

			canHost = toggleHosting(canHost, runningDinnerConfig.isForceEqualDistributedCapacityTeams());
			gender = toggleGender(gender, runningDinnerConfig.getGenderAspects());

			Participant bestMatchingTeamParticipant = findMatchingParticipant(runningDinnerConfig, canHost, gender);

			// Get random participant from list (which is not yet marked as deleted)
			if (bestMatchingTeamParticipant == null) {
				while (iter.hasNext() && bestMatchingTeamParticipant == null) {
					bestMatchingTeamParticipant = iter.next();
					if (isParticipantDeleted(bestMatchingTeamParticipant)) {
						bestMatchingTeamParticipant = null;
					}
				}
			}

			categoryOneList.add(participant);
			if (bestMatchingTeamParticipant != null) {
				categoryTwoList.add(bestMatchingTeamParticipant);
			}

			removeParticipant(bestMatchingTeamParticipant);
		}

		// for (int i = 0; i < numParticipants; i += 2) {
		//
		// if (teamSize != 2) {
		// throw new RuntimeException("Currently we support only team sizes of 2!");
		// }
		//
		// Participant participant = participants.get(0); // Get first
		// Gender gender = participant.getGender();
		// FuzzyBoolean canHost = runningDinnerConfig.canHost(participant);
		//
		// canHost = toggleHosting(canHost, runningDinnerConfig.isForceEqualDistributedCapacityTeams());
		// gender = toggleGender(gender, runningDinnerConfig.getGenderAspects());
		//
		// Participant bestMatchingTeamParticipant = findMatchingParticipant(runningDinnerConfig, canHost, gender);
		// if (bestMatchingTeamParticipant == null) {
		// bestMatchingTeamParticipant = participants.get(1);
		// }
		//
		// categoryOneList.add(participant);
		// categoryTwoList.add(bestMatchingTeamParticipant);
		//
		// participants.remove(bestMatchingParticipant);
		// }

		// this.currentCategoryList = categoryOneList;
		//
		// for (int i = 0; i < numParticipants; i++) {
		//
		// Participant p = fetchBestMatchingParticipant(runningDinnerConfig);
		// currentCategoryList.add(p);
		//
		// // Swap queues:
		// if (currentCategoryList == categoryOneList) {
		// this.currentCategoryList = categoryTwoList;
		// }
		// else {
		// this.currentCategoryList = categoryOneList;
		// }
		// }
		//
		// if (runningDinnerConfig.getGenderAspects() == GenderAspect.FORCE_SAME_GENDER) {
		// List<Participant> tmpOneList = new ArrayList<Participant>(categoryOneList);
		// List<Participant> tmpTwoList = new ArrayList<Participant>(categoryTwoList);
		// Collections.sort(tmpOneList, new ParticipantGenederSorter(Gender.MALE));
		// Collections.sort(tmpTwoList, new ParticipantGenederSorter(Gender.MALE));
		// categoryOneList = new ArrayDeque<Participant>(tmpOneList);
		// categoryTwoList = new ArrayDeque<Participant>(tmpTwoList);
		// }
	}

	private void removeParticipant(Participant bestMatchingTeamParticipant) {
		this.removedParticipantsTmp.add(bestMatchingTeamParticipant);
	}

	private boolean isParticipantDeleted(Participant p) {
		return this.removedParticipantsTmp.contains(p);
	}

	private Participant findMatchingParticipant(RunningDinnerConfig runningDinnerConfig, FuzzyBoolean canHostCondition,
			Gender genderCondition) {

		Participant bestResult = null;

		Participant firstFallback = null;
		Participant secondFallback = null;

		for (Participant p : participants) {

			if (isParticipantDeleted(p)) {
				continue;
			}

			final FuzzyBoolean canHost = runningDinnerConfig.canHost(p);
			final Gender gender = p.getGender();

			if ((canHost == canHostCondition || canHostCondition == FuzzyBoolean.UNKNOWN)
					&& (gender == genderCondition || genderCondition == Gender.UNDEFINED)) {
				bestResult = p;
				break;
			}

			if (canHostCondition == FuzzyBoolean.UNKNOWN && Gender.UNDEFINED == genderCondition) {
				bestResult = p;
				break;
			}

			if (canHost == canHostCondition) {
				if (genderCondition == Gender.UNDEFINED) {
					bestResult = p;
					break;
				}
				firstFallback = p;
				continue;
			}
			else if (gender == genderCondition) {
				if (canHostCondition == FuzzyBoolean.UNKNOWN) {
					bestResult = p;
					break;
				}
				secondFallback = p;
				continue;
			}
		}

		// No ideal participant found: Try to take one of the fallbacks
		if (bestResult == null) {
			if (firstFallback != null) {
				bestResult = firstFallback;
			}
			else if (secondFallback != null) {
				bestResult = secondFallback;
			}
		}

		return bestResult;
	}

	static FuzzyBoolean toggleHosting(FuzzyBoolean canHost, boolean forceDistribution) {
		if (FuzzyBoolean.UNKNOWN == canHost || forceDistribution == false) {
			return FuzzyBoolean.UNKNOWN;
		}
		else if (FuzzyBoolean.TRUE == canHost) {
			return FuzzyBoolean.FALSE;
		}
		else { // False:
			return FuzzyBoolean.TRUE;
		}
	}

	static Gender toggleGender(Gender gender, GenderAspect genderAspect) {
		if (Gender.UNDEFINED == gender || GenderAspect.IGNORE_GENDER == genderAspect) {
			return Gender.UNDEFINED;
		}
		if (genderAspect == GenderAspect.FORCE_GENDER_MIX) {
			if (Gender.MALE == gender) {
				return Gender.FEMALE;
			}
			else { // Female:
				return Gender.MALE;
			}
		}
		// For same gender:
		return gender;
	}

	// protected boolean isCurrentListFirstOne() {
	// return (currentCategoryList == categoryOneList);
	// }
	//
	// protected boolean isCurrentListSecondOne() {
	// return !isCurrentListFirstOne();
	// }

	// /**
	// *
	// * @param runningDinnerConfig
	// * @param firstList If set to true, choose a participant for first list (canHost==true, Gender==male, ...)
	// * @return
	// */
	// private Participant fetchBestMatchingParticipant(RunningDinnerConfig runningDinnerConfig) {
	//
	// Participant bestResult = null;
	//
	// Participant firstFallback = null;
	// Participant secondFallback = null;
	//
	// final GenderAspect genderAspects = runningDinnerConfig.getGenderAspects();
	// final boolean capacityBasedDistribution = runningDinnerConfig.isForceEqualDistributedCapacityTeams();
	//
	// for (Participant p : participants) {
	//
	// boolean firstCriteriaMatched = false;
	// boolean secondCriteriaMatched = false;
	//
	// if (!capacityBasedDistribution) {
	// firstCriteriaMatched = true;
	// }
	// else {
	// FuzzyBoolean canHost = runningDinnerConfig.canHost(p);
	// if (canHost == FuzzyBoolean.TRUE && isCurrentListFirstOne()) {
	// firstCriteriaMatched = true;
	// firstFallback = p;
	// }
	// else if (canHost == FuzzyBoolean.FALSE && isCurrentListSecondOne()) {
	// firstCriteriaMatched = true;
	// firstFallback = p;
	// }
	// }
	//
	// if (GenderAspect.IGNORE_GENDER == genderAspects) {
	// secondCriteriaMatched = true;
	// }
	// else {
	//
	// final Gender gender = p.getGender();
	//
	// if (GenderAspect.FORCE_SAME_GENDER == genderAspects) {
	// Participant lastParticipant = null;
	// if (isCurrentListFirstOne() && !categoryTwoList.isEmpty()) {
	// lastParticipant = categoryTwoList.getLast();
	// }
	// else if (isCurrentListSecondOne() && !categoryOneList.isEmpty()) {
	// lastParticipant = categoryOneList.getLast();
	// }
	// if (lastParticipant != null && gender == lastParticipant.getGender()) {
	// secondCriteriaMatched = true;
	// secondFallback = p;
	// }
	// }
	// else { // Mix Gender, by just setting a different gender on the other list:
	// if (gender == Gender.MALE && isCurrentListFirstOne()) {
	// secondCriteriaMatched = true;
	// secondFallback = p;
	// }
	// else if (gender == Gender.FEMALE && isCurrentListSecondOne()) {
	// secondCriteriaMatched = true;
	// secondFallback = p;
	// }
	// }
	// }
	//
	// if (firstCriteriaMatched && secondCriteriaMatched) {
	// bestResult = p;
	// break;
	// }
	//
	// }
	//
	// // No ideal participant found: Try to take one of the fallbacks
	// if (bestResult == null) {
	// if (firstFallback != null) {
	// bestResult = firstFallback;
	// }
	// else if (secondFallback != null) {
	// bestResult = secondFallback;
	// }
	// }
	//
	// // Still null?
	// if (bestResult == null) {
	// // Take just one participant (there is no good choice)
	// bestResult = participants.get(0);
	// }
	//
	// participants.remove(bestResult);
	// return bestResult;
	// }
	//
	// private void sortByGenderAspect(GenderAspect genderDistribution) {
	//
	// List<Participant> tmpOneList = new ArrayList<Participant>(categoryOneList);
	// List<Participant> tmpTwoList = new ArrayList<Participant>(categoryTwoList);
	//
	// if (!CoreUtil.isEmpty(uncategeorizedList)) {
	// // Take not used participants so far and enrich the male/female lists accordingly:
	// final int numUncategorizedParticipants = uncategeorizedList.size();
	// for (int i = 0; i < numUncategorizedParticipants; i++) {
	// Participant uncategorizedParticipant = uncategeorizedList.peek();
	//
	// if (Gender.MALE == uncategorizedParticipant.getGender()) {
	// uncategorizedParticipant = uncategeorizedList.poll(); // Really remove the element now!
	// tmpOneList.add(uncategorizedParticipant);
	// }
	// else if (Gender.FEMALE == uncategorizedParticipant.getGender()) {
	// uncategorizedParticipant = uncategeorizedList.poll(); // Really remove the element now!
	// tmpTwoList.add(uncategorizedParticipant);
	// }
	// }
	//
	// if (!CoreUtil.isEmpty(uncategeorizedList)) {
	// // For these ones the gender was unknown, hence just distribute them equally:
	// CoreUtil.distributeEqually(tmpOneList, uncategeorizedList, tmpTwoList);
	// }
	// }
	//
	// if (GenderAspect.FORCE_GENDER_MIX == genderDistribution) {
	// Collections.sort(tmpOneList, new ParticipantGenederSorter(Gender.MALE));
	// Collections.sort(tmpTwoList, new ParticipantGenederSorter(Gender.FEMALE));
	// }
	// else {
	// Collections.sort(tmpOneList, new ParticipantGenederSorter(Gender.MALE));
	// Collections.sort(tmpTwoList, new ParticipantGenederSorter(Gender.MALE));
	// }
	//
	// categoryOneList = new ArrayDeque<Participant>(tmpOneList);
	// categoryTwoList = new ArrayDeque<Participant>(tmpTwoList);
	// }

	static class ParticipantGenederSorter implements Comparator<Participant> {

		private Gender leadingGender;

		public ParticipantGenederSorter(Gender leadingGender) {
			this.leadingGender = leadingGender;
		}

		@Override
		public int compare(Participant p1, Participant p2) {
			Gender gender1 = p1.getGender();
			Gender gender2 = p2.getGender();

			if (gender1 == leadingGender) {
				return 1;
			}
			else if (gender2 == leadingGender) {
				return -1;
			}
			return 0;
		}
	}

	public Queue<Participant> getCategoryOneList() {
		return categoryOneList;
	}

	public Queue<Participant> getCategoryTwoList() {
		return categoryTwoList;
	}

}
