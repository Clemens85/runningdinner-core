package org.runningdinner.core;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

@Embeddable
@Access(AccessType.FIELD)
public class VisitationPlan /* extends AbstractEntity */{

	/**
	 * This is just used when populating the VisitationPlan. Later on this information isn't needed any longer
	 */
	@Transient
	protected Team team;

	/**
	 * We have two associations to the same entity (Team) by using hostTeams and guestTeams. Therefore we must use ManyToMany association
	 * for being able to get two disjunct join-tables.
	 */
	@ManyToMany
	@JoinTable(name = "HostTeamMapping", joinColumns = @JoinColumn(name = "host_team_id"), inverseJoinColumns = @JoinColumn(name = "parent_team_id"))
	protected Set<Team> hostTeams = new HashSet<Team>(2); // heuristic assumption, will apply in nearly any case

	/**
	 * We have two associations to the same entity (Team) by using hostTeams and guestTeams. Therefore we must use ManyToMany association
	 * for being able to get two disjunct join-tables.
	 */
	@ManyToMany
	@JoinTable(name = "GuestTeamMapping", joinColumns = @JoinColumn(name = "guest_team_id"), inverseJoinColumns = @JoinColumn(name = "parent_team_id"))
	protected Set<Team> guestTeams = new HashSet<Team>(2); // heuristic assumption, will apply in nearly any case

	protected VisitationPlan() {
		// JPA
	}

	public VisitationPlan(final Team currentTeam) {
		this.team = currentTeam;
		currentTeam.setVisitationPlan(this);
	}

	/**
	 * Returns the teams which are host for the current team (from the VisitationPlan)
	 * 
	 * @return
	 */
	public Set<Team> getHostTeams() {
		return hostTeams;
	}

	/**
	 * Returns the teams which are guest of the current team (from the VisitationPlan)
	 * 
	 * @return
	 */
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
