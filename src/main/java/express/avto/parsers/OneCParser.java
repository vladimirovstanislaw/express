package express.avto.parsers;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;

import express.avto.rows.OneCAllDataRow;

public class OneCParser {
	private String filenameFrom = null;
	private HashMap<String, OneCAllDataRow> oneCMap;

	private static OneCParser parser = new OneCParser();

	private OneCParser() {
		super();
		oneCMap = new HashMap<String, OneCAllDataRow>();
	}

	public static OneCParser getInstance() {
		return parser;
	}

	public String getFilenameFrom() {
		return filenameFrom;
	}

	public void setFilenameFrom(String filenameFrom) {
		this.filenameFrom = filenameFrom;
	}

	public HashMap<String, OneCAllDataRow> getOneCMap() {
		return oneCMap;
	}

	public void setOneCMap(HashMap<String, OneCAllDataRow> oneCMap) {
		this.oneCMap = oneCMap;
	}

	public HashMap<String, OneCAllDataRow> Parse() throws IOException {
		int count = 0;
		try (Reader reader = Files.newBufferedReader(Paths.get(filenameFrom));
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);) {
			for (CSVRecord csvRecord : csvParser) {

				// Accessing Values by Column Index
				String id = csvRecord.get(0); // это код производителя
				String name = csvRecord.get(2);
				String leftOver = csvRecord.get(3);
				String price = csvRecord.get(1);

				if (isNullString(id) || id.length() < 4) {
					continue;
				}
				if (isNullString(leftOver)) {
					continue;
				}
				if (isNullString(price)) {
					continue;
				}
				if (isNullString(name)) {
					continue;
				}
				if (leftOver.contains(" ")) {
					continue;
				}
				OneCAllDataRow tmpRow = new OneCAllDataRow();
				tmpRow.setId(id);
				tmpRow.setLeftOver(leftOver);
				tmpRow.setName(name);
				tmpRow.setPrice(price);
				oneCMap.put(id, tmpRow);
				count++;
			}
		}
		System.out.println("The number of OneC rows = " + count);
		return oneCMap;
	}

	@Override
	public String toString() {
		return "OneCParser [filenameFrom=" + filenameFrom + ", asIsCentralProviderMap=" + oneCMap + "]";
	}

	public boolean isNullString(String string) {
		if (string == null) {
			return true;
		}

		if (string.equals("")) {
			return true;
		}
		if (string.isEmpty()) {
			return true;
		}
		if (("\"" + string + "\"").equals("\"\"")) {
			return true;
		}
		if (string.length() == 0) {
			return true;
		}
		return false;
	}
}
