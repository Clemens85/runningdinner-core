package org.runningdinner.core.converter;

import org.apache.commons.lang3.StringUtils;
import org.runningdinner.core.converter.config.ParsingConfiguration;
import org.runningdinner.core.converter.impl.HssfConverter;
import org.runningdinner.core.converter.impl.XssfConverter;

public class ConverterFactory {

	private static final String XLS = "xls";
	private static final String XLSX = "xlsx";
	private static final String XLSM = "xlsm";
	private static final String CSV = "csv";

	public static enum INPUT_FILE_TYPE {
		HSSF, XSSF, CSV, UNKNOWN
	}

	public static FileConverter newConverter(final ParsingConfiguration parsingConfiguration, final INPUT_FILE_TYPE fileType) {
		if (INPUT_FILE_TYPE.HSSF == fileType) {
			return new HssfConverter(parsingConfiguration);
		}
		else if (INPUT_FILE_TYPE.XSSF == fileType) {
			return new XssfConverter(parsingConfiguration);
		}

		String msg = "Unsupported input file type " + fileType;
		throw new IllegalArgumentException(msg);
	}

	/**
	 * Tries to detect the file type based on the passed identifier.<br>
	 * The identifier can either be a file-suffix (like e.g. "xlsx"), a filename or a mime-type like e.g. ("application/vnd.ms-excel").<br>
	 * 
	 * @param identifier
	 * @return The recognized excel file type or EXCEL_FILE_TYPE.UNKNOWN if recognition failed.
	 */
	public static INPUT_FILE_TYPE determineFileType(final String identifier) {

		if (StringUtils.isNotEmpty(identifier) && identifier.contains("/")) {
			if ("application/vnd.ms-excel".equalsIgnoreCase(identifier) || "application/xls".equalsIgnoreCase(identifier)) {
				return INPUT_FILE_TYPE.HSSF;
			}
			else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(identifier)
					|| "application/vnd.openxmlformats-officedocument.spreadsheetml.template".equalsIgnoreCase(identifier)) {
				return INPUT_FILE_TYPE.XSSF;
			}
		}

		if (XLS.equalsIgnoreCase(identifier)) {
			return INPUT_FILE_TYPE.HSSF;
		}
		else if (XLSM.equalsIgnoreCase(identifier) || XLSX.equalsIgnoreCase(identifier)) {
			return INPUT_FILE_TYPE.XSSF;
		}

		int lastIndexOfSuffixSeparator = identifier.lastIndexOf('.');
		if (lastIndexOfSuffixSeparator != -1 && lastIndexOfSuffixSeparator + 1 < identifier.length()) {
			String fileExtension = identifier.substring(lastIndexOfSuffixSeparator + 1, identifier.length());
			return determineFileType(fileExtension);
		}

		return INPUT_FILE_TYPE.UNKNOWN;
	}

}
