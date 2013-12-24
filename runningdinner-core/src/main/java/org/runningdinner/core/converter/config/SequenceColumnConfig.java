package org.runningdinner.core.converter.config;

public class SequenceColumnConfig extends AbstractColumnConfig {

	protected SequenceColumnConfig(int columnIndex) {
		super(columnIndex);
	}

	public static SequenceColumnConfig noSequenceColumn() {
		SequenceColumnConfig result = new SequenceColumnConfig(Integer.MIN_VALUE);
		return result;
	}

	public static SequenceColumnConfig createSequenceColumnConfig(final int columnIndex) {
		SequenceColumnConfig result = new SequenceColumnConfig(columnIndex);
		return result;
	}

}
