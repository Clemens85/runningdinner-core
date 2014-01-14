package org.runningdinner.core;

import javax.persistence.Embeddable;

import org.apache.commons.lang3.StringUtils;

@Embeddable
public class ParticipantName {

	private String firstnamePart;
	private String lastname;

	protected ParticipantName() {
		// JPA
	}

	/**
	 * Contains typically the firstname of a person, but for persons that have several names (e.g. middlename) these name parts are also
	 * contained.
	 * 
	 * @return
	 */
	public String getFirstnamePart() {
		return firstnamePart;
	}

	/**
	 * Contains always the surname of a person
	 * 
	 * @return
	 */
	public String getLastname() {
		return lastname;
	}

	public String getFullnameFirstnameFirst() {
		String result = firstnamePart;
		if (StringUtils.isEmpty(firstnamePart)) {
			result = StringUtils.EMPTY;
		}
		else {
			if (!StringUtils.isEmpty(lastname)) {
				result += " ";
			}
		}

		if (!StringUtils.isEmpty(lastname)) {
			result += lastname;
		}

		return result;
	}

	@Override
	public String toString() {
		return getFullnameFirstnameFirst();
	}

	public static NameBuilder newName() {
		return new NameBuilder();
	}

	public static class NameBuilder {

		protected NameBuilder() {
		}

		public FirstLastNameBuilder withFirstname(final String firstname) {
			CoreUtil.assertNotEmpty(firstname, "Firstname must not be empty!");
			return new FirstLastNameBuilder(firstname);
		}

		public ParticipantName withCompleteNameString(final String completeName) {
			ParticipantName result = new ParticipantName();

			String[] nameParts = completeName.trim().split("\\s+");
			if (nameParts.length <= 1) {
				throw new IllegalArgumentException("Complete Name must be in a format like 'Max Mustermann'");
			}

			result.lastname = nameParts[nameParts.length - 1];

			StringBuilder firstnamesBuilder = new StringBuilder();
			int cnt = 0;
			for (int i = 0; i < nameParts.length - 1; i++) {
				if (cnt++ > 0) {
					firstnamesBuilder.append(" ");
				}
				firstnamesBuilder.append(nameParts[i]);
			}
			result.firstnamePart = firstnamesBuilder.toString();

			return result;
		}

	}

	public static class FirstLastNameBuilder {

		private String firstname;

		protected FirstLastNameBuilder(String firstname) {
			this.firstname = firstname;
		}

		public ParticipantName andLastname(String lastname) {
			CoreUtil.assertNotEmpty(lastname, "Lastname must not be empty!");
			ParticipantName result = new ParticipantName();
			result.firstnamePart = firstname;
			result.lastname = lastname;
			return result;
		}
	}
}
