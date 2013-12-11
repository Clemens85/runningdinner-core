package org.runningdinner.core;

import java.util.HashSet;
import java.util.Set;

public class VisitationPlan {

	private Team currentTeam;

	private Set<Team> hostTeams = new HashSet<Team>(2); // heuristic assumption, will apply in nearly any case

	private Set<Team> guestTeams = new HashSet<Team>(2); // heuristic assumption, will apply in nearly any case

	public VisitationPlan(final Team currentTeam) {
		this.currentTeam = currentTeam;
		currentTeam.setVisitationPlan(this);
	}

	public Team getForTeam() {
		return currentTeam;
	}

	public Set<Team> getHostTeams() {
		return hostTeams;
	}

	public Set<Team> getGuestTeams() {
		return guestTeams;
	}

	public int getNumberOfGuests() {
		return guestTeams.size();
	}

	public int getNumberOfHosts() {
		return hostTeams.size();
	}

	public boolean containsGuestOrHostReference(final Team team) {
		return guestTeams.contains(team) || hostTeams.contains(team);
	}

	public void addHostTeam(final Team hostTeam) {
		this.hostTeams.add(hostTeam);
		hostTeam.getVisitationPlan().addGuestTeam(currentTeam);
	}

	/**
	 * Only needed for internal usage.
	 * 
	 * @param guestTeam
	 */
	private void addGuestTeam(final Team guestTeam) {
		this.guestTeams.add(guestTeam);
	}

	// /**
	// * Special method for internal usage only. Used for preventing endless recursions when managing host/guest relationships
	// *
	// * @param guestTeam
	// */
	// private void addGuestTeamOneSide(final Team guestTeam) {
	// this.guestTeams.add(guestTeam);
	// }
}
