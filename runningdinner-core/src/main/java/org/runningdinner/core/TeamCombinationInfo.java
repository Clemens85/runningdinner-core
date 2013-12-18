package org.runningdinner.core;

/**
 * Simple helper object for saving information about combining teams
 * 
 * @author i01002492
 * 
 */
public class TeamCombinationInfo {

	private int numRemaindingTeams;
	private int teamSegmentSize;

	protected TeamCombinationInfo(int teamSegmentSize, int numRemaindingTeams) {
		this.teamSegmentSize = teamSegmentSize;
		this.numRemaindingTeams = numRemaindingTeams;
	}

	/**
	 * The number of teams that must be excluded for building a complete running dinner execution plan.<br>
	 * This must be once computed based on the number of possible teams and the number of meals to cook.
	 * 
	 * @return
	 */
	public int getNumRemaindingTeams() {
		return numRemaindingTeams;
	}

	/**
	 * The team segment size is the exact number of teams needed for building one dinner execution plan that satifies the running dinner
	 * rules.<br>
	 * A complete running dinner consists of N plans (with every plan containing {@link #teamSegmentSize} teams)
	 * 
	 * @return
	 */
	public int getTeamSegmentSize() {
		return teamSegmentSize;
	}

	@Override
	public String toString() {
		return "TeamCombinationInfo [numRemaindingTeams=" + numRemaindingTeams + ", teamSegmentSize=" + teamSegmentSize + "]";
	}

}
