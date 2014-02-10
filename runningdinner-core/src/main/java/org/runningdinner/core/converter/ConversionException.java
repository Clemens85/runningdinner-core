package org.runningdinner.core.converter;

/**
 * Exception that is thrown if some error occurs during parsing a file with participants.
 * 
 * @author Clemens Stich
 * 
 */
public class ConversionException extends Exception {

	private static final long serialVersionUID = -5727835165400406188L;

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

	/**
	 * Adds details error information to this exception for displaying it in UI.<br>
	 * 
	 * @param rowNumber
	 * @param errorType
	 * @return This exception instance for being able to call this method in a fluent way
	 */
	public ConversionException setErrorInformation(int rowNumber, CONVERSION_ERROR errorType) {
		this.rowNumber = rowNumber;
		this.conversionError = errorType;
		return this;
	}

	/**
	 * Retrieves the number of the row in which the error occurred (or -1 if the row-number is not known)
	 * 
	 * @return
	 */
	public int getRowNumber() {
		return rowNumber;
	}

	/**
	 * Retrieves the type of conversion error for indicating which entity-part of a participant caused the exception.
	 * 
	 * @return
	 */
	public CONVERSION_ERROR getConversionError() {
		if (conversionError == null) {
			return CONVERSION_ERROR.UNKNOWN;
		}
		return conversionError;
	}

}
