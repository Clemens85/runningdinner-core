package org.runningdinner.core.converter.config;

public class GenderColumnConfig extends AbstractColumnConfig {

	public GenderColumnConfig(int columnIndex) {
		super(columnIndex);
	}

	public static GenderColumnConfig noGenderColumn() {
		GenderColumnConfig result = new GenderColumnConfig(Integer.MIN_VALUE);
		return result;
	}

	public static GenderColumnConfig createGenderColumn(final int columnIndex) {
		GenderColumnConfig result = new GenderColumnConfig(columnIndex);
		return result;
	}
}
