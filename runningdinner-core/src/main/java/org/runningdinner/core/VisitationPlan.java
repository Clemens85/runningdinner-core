package org.runningdinner.core;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;

@Embeddable
public class VisitationPlan {

	private Team team;

	private Set<Team> hostTeams = new HashSet<Team>(2); // heuristic assumption, will apply in nearly any case

	private Set<Team> guestTeams = new HashSet<Team>(2); // heuristic assumption, will apply in nearly any case

	public VisitationPlan(final Team currentTeam) {
		this.team = currentTeam;
		currentTeam.setVisitationPlan(this);
	}

	public Team getTeam() {
		return team;
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
		hostTeam.getVisitationPlan().addGuestTeam(team);
	}

	/**
	 * Only needed for internal usage.
	 * 
	 * @param guestTeam
	 */
	private void addGuestTeam(final Team guestTeam) {
		this.guestTeams.add(guestTeam);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();

		result.append("-> ");
		writeTeams(result, hostTeams);

		result.append(CoreUtil.NEWLINE);
		result.append("<- ");
		writeTeams(result, guestTeams);

		return result.toString();
	}

	private void writeTeams(final StringBuilder buffer, final Set<Team> teams) {
		int cnt = 0;
		for (Team team : teams) {
			if (cnt++ > 0) {
				buffer.append(", ");
			}
			buffer.append(team.toString());
		}
	}

}
