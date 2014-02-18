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
public class VisitationPlan {

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

	/**
	 * Returns true if this team has at least one reference to the passed team
	 * 
	 * @param team
	 * @return
	 */
	public boolean containsGuestOrHostReference(final Team team) {
		return guestTeams.contains(team) || hostTeams.contains(team);
	}

	/**
	 * Returns true if this team has at least one hosting reference to a team that must cook the passed meal.
	 * 
	 * @param mealClass
	 * @return
	 */
	public boolean containsHostReferenceWithSameMealClass(MealClass mealClass) {
		for (Team hostTeam : hostTeams) {
			if (mealClass.equals(hostTeam.getMealClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if this team has at least one guest reference to a team that must cook itself the passed meal.
	 * 
	 * @param mealClass
	 * @return
	 */
	public boolean containsGuestReferenceWithSameMealClass(MealClass mealClass) {
		for (Team guestTeam : guestTeams) {
			if (mealClass.equals(guestTeam.getMealClass())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the passed team as a host team for this team.<br>
	 * This method acts transitive, thus this team is also added as a guest-team to the passed hostTeam.
	 * 
	 * @param hostTeam
	 */
	public void addHostTeam(final Team hostTeam) {
		this.hostTeams.add(hostTeam);
		hostTeam.getVisitationPlan().addGuestTeam(team);
	}

	/**
	 * Removes all team-references from this VisitationPlan.<br>
	 * Note: This method is NOT transitive meaning that e.g. removed guest-teams are not removed in the host-team reference
	 */
	public void removeAllTeamReferences() {
		this.hostTeams.clear();
		this.guestTeams.clear();
	}

	/**
	 * Only needed for internal usage.
	 * 
	 * @param guestTeam
	 */
	private void addGuestTeam(final Team guestTeam) {
		this.guestTeams.add(guestTeam);
	}

	/**
	 * This method works only when all associations are loaded!
	 */
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
