package org.runningdinner.core.converter.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.FileConverter;
import org.runningdinner.core.converter.config.ParsingConfiguration;

// TODO: XLSX parsing takes huge amount of memory when using the highlevel XSSF API.
// If this is an issue, it has to be changed to low-level event-driven parsing
public class XssfConverter extends AbstractExcelConverterHighLevel implements FileConverter {

	public XssfConverter(ParsingConfiguration parsingConfiguration) {
		super(parsingConfiguration);
	}

	@Override
	public List<Participant> parseParticipants(final InputStream inputStream) throws IOException, ConversionException {
		CoreUtil.assertNotNull(inputStream, "Passed InputStream must not be null!");
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);

		return parseParticipants(sheet);
	}

	@Override
	public InputStream saveTeamPlans(Collection<Team> teams) throws IOException, ConversionException {
		throw new UnsupportedOperationException("not yet implemented");
	}
}
