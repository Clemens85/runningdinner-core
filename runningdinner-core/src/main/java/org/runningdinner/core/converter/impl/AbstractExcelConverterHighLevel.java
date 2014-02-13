package org.runningdinner.core.converter.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.FuzzyBoolean;
import org.runningdinner.core.Participant;
import org.runningdinner.core.ParticipantAddress;
import org.runningdinner.core.ParticipantName;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.ConversionException.CONVERSION_ERROR;
import org.runningdinner.core.converter.FileConverter;
import org.runningdinner.core.converter.config.AbstractColumnConfig;
import org.runningdinner.core.converter.config.AddressColumnConfig;
import org.runningdinner.core.converter.config.NameColumnConfig;
import org.runningdinner.core.converter.config.NumberOfSeatsColumnConfig;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.core.converter.config.SequenceColumnConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for parsing excel files which contains the main logic.<br>
 * This class is independent of XSSF or HSSF excel file types.
 * 
 * @author Clemens Stich
 * 
 */
public class AbstractExcelConverterHighLevel {

	protected ParsingConfiguration parsingConfiguration;

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractExcelConverterHighLevel.class);

	public AbstractExcelConverterHighLevel(ParsingConfiguration parsingConfiguration) {
		this.parsingConfiguration = parsingConfiguration;
	}

	/**
	 * Parses each excel row and assigns each participant an ascending participant-number.
	 * 
	 * @param sheet
	 * @return
	 * @throws ConversionException
	 */
	public List<Participant> parseParticipants(final Sheet sheet) throws ConversionException {
		int startRow = parsingConfiguration.getStartRow();

		Set<Participant> tmpResult = new LinkedHashSet<Participant>();

		int cnt = 1;
		for (int rowIndex = startRow; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			Row row = sheet.getRow(rowIndex);

			LOGGER.debug("Parsing row number {}", cnt);

			if (row == null) {
				continue;
			}

			int participantNr = cnt++;

			ParticipantName participantName = getName(row);

			participantNr = getSequenceNumberIfAvailable(row, participantNr);

			int numSeats = getNumberOfSeats(row);

			ParticipantAddress address = getAddress(row);

			Participant participant = new Participant(participantNr);
			participant.setNumSeats(numSeats);
			participant.setName(participantName);
			participant.setAddress(address);

			participant.setEmail(getColumnString(row, parsingConfiguration.getEmailColumnConfig()));
			participant.setMobileNumber(getColumnString(row, parsingConfiguration.getMobileNumberColumnConfig()));

			if (!tmpResult.add(participant)) {
				handleDuplicateError(participant, rowIndex);
			}

			LOGGER.debug("Participant {} has been parsed", participant);
		}

		if (tmpResult.size() > FileConverter.MAX_PARTICIPANTS) {
			// Not quite clever to first parse all particiapants and then throw exception, but actually this should never happen
			throw new ConversionException().setErrorInformation(-1, CONVERSION_ERROR.TOO_MUCH_PARTICIPANTS);
		}

		return new ArrayList<Participant>(tmpResult);
	}

	/**
	 * Reads out the value that is represented by the passed column configuration or returns an empty string if the passed column
	 * configuration shall not be considered.
	 * 
	 * @param row Current row of excel sheet
	 * @param columnConfig
	 * @return
	 */
	private String getColumnString(final Row row, final AbstractColumnConfig columnConfig) {
		if (columnConfig.isAvailable()) {
			return getCellValueAsString(row, columnConfig.getColumnIndex());
		}
		return StringUtils.EMPTY;
	}

	/**
	 * Reads out the participant name as it is configured in the NameColumnConfig
	 * 
	 * @param row Current row of excel sheet
	 * @return
	 * @throws ConversionException
	 */
	private ParticipantName getName(Row row) throws ConversionException {
		try {
			NameColumnConfig nameColumnConfig = parsingConfiguration.getNameColumnConfig();
			int firstnameColumn = nameColumnConfig.getFirstnameColumn();
			int lastnameColumn = nameColumnConfig.getLastnameColumn();

			if (nameColumnConfig.isComposite()) {
				String fullname = getCellValueAsString(row, firstnameColumn);
				return ParticipantName.newName().withCompleteNameString(fullname);
			}
			else {
				String firstname = getCellValueAsString(row, firstnameColumn);
				String lastname = getCellValueAsString(row, lastnameColumn);
				return ParticipantName.newName().withFirstname(firstname).andLastname(lastname);
			}
		}
		catch (IllegalArgumentException ex) {
			throw new ConversionException("Could not parse name at row " + row.getRowNum() + 1, ex).setErrorInformation(
					row.getRowNum() + 1, CONVERSION_ERROR.NAME);
		}
	}

	private void handleDuplicateError(final Participant participant, final int rowIndex) throws ConversionException {
		throw new ConversionException("Could not add new participant " + participant + " in row " + (rowIndex + 1)
				+ " because there existed alread another participant with the same sequence number!").setErrorInformation(rowIndex + 1,
				CONVERSION_ERROR.PARTICIPANT_NR);
	}

	/**
	 * Reads out the participant address as it is configured in the AddressColumnConfig
	 * 
	 * @param row Current row of excel sheet
	 * @return
	 * @throws ConversionException
	 */
	private ParticipantAddress getAddress(Row row) throws ConversionException {
		try {
			AddressColumnConfig addressColumnConfig = parsingConfiguration.getAddressColumnConfig();

			if (addressColumnConfig.isSingleColumnConfig()) {
				String street = getCellValueAsString(row, addressColumnConfig.getStreetColumn());
				String streetNr = getCellValueAsString(row, addressColumnConfig.getStreetNrColumn());
				String zipStr = getCellValueAsString(row, addressColumnConfig.getZipColumn());
				int zip = CoreUtil.convertToNumber(zipStr, -1);

				ParticipantAddress result = new ParticipantAddress(street, streetNr, zip);

				if (addressColumnConfig.getCityColumn() != AbstractColumnConfig.UNAVAILABLE_COLUMN_INDEX) {
					String city = getCellValueAsString(row, addressColumnConfig.getCityColumn());
					result.setCityName(city);
				}

				return result;
			}
			else if (addressColumnConfig.isCompositeConfig()) {
				String compositeAddressStr = getCellValueAsString(row, addressColumnConfig.getStreetColumn());
				return ParticipantAddress.parseFromString(compositeAddressStr);
			}
			else {
				ParticipantAddress result = new ParticipantAddress();

				if (addressColumnConfig.isStreetAndStreetNrCompositeConfig()) {
					String streetWithNumber = getCellValueAsString(row, addressColumnConfig.getStreetColumn());
					result.setStreetAndNr(streetWithNumber);
				}
				if (addressColumnConfig.isZipAndCityCompositeConfig()) {
					String zipWithCity = getCellValueAsString(row, addressColumnConfig.getZipColumn());
					result.setZipAndCity(zipWithCity);
				}
				else {
					String zipStr = getCellValueAsString(row, addressColumnConfig.getZipColumn());
					String city = getCellValueAsString(row, addressColumnConfig.getCityColumn());
					result.setCityName(city);
					result.setZip(CoreUtil.convertToNumber(zipStr, -1));
				}

				return result;
			}

		}
		catch (IllegalArgumentException ex) {
			throw new ConversionException("Could not parse address at row " + row.getRowNum() + 1, ex).setErrorInformation(
					row.getRowNum() + 1, CONVERSION_ERROR.ADDRESS);
		}
	}

	/**
	 * Reads out the participant's number of seats (important for hosting capabilities) as it is configured in the NumberOfSeatsColumnConfig
	 * 
	 * @param row Current row of excel sheet
	 * @return The number of seats for this participant. If this information is not available it is returned a negative number. Furthermore
	 *         if the information about number of seats is represented by a boolen (can host or cannot host) it is returned
	 *         Integer.MAX_VALUE which is certainly sufficient for every scenario
	 * @throws ConversionException
	 */
	private int getNumberOfSeats(final Row row) throws ConversionException {

		try {
			NumberOfSeatsColumnConfig numSeatsColumnConfig = parsingConfiguration.getNumSeatsColumnConfig();
			int numSeatsColumnIndex = numSeatsColumnConfig.getColumnIndex();

			if (numSeatsColumnConfig.isAvailable()) {
				String numSeatsStr = getCellValueAsString(row, numSeatsColumnIndex);

				if (numSeatsColumnConfig.isNumericDeclaration()) {
					return CoreUtil.convertToNumber(numSeatsStr, Participant.UNDEFINED_SEATS);
				}
				else {
					FuzzyBoolean fuzzyBool = CoreUtil.convertToBoolean(numSeatsStr, FuzzyBoolean.UNKNOWN);
					if (FuzzyBoolean.TRUE == fuzzyBool) {
						// Slight "workaround", but nobody can have so many seats, so it is assured that he can be a host
						return Integer.MAX_VALUE;
					}
					else if (FuzzyBoolean.FALSE == fuzzyBool) {
						return 0; // With no seats it is not possible to be a host
					}
					// else: numSeats == UNDEFINED
				}
			}

			return Participant.UNDEFINED_SEATS;
		}
		catch (IllegalArgumentException ex) {
			throw new ConversionException("Could not parse number of seats at row " + row.getRowNum() + 1, ex).setErrorInformation(
					row.getRowNum() + 1, CONVERSION_ERROR.NUMBER_OF_SEATS);
		}
	}

	/**
	 * It is possible to specify a separate sequence number column, which is used for assigning participantNumbers to participants.<br>
	 * If such a sequence-number column is configured this column is used for generating the participant number. Otherwise the passed
	 * pre-generated participant-number will be used
	 * 
	 * @param row Current row of excel sheet
	 * @param participantNr The generated participant number according to the order in excel. This is used as a result if no sequence column
	 *            configuration exists
	 * @return
	 * @throws ConversionException
	 */
	private int getSequenceNumberIfAvailable(final Row row, final int participantNr) {
		int result = participantNr;

		SequenceColumnConfig sequenceColumn = parsingConfiguration.getSequenceColumnConfig();
		if (sequenceColumn.isAvailable()) {
			int sequenceNr = CoreUtil.convertToNumber(getCellValueAsString(row, sequenceColumn.getColumnIndex()), -1);
			if (sequenceNr >= 0) {
				result = sequenceNr;
			}
		}

		return result;
	}

	public ParsingConfiguration getParsingConfiguration() {
		return parsingConfiguration;
	}

	/**
	 * Helper method for dealing with different column types from Excel.<br>
	 * It returns for each column type a string, so that the caller is responsible for handling different datatypes
	 * 
	 * @param row
	 * @param cellNumber
	 * @return
	 */
	protected String getCellValueAsString(final Row row, final int cellNumber) {
		Cell cell = row.getCell(cellNumber);

		String result = null;
		if (cell != null) {
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				result = cell.getRichStringCellValue().getString();
				result = result.trim();
			}
			if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
				if (DateUtil.isCellDateFormatted(cell)) {

					// if (HSSFDateUtil.isCellDateFormatted(cell)) {
					// TODO: XSSF <-> HSSF, but seems to work for both
					HSSFDataFormatter formater = new HSSFDataFormatter();
					result = formater.formatCellValue(cell);
				}
				else {
					Double doub = new Double(cell.getNumericCellValue());
					result = String.valueOf(doub.longValue());
				}
			}
			if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
				if (cell.getBooleanCellValue()) {
					result = "true";
				}
				else {
					result = "false";
				}
			}
		}
		return result;
	}
}
