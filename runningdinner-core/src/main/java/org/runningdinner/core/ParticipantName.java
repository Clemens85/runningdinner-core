package org.runningdinner.core;

import org.apache.commons.lang3.StringUtils;

public class ParticipantName {

	private String firstname;
	private String surname;

	protected ParticipantName(MemberNameBuilder builder) {
		this.firstname = builder.firstname;
		this.surname = builder.surname;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getSurname() {
		return surname;
	}

	public String getFullnameFirstnameFirst() {
		String result = firstname;
		if (StringUtils.isEmpty(firstname)) {
			result = StringUtils.EMPTY;
		}
		else {
			if (!StringUtils.isEmpty(surname)) {
				result += ", ";
			}
		}

		if (!StringUtils.isEmpty(surname)) {
			result += surname;
		}

		return result;
	}

	@Override
	public String toString() {
		return getFullnameFirstnameFirst();
	}

	public static MemberNameBuilder newBuilder() {
		return new MemberNameBuilder();
	}

	public static class MemberNameBuilder {

		private String firstname;
		private String surname;

		private String nameSplitPattern;

		public MemberNameBuilder() {
		}

		public MemberNameBuilder(final String fullnamePattern) {
		}

		public MemberNameBuilder withFirstname(final String firstname) {
			this.firstname = firstname;
			return this;
		}

		public MemberNameBuilder withSurname(final String surname) {
			this.surname = surname;
			return this;
		}

		public MemberNameBuilder withCompleteNameString(final String completeName) {
			// TODO: Split name according to standard or passed pattern!
			return this;
		}

		public ParticipantName build() {
			return new ParticipantName(this);
		}
	}
}
