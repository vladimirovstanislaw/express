package express.avto.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import express.avto.rows.EmailLeftOversRow;

public class SAMMBParser {
	private String filenameFrom = null;
	private HashMap<String, EmailLeftOversRow> samMbMap;

	private static SAMMBParser parser = new SAMMBParser();

	public String getFilenameFrom() {
		return filenameFrom;
	}

	public void setFilenameFrom(String filenameFrom) {
		this.filenameFrom = filenameFrom;
	}

	public HashMap<String, EmailLeftOversRow> getSamMbMap() {
		return samMbMap;
	}

	public void setSamMbMap(HashMap<String, EmailLeftOversRow> samMbMap) {
		this.samMbMap = samMbMap;
	}

	private SAMMBParser() {
		super();
		samMbMap = new HashMap<String, EmailLeftOversRow>();
	}

	public static SAMMBParser getInstance() {
		return parser;
	}

	public Map<String, EmailLeftOversRow> Parse() throws IOException {
		// Наименование index: 1
		// Остаток index: 2
		// Код_производителя index: 16
		// Цена index: 23

		int idCol = 16;
		int nameCol = 1;
		int leftOverCol = 2;
		int priceCol = 23;

		File myFile = new File(filenameFrom);

		FileInputStream fis = new FileInputStream(myFile);

		// Finds the workbook instance for XLSX file
		HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);

		// Return first sheet from the XLSX workbook
		HSSFSheet mySheet = myWorkBook.getSheetAt(0);

		// Get iterator to all the rows in current sheet
		Iterator<Row> rowIterator = mySheet.iterator();
		int countAllRows = 0;
		// Traversing over each row of XLSX file
		String tmpMapKey = null;
		// Traversing over each row of XLSX file
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			if (row == null) {
				continue;
			}
			if (row.getCell(idCol) == null) {
				continue;
			}
			if (row.getCell(idCol).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(idCol).toString().equals("")) {
				continue;
			}
			if (row.getCell(nameCol) == null) {
				continue;
			}
			if (row.getCell(nameCol).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(nameCol).toString().equals("")) {
				continue;
			}
			if (row.getCell(leftOverCol) == null) {
				continue;
			}
			if (row.getCell(leftOverCol).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(leftOverCol).toString().equals("")) {
				continue;
			}
			if (row.getCell(priceCol) == null) {
				continue;
			}
			if (row.getCell(priceCol).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(priceCol).toString().equals("")) {
				continue;
			}

			EmailLeftOversRow tmpRow = new EmailLeftOversRow();
			tmpMapKey = row.getCell(idCol).toString();
			tmpRow.setId(row.getCell(idCol).toString());
			tmpRow.setName(row.getCell(nameCol).toString());
			tmpRow.setLeftOver(row.getCell(leftOverCol).toString());
			tmpRow.setPrice(row.getCell(priceCol).toString());

			samMbMap.put(tmpRow.getId(), tmpRow);
			countAllRows++;

		}

		System.out.println("The number of SAM MB rows = " + countAllRows);
		myWorkBook.close();

		return samMbMap;
	}

	@Override
	public String toString() {
		return "OneCParser [filenameFrom=" + filenameFrom + ", asIsCentralProviderMap=" + samMbMap + "]";
	}
}
