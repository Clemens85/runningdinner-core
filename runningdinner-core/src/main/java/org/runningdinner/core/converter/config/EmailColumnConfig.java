package org.runningdinner.core.converter.config;

public class EmailColumnConfig extends AbstractColumnConfig {
	protected EmailColumnConfig(int columnIndex) {
		super(columnIndex);
	}

	public static EmailColumnConfig noEmailColumn() {
		EmailColumnConfig result = new EmailColumnConfig(Integer.MIN_VALUE);
		return result;
	}

	public static EmailColumnConfig createEmailColumnConfig(final int columnIndex) {
		EmailColumnConfig result = new EmailColumnConfig(columnIndex);
		return result;
	}
}
