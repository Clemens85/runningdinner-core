package org.runningdinner.core.converter;

public class ConversionException extends Exception {

	public static enum CONVERSION_ERROR {
		NAME, ADDRESS, PARTICIPANT_NR, NUMBER_OF_SEATS, UNKNOWN
	}

	private int rowNumber = -1;
	private CONVERSION_ERROR conversionError;

	public ConversionException() {
		super();
	}

	public ConversionException(String message, Throwable arg1) {
		super(message, arg1);
	}

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Throwable t) {
		super(t);
	}

	public ConversionException setErrorInformation(int rowNumber, CONVERSION_ERROR errorType) {
		this.rowNumber = rowNumber;
		this.conversionError = errorType;
		return this;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public CONVERSION_ERROR getConversionError() {
		if (conversionError == null) {
			return CONVERSION_ERROR.UNKNOWN;
		}
		return conversionError;
	}

}
