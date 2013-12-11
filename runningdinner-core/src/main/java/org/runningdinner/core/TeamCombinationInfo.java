package org.runningdinner.core;

public class TeamCombinationInfo {

	private int numTeamSegments;
	private int numRemaindingTeams;
	private int teamSegmentSize;

	protected TeamCombinationInfo(int teamSegmentSize, int numTeamSegments, int numRemaindingTeams) {
		this.teamSegmentSize = teamSegmentSize;
		this.numTeamSegments = numTeamSegments;
		this.numRemaindingTeams = numRemaindingTeams;
	}

	// public int getNumTeamSegments() {
	// return numTeamSegments;
	// }

	public int getNumRemaindingTeams() {
		return numRemaindingTeams;
	}

	public int getTeamSegmentSize() {
		return teamSegmentSize;
	}

}
