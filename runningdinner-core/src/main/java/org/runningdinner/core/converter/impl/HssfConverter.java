package org.runningdinner.core.converter.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.runningdinner.core.CoreUtil;
import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;
import org.runningdinner.core.converter.ConversionException;
import org.runningdinner.core.converter.FileConverter;
import org.runningdinner.core.converter.config.ParsingConfiguration;

public class HssfConverter extends AbstractExcelConverterHighLevel implements FileConverter {

	public HssfConverter(ParsingConfiguration parsingConfiguration) {
		super(parsingConfiguration);
	}

	@Override
	public List<Participant> parseParticipants(final InputStream inputStream) throws IOException, ConversionException {
		CoreUtil.assertNotNull(inputStream, "Passed InputStream must not be null!");
		POIFSFileSystem poiFileSystem = new POIFSFileSystem(inputStream);
		HSSFWorkbook workbook = new HSSFWorkbook(poiFileSystem);
		HSSFSheet sheet = workbook.getSheetAt(0);

		return parseParticipants(sheet);
	}

	@Override
	public InputStream saveTeamPlans(Collection<Team> teams) throws IOException, ConversionException {
		HSSFWorkbook workbook = new HSSFWorkbook();

		ByteArrayOutputStream out = new ByteArrayOutputStream(16 * 1024); // 16 kb
		workbook.write(out);
		out.close();
		byte[] byteArray = out.toByteArray();
		out = null;

		// TODO

		ByteArrayInputStream result = new ByteArrayInputStream(byteArray);
		return result;
		// throw new UnsupportedOperationException("not yet implemented");
	}

}
