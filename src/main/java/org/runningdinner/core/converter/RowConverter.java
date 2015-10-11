package org.runningdinner.core.converter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface RowConverter {

	List<List<String>> readRows(final InputStream inputStream, int maxRows) throws IOException;
}
