package org.runningdinner.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ParticipantAddress {

	private String street;
	private String streetNr;

	private int zip;
	private String cityName;

	private String addressName;
	private String remarks;

	public ParticipantAddress(final String street, final String streetNr, final int zip) {
		this.setStreet(street);
		this.setStreetNr(streetNr);
		this.setZip(zip);
	}

	public ParticipantAddress() {

	}

	public static ParticipantAddress parseFromString(String completeAddressString) {
		String[] addressParts = completeAddressString.split("\\r?\\n");

		if (addressParts.length != 2) {
			throw new IllegalArgumentException("Address must be provided in format like MyStreet 12 NEWLINE 12345 MyCity");
		}

		String streetWithNr = addressParts[0].trim();
		String zipAndCity = addressParts[1].trim();
		ParticipantAddress result = new ParticipantAddress();
		result.setStreetAndNr(streetWithNr);
		result.setZipAndCity(zipAndCity);
		return result;
	}

	public static ParticipantAddress parseFromCommaSeparatedString(String completeAddressString) {
		String[] addressParts = completeAddressString.split(",");

		if (addressParts.length == 2) {
			String streetWithNr = addressParts[0].trim();
			String zipAndCity = addressParts[1].trim();
			ParticipantAddress result = new ParticipantAddress();
			result.setStreetAndNr(streetWithNr);
			result.setZipAndCity(zipAndCity);
			return result;
		}
		else if (addressParts.length == 4) {
			ParticipantAddress result = new ParticipantAddress();
			String street = addressParts[0].trim();
			String streetNr = addressParts[1].trim();
			String zipStr = addressParts[2].trim();
			String city = addressParts[3].trim();
			int zip = CoreUtil.convertToNumber(zipStr, -1);
			result.setStreet(street);
			result.setStreetNr(streetNr);
			result.setZip(zip);
			result.setCityName(city);
			return result;
		}

		throw new IllegalArgumentException("Address must be provided in format like MyStreet 12, 12345 MyCity");

	}

	/**
	 * 
	 * @param streetWithNumber
	 * @throws IllegalArgumentException
	 */
	public void setStreetAndNr(final String streetWithNumber) {
		String[] parts = streetWithNumber.split("\\s+");
		if (parts.length == 2) {
			this.street = parts[0];
			this.streetNr = parts[1];
		}
		else if (parts.length > 2) {
			this.streetNr = parts[parts.length - 1];

			// Street seems to be composed of several words:
			StringBuilder streetBuilder = new StringBuilder();
			int cnt = 0;
			for (int i = 0; i < parts.length - 1; i++) {
				if (cnt++ > 0) {
					streetBuilder.append(" ");
				}
				streetBuilder.append(parts[i]);
			}
			this.street = streetBuilder.toString();
		}
		else {
			throw new IllegalArgumentException("StreetWithNumber parameter must be in format like 'MyStreet 55', but was "
					+ streetWithNumber);
		}
	}

	/**
	 * 
	 * @param zipWithCity
	 * @throws IllegalArgumentException
	 */
	public void setZipAndCity(final String zipWithCity) {
		String[] parts = zipWithCity.split("\\s+");

		if (parts.length < 1) {
			throw new IllegalArgumentException("zipWithCity parameter must be in format like '79100 Freiburg', but was " + zipWithCity);
		}

		String zipStr = parts[0];
		int theZip = CoreUtil.convertToNumber(zipStr, -1);
		this.setZip(theZip);

		if (parts.length == 2) {
			this.cityName = parts[1];
		}
		else if (parts.length > 2) {
			// Maybe it is composed city name: Put it together again
			int cnt = 0;
			StringBuilder cityNameBuilder = new StringBuilder();
			for (int i = 1; i < parts.length; i++) {
				if (cnt++ > 0) {
					cityNameBuilder.append(" ");
				}
				cityNameBuilder.append(parts[i]);
			}
			this.cityName = cityNameBuilder.toString();
		}
		else {
			throw new IllegalArgumentException("zipWithCity parameter must be in format like '79100 Freiburg', but was " + zipWithCity);
		}

	}

	public String getStreet() {
		return street;
	}

	public String getStreetWithNr() {
		return street + " " + streetNr;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNr() {
		return streetNr;
	}

	public void setStreetNr(String streetNr) {
		this.streetNr = streetNr;
	}

	public int getZip() {
		return zip;
	}

	public void setZip(int zip) {
		CoreUtil.assertSmaller(9999, zip, "Zip must be a positive number with exactly 5 digits");
		CoreUtil.assertSmaller(zip, 100000, "Zip must be a positive number with exactly 5 digits");
		this.zip = zip;
	}

	public String getZipWithCity() {
		if (StringUtils.isNotEmpty(cityName)) {
			return zip + " " + cityName;
		}
		return String.valueOf(zip);
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(3, 19).append(getZip()).append(getStreet()).append(getStreetNr()).hashCode();
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
		ParticipantAddress other = (ParticipantAddress)obj;
		return new EqualsBuilder().append(getZip(), other.getZip()).append(getStreet(), other.getStreet()).append(getStreetNr(),
				other.getStreetNr()).isEquals();
	}

	@Override
	public String toString() {
		return new StringBuilder().append(street).append(" ").append(streetNr).append(", ").append(zip).append(" ").append(cityName).toString();
	}

}
