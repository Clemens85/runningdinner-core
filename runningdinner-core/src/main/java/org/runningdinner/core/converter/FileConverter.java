package org.runningdinner.core.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.runningdinner.core.Participant;

public interface FileConverter {
	List<Participant> parseParticipants(final InputStream inputStream) throws IOException, ConversionException;
}
