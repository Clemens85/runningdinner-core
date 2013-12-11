package org.runningdinner.core;

import java.util.Collections;
import java.util.List;

public class GeneratedTeamsResult {

	private List<Team> regularTeams;

	private List<Participant> notAssignedParticipants;

	private TeamCombinationInfo teamCombinationInfo;

	public List<Team> getRegularTeams() {
		if (regularTeams == null) {
			return Collections.emptyList();
		}
		return regularTeams;
	}

	public List<Participant> getNotAssignedParticipants() {
		if (notAssignedParticipants == null) {
			return Collections.emptyList();
		}
		return notAssignedParticipants;
	}

	public TeamCombinationInfo getTeamCombinationInfo() {
		return teamCombinationInfo;
	}

	public boolean hasNotAssignedParticipants() {
		return notAssignedParticipants != null && notAssignedParticipants.size() > 0;
	}

	void setRegularTeams(List<Team> regularTeams) {
		this.regularTeams = regularTeams;
	}

	void setNotAssignedParticipants(List<Participant> notAssignedMembers) {
		this.notAssignedParticipants = notAssignedMembers;
	}

	void setTeamCombinationInfo(TeamCombinationInfo teamCombinationInfo) {
		this.teamCombinationInfo = teamCombinationInfo;
	}

}