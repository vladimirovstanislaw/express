package express.avto.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import express.avto.rows.EmailLeftOversRow;
import express.avto.rows.OneCAllDataRow;
import ru.badyavina.www.rows.CentralProviderRow;

public class SAMMBParser {
	private String filenameFrom = null;
	private Map<String, EmailLeftOversRow> samMbMap;

	private static SAMMBParser parser = new SAMMBParser();

	private SAMMBParser() {
		super();
		samMbMap = new HashMap<String, EmailLeftOversRow>();
	}

	public static SAMMBParser getInstance() {
		return parser;
	}

	private SAMMBParser(String filenameFrom, Map<String, EmailLeftOversRow> samMbMap) {
		super();
		this.filenameFrom = filenameFrom;
		this.samMbMap = samMbMap;
	}
	public Map<String, EmailLeftOversRow> Parse() throws IOException {
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

		// Traversing over each row of XLSX file
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			if (row == null) {
				continue;
			}
			if (row.getCell(0) == null) {
				continue;
			}
			if (row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(0).toString().equals("")) {
				continue;
			}
			if (row.getCell(7) == null) {
				continue;
			}
			if (row.getCell(7).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(7).toString().equals("")) {
				continue;
			}
			if (row.getCell(13) == null) {
				continue;
			}
			if (row.getCell(13).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(13).toString().equals("")) {
				continue;
			}
			if (row.getCell(9) == null) {
				continue;
			}
			if (row.getCell(9).getCellType() == Cell.CELL_TYPE_BLANK) {
				continue;
			}
			if (row.getCell(9).toString().equals("")) {
				continue;
			}

			EmailLeftOversRow tmpRow = new EmailLeftOversRow();
			tmpRow.setCode(row.getCell(7).toString());
			tmpRow.setRetailPrice(row.getCell(13).toString());
			tmpRow.setName(row.getCell(9).toString());
			
			asIsCentralProviderMap.put(tmpRow.getCode(), tmpRow);
			countAllRows++;

		}

		System.out.println("The number of CENTRAL rows = " + countAllRows);
		myWorkBook.close();
		return asIsCentralProviderMap;
	}

	@Override
	public String toString() {
		return "OneCParser [filenameFrom=" + filenameFrom + ", asIsCentralProviderMap=" + samMbMap + "]";
	}
}
