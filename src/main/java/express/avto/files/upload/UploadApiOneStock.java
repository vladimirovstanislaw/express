package express.avto.files.upload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import express.avto.rows.AllDataApiOneStockRow;
import express.avto.rows.AllDataApiRow;
import express.avto.rows.AllDataRow;
import express.avto.rows.ApiSamMbRow;
import express.avto.rows.EmailLeftOversRow;
import express.avto.rows.OneCAllDataRow;

public class UploadApiOneStock {
	private static final String n = "\r\n";
	private static final String semilicon = ";";
	private static final String replaceWith = "";
	private final Pattern p = Pattern.compile("( )*$");

	private HashMap<String, ApiSamMbRow> mapData = null;

	private HashMap<String, OneCAllDataRow> oneCMap = null;
	private HashMap<String, AllDataApiOneStockRow> allDataMap;
	private String finalData;
	private String fileName;
	private int dayToDelivery;

	private final int DEFAULT_dayToDelivery = 5;

	public UploadApiOneStock() {
		super();
		allDataMap = new HashMap<String, AllDataApiOneStockRow>();
		finalData = "";
		dayToDelivery = DEFAULT_dayToDelivery;
	}

	public UploadApiOneStock(HashMap<String, ApiSamMbRow> mapData, HashMap<String, OneCAllDataRow> oneCMap,
			String fileName, int dayToDelivery) {
		super();
		this.mapData = mapData;
		this.oneCMap = oneCMap;
		this.fileName = fileName;
		this.dayToDelivery = dayToDelivery;

	}

	public HashMap<String, ApiSamMbRow> getMapData() {
		return mapData;
	}

	public void setMapData(HashMap<String, ApiSamMbRow> mapData) {
		this.mapData = mapData;
	}

	public HashMap<String, OneCAllDataRow> getOneCMap() {
		return oneCMap;
	}

	public void setOneCMap(HashMap<String, OneCAllDataRow> oneCMap) {
		this.oneCMap = oneCMap;
	}

	public HashMap<String, AllDataApiOneStockRow> getAllDataMap() {
		return allDataMap;
	}

	public void setAllDataMap(HashMap<String, AllDataApiOneStockRow> allDataMap) {
		this.allDataMap = allDataMap;
	}

	public String getFinalData() {
		return finalData;
	}

	public void setFinalData(String finalData) {
		this.finalData = finalData;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getDayToDelivery() {
		return dayToDelivery;
	}

	public void setDayToDelivery(int dayToDelivery) {
		this.dayToDelivery = dayToDelivery;
	}

	public HashMap<String, AllDataApiOneStockRow> configureUploadMap() throws Exception {

		if (mapData != null && oneCMap != null) {

			mapData.entrySet().stream().forEach(e -> {
				AllDataApiOneStockRow tmpRow = new AllDataApiOneStockRow();

				tmpRow.setId(e.getKey());
				tmpRow.setPriceFirst(e.getValue().getPrice());
				tmpRow.setLeftOverFirst(e.getValue().getLeftOvers());
				tmpRow.setDaysFirstStock(String.valueOf(dayToDelivery));

				allDataMap.put(e.getKey(), tmpRow);
			});
			oneCMap.entrySet().stream().forEach(e -> {

				AllDataApiOneStockRow tmpRow = new AllDataApiOneStockRow();

				if (!allDataMap.containsKey(e.getKey())) {
					tmpRow.setId(e.getKey());

					tmpRow.setPriceOneC(e.getValue().getPrice());
					tmpRow.setLeftOverOneC(e.getValue().getLeftOver());
					tmpRow.setDaysExpressStock("0");

					allDataMap.put(e.getKey(), tmpRow);
				} else {

					tmpRow = allDataMap.get(e.getKey());

					tmpRow.setPriceOneC(e.getValue().getPrice());
					tmpRow.setLeftOverOneC(e.getValue().getLeftOver());
					tmpRow.setDaysExpressStock("0");

					allDataMap.put(e.getKey(), tmpRow);

				}
			});

			deleteBadEntryZeroPrice();
			return allDataMap;

		} else {
			throw new Exception("No data in maps");
		}
	}

	public void deleteBadEntryZeroPrice() {
		HashMap<String, AllDataApiOneStockRow> mapToDelete = new HashMap<String, AllDataApiOneStockRow>();

		allDataMap.entrySet().stream().forEach(e -> {
			if (e.getValue().getPriceFirst() != null) {
				if (e.getValue().getPriceFirst().equals("0")) {
					mapToDelete.put(e.getKey(), e.getValue());
				}
			}

			if (e.getValue().getPriceOneC() != null) {
				if (e.getValue().getPriceOneC().equals("0")) {
					mapToDelete.put(e.getKey(), e.getValue());
				}
			}

		});

		mapToDelete.entrySet().forEach(e -> {
			allDataMap.remove(e.getKey());
		});

	}

	public void writeFile() throws IOException {
		if (allDataMap != null) {

			allDataMap.forEach((k,
					v) -> finalData += k + semilicon + v.getPriceOneC() + semilicon + v.getLeftOverOneC() + semilicon
							+ v.getDaysExpressStock() + semilicon + v.getPriceFirst() + semilicon + v.getLeftOverFirst()
							+ semilicon + v.getDaysFirstStock() + n);
			FileOutputStream outputStream = new FileOutputStream(fileName);

			byte[] strToBytes = finalData.getBytes();

			outputStream.write(strToBytes);

			outputStream.close();
		}
	}

}
