package org.runningdinner.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.BatchSize;
import org.runningdinner.core.model.AbstractEntity;
import org.runningdinner.core.util.CoreUtil;

/**
 * Represents a team of a running dinner.<br>
 * Each team is identified by his teamNumber which is unique inside <b>one</b> running-dinner.
 * 
 * @author Clemens Stich
 * 
 */
@Entity
@Access(AccessType.FIELD)
public class Team extends AbstractEntity implements Comparable<Team> {

	private static final long serialVersionUID = -2808246041848437912L;

	protected int teamNumber;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	@BatchSize(size = 30)
	protected Set<Participant> teamMembers;

	@OneToOne(fetch = FetchType.LAZY)
	@BatchSize(size = 30)
	protected MealClass mealClass;

	@Embedded
	protected VisitationPlan visitationPlan;

	protected Team() {
		// JPA
	}

	/**
	 * Constructs a new team with the passed teamNumber
	 * 
	 * @param teamNumber
	 */
	public Team(int teamNumber) {
		this.teamNumber = teamNumber;
	}

	/**
	 * Returns all participans that are members of this team
	 * 
	 * @return
	 */
	public Set<Participant> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(Set<Participant> teamMembers) {
		this.teamMembers = teamMembers;
	}

	/**
	 * Returns the assigned meal of this team. The team is therefore responsible for cooking this meal.
	 * 
	 * @return
	 */
	public MealClass getMealClass() {
		return mealClass;
	}

	public void setMealClass(MealClass mealClass) {
		this.mealClass = mealClass;
	}

	/**
	 * Returns the number of this team
	 * 
	 * @return
	 */
	public int getTeamNumber() {
		return teamNumber;
	}

	/**
	 * Gets the VisitationPlan (=dinner-route) of this team.<br>
	 * If there exist no VisitationPlan yet a new one will be created.<br>
	 * During calculation of dinner-routes (-> RunningDinnerCalculator) each VisitationPlan is sequentially enriched
	 * 
	 * @return
	 */
	public VisitationPlan getVisitationPlan() {
		if (this.visitationPlan == null) {
			this.visitationPlan = new VisitationPlan(this);
		}
		return visitationPlan;
	}

	void setVisitationPlan(VisitationPlan visitationPlan) {
		this.visitationPlan = visitationPlan;
	}

	/**
	 * Retrieves the participant from this team which is marked as host
	 * 
	 * @return
	 */
	public Participant getHostTeamMember() {
		if (!CoreUtil.isEmpty(teamMembers)) {
			for (Participant p : teamMembers) {
				if (p.isHost()) {
					return p;
				}
			}
		}
		return null;
	}
	
	/**
	 * Checks whether the participant denoted by the passed participantKey is a member of this team
	 * 
	 * @param participantKey
	 * @return
	 */
	public boolean isParticipantTeamMember(final String participantKey) {
		if (CoreUtil.isEmpty(teamMembers)) {
			return false;
		}
		for (Participant p : teamMembers) {
			if (p.getNaturalKey().equals(participantKey)) {
				return true;
			}
		}
		return false;
	}

	public Participant getTeamMemberByKey(final String participantKey) {
		if (CoreUtil.isEmpty(teamMembers)) {
			return null;
		}
		for (Participant p : teamMembers) {
			if (p.getNaturalKey().equals(participantKey)) {
				return p;
			}
		}
		return null;
	}
	
	/**
	 * Returns a list with hosting capabilities of each team member.
	 * 
	 * @param runningDinnerConfig Must be passed in for determining the hosting capabilities
	 * @return E.g. [TRUE, UNKNOWN] for two participants whereas one can act as host and whereas it is unknown for the other team member.
	 */
	public List<FuzzyBoolean> getHostingCapability(final RunningDinnerConfig runningDinnerConfig) {
		ArrayList<FuzzyBoolean> result = new ArrayList<FuzzyBoolean>(teamMembers.size());
		for (Participant member : teamMembers) {
			result.add(runningDinnerConfig.canHost(member));
		}
		return result;
	}

	/**
	 * Retrieves the number of possible hosts inside this team.
	 * 
	 * @param hostingCapabilities List with all hosting capabilities of the team (see {@link getHostingCapability})
	 * @return
	 */
	public int getNumberOfHosts(final List<FuzzyBoolean> hostingCapabilities) {
		int result = 0;
		for (FuzzyBoolean canHost : hostingCapabilities) {
			if (FuzzyBoolean.TRUE == canHost) {
				result++;
			}
		}
		return result;
	}

	/**
	 * Checks whether there exist at least one host which has an unknown hosting capability
	 * 
	 * @param hostingCapabilities List with all hosting capabilities of the team (see {@link getHostingCapability})
	 * @return
	 */
	public boolean hasOneUnknownHost(final List<FuzzyBoolean> hostingCapabilities) {
		for (FuzzyBoolean canHost : hostingCapabilities) {
			if (FuzzyBoolean.UNKNOWN == canHost) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(31, 7).append(getTeamNumber()).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		Team other = (Team)obj;
		return new EqualsBuilder().append(getTeamNumber(), other.getTeamNumber()).isEquals();
	}

	@Override
	public String toString() {
		// Be careful: Only valid if mealClass is loaded. We load it however all time in our queries.
		String mealClassStr = mealClass != null ? " - " + mealClass.toString() : "";
		return teamNumber + mealClassStr;
	}

	@Override
	public int compareTo(Team o) {
		if (this.getTeamNumber() < o.getTeamNumber()) {
			return -1;
		}
		if (this.getTeamNumber() > o.getTeamNumber()) {
			return 1;
		}
		return 0;
	}


}
