package org.runningdinner.core.converter.config;

import org.runningdinner.core.CoreUtil;

public class ParsingConfiguration {

	private int startRow = 1;

	private SequenceColumnConfig sequenceColumnConfig;
	private NameColumnConfig nameColumnConfig;
	private AddressColumnConfig addressColumnConfig;
	private NumberOfSeatsColumnConfig numSeatsColumnConfig;

	private EmailColumnConfig emailColumnConfig;
	private MobileNumberColumnConfig mobileNumberColumnConfig;

	public ParsingConfiguration(NameColumnConfig nameColumnConfig, AddressColumnConfig addressColumnConfig,
			NumberOfSeatsColumnConfig numSeatsColumnConfig) {

		CoreUtil.assertNotNull(nameColumnConfig, "NameColumnConfig must not be null!");
		CoreUtil.assertNotNull(addressColumnConfig, "AddressColumnConfig must not be null!");

		this.nameColumnConfig = nameColumnConfig;
		this.addressColumnConfig = addressColumnConfig;
		this.numSeatsColumnConfig = numSeatsColumnConfig;
	}

	public ParsingConfiguration(NameColumnConfig nameColumnConfig, AddressColumnConfig addressColumnConfig) {
		this(nameColumnConfig, addressColumnConfig, NumberOfSeatsColumnConfig.noNumberOfSeatsColumn());
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public NameColumnConfig getNameColumnConfig() {
		return nameColumnConfig;
	}

	public AddressColumnConfig getAddressColumnConfig() {
		return addressColumnConfig;
	}

	public SequenceColumnConfig getSequenceColumn() {
		if (this.sequenceColumnConfig == null) {
			return SequenceColumnConfig.noSequenceColumn();
		}
		return sequenceColumnConfig;
	}

	public NumberOfSeatsColumnConfig getNumSeatsColumnConfig() {
		if (numSeatsColumnConfig == null) {
			return NumberOfSeatsColumnConfig.noNumberOfSeatsColumn();
		}
		return numSeatsColumnConfig;
	}

	public EmailColumnConfig getEmailColumnConfig() {
		if (emailColumnConfig == null) {
			return EmailColumnConfig.noEmailColumn();
		}
		return emailColumnConfig;
	}

	public void setEmailColumnConfig(EmailColumnConfig emailColumnConfig) {
		this.emailColumnConfig = emailColumnConfig;
	}

	public MobileNumberColumnConfig getMobileNumberColumnConfig() {
		if (mobileNumberColumnConfig == null) {
			return MobileNumberColumnConfig.noMobileNumberColumn();
		}
		return mobileNumberColumnConfig;
	}

	public void setMobileNumberColumnConfig(MobileNumberColumnConfig mobileNumberColumnConfig) {
		this.mobileNumberColumnConfig = mobileNumberColumnConfig;
	}

	public static ParsingConfiguration newDefaultConfiguration() {
		NameColumnConfig nameColumnConfig = NameColumnConfig.createForOneColumn(0);
		AddressColumnConfig addressColumnConfig = AddressColumnConfig.newBuilder().withStreetAndStreetNrColumn(1).buildWithZipAndCityColumn(
				2);
		NumberOfSeatsColumnConfig numberSeatsColumnConfig = NumberOfSeatsColumnConfig.newNumericSeatsColumnConfig(3);

		ParsingConfiguration config = new ParsingConfiguration(nameColumnConfig, addressColumnConfig, numberSeatsColumnConfig);
		return config;
	}
}
