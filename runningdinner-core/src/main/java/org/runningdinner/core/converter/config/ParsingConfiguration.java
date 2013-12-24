package org.runningdinner.core.converter.config;

import org.runningdinner.core.CoreUtil;

public class ParsingConfiguration {

	private int startRow = 1;

	private SequenceColumnConfig sequenceColumn;
	private NameColumnConfig nameColumnConfig;
	private AddressColumnConfig addressColumnConfig;
	private NumberOfSeatsColumnConfig numSeatsColumnConfig;

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
		if (this.sequenceColumn == null) {
			return SequenceColumnConfig.noSequenceColumn();
		}
		return sequenceColumn;
	}

	public NumberOfSeatsColumnConfig getNumSeatsColumnConfig() {
		if (numSeatsColumnConfig == null) {
			return NumberOfSeatsColumnConfig.noNumberOfSeatsColumn();
		}
		return numSeatsColumnConfig;
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
