package org.runningdinner.core.converter.config;

public class AbstractColumnConfig {

	protected int columnIndex = Integer.MIN_VALUE;

	public AbstractColumnConfig(final int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public boolean isAvailable() {
		return columnIndex != Integer.MIN_VALUE;
	}

}
