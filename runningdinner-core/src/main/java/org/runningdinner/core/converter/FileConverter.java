package org.runningdinner.core.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.runningdinner.core.Participant;
import org.runningdinner.core.Team;

public interface FileConverter {

	List<Participant> parseParticipants(final InputStream inputStream) throws IOException, ConversionException;

	InputStream saveTeamPlans(final Collection<Team> teams) throws IOException, ConversionException;
}
