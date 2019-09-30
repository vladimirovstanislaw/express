package express.avto.files.upload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import express.avto.rows.AllDataApiRow;
import express.avto.rows.AllDataRow;
import express.avto.rows.ApiSamMbRow;
import express.avto.rows.EmailLeftOversRow;
import express.avto.rows.OneCAllDataRow;

public class UploadApiTwoStocks {
	private static final String n = "\r\n";
	private static final String semilicon = ";";
	private static final String replaceWith = "";
	private final Pattern p = Pattern.compile("( )*$");

	private HashMap<String, ApiSamMbRow> mapDataFirst = null;
	private HashMap<String, ApiSamMbRow> mapDataSecond = null;
	private HashMap<String, OneCAllDataRow> oneCMap = null;
	private HashMap<String, AllDataApiRow> allDataMap;
	private String finalData;
	private String fileName;
	private int dayToDeliveryMsk;
	private int dayToDeliveryPerm;

	private final int DEFAULT_dayToDeliveryMsk = 7;
	private final int DEFAULT_dayToDeliveryPerm = 5;

	public UploadApiTwoStocks() {
		super();
		allDataMap = new HashMap<String, AllDataApiRow>();
		finalData = "";
		dayToDeliveryMsk = DEFAULT_dayToDeliveryMsk;
		dayToDeliveryPerm = DEFAULT_dayToDeliveryPerm;
	}

	public UploadApiTwoStocks(HashMap<String, ApiSamMbRow> mapDataFirst, HashMap<String, ApiSamMbRow> mapDataSecond,
			HashMap<String, OneCAllDataRow> oneCMap, HashMap<String, AllDataApiRow> allDataMap, String finalData,
			String fileName, int dayToDeliveryMsk, int dayToDeliveryPerm) {
		super();
		this.mapDataFirst = mapDataFirst;
		this.mapDataSecond = mapDataSecond;
		this.oneCMap = oneCMap;
		this.allDataMap = allDataMap;
		this.finalData = finalData;
		this.fileName = fileName;
		this.dayToDeliveryMsk = dayToDeliveryMsk;
		this.dayToDeliveryPerm = dayToDeliveryPerm;
	}

	public HashMap<String, ApiSamMbRow> getMapDataMsk() {
		return mapDataFirst;
	}

	public void setMapDataMsk(HashMap<String, ApiSamMbRow> mapDataFirst) {
		this.mapDataFirst = mapDataFirst;
	}

	public HashMap<String, ApiSamMbRow> getMapDataPerm() {
		return mapDataSecond;
	}

	public void setMapDataPerm(HashMap<String, ApiSamMbRow> mapDataSecond) {
		this.mapDataSecond = mapDataSecond;
	}

	public HashMap<String, OneCAllDataRow> getOneCMap() {
		return oneCMap;
	}

	public void setOneCMap(HashMap<String, OneCAllDataRow> oneCMap) {
		this.oneCMap = oneCMap;
	}

	public HashMap<String, AllDataApiRow> getAllDataMap() {
		return allDataMap;
	}

	public void setAllDataMap(HashMap<String, AllDataApiRow> allDataMap) {
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

	public int getDayToDeliveryMsk() {
		return dayToDeliveryMsk;
	}

	public void setDayToDeliveryMsk(int dayToDeliveryMsk) {
		this.dayToDeliveryMsk = dayToDeliveryMsk;
	}

	public int getDayToDeliveryPerm() {
		return dayToDeliveryPerm;
	}

	public void setDayToDeliveryPerm(int dayToDeliveryPerm) {
		this.dayToDeliveryPerm = dayToDeliveryPerm;
	}

	public HashMap<String, AllDataApiRow> configureUploadMap() throws Exception {

		if (mapDataFirst != null && mapDataSecond != null && oneCMap != null) {

			mapDataFirst.entrySet().stream().forEach(e -> {
				AllDataApiRow tmpRow = new AllDataApiRow();

				tmpRow.setId(e.getKey());
				tmpRow.setPriceFirst(e.getValue().getPrice());
				tmpRow.setLeftOverFirst(e.getValue().getLeftOvers());
				tmpRow.setDaysFirstStock(String.valueOf(dayToDeliveryMsk));

//				tmpRowSam.setId(e.getKey());
//				tmpRowSam.setPrice_one_c("");
//				tmpRowSam.setLeft_over_one_c("");
//				tmpRowSam.setDays_first_stock("");
//				tmpRowSam.setPrice_sam_mb(e.getValue().getPrice());
//				tmpRowSam.setLeft_over_sam_mb(e.getValue().getLeftOver());
//				tmpRowSam.setDays_second_stock(String.valueOf(dayToDeliverySamMb));

				allDataMap.put(e.getKey(), tmpRow);
			});
			oneCMap.entrySet().stream().forEach(e -> {

				AllDataApiRow tmpRow = new AllDataApiRow();

				if (!allDataMap.containsKey(e.getKey())) {
					tmpRow.setId(e.getKey());

					tmpRow.setPriceOneC(e.getValue().getPrice());
					tmpRow.setLeftOverOneC(e.getValue().getLeftOver());
					tmpRow.setDaysExpressStock("0");

//					tmpRow.setPrice_one_c(e.getValue().getPrice());
//					tmpRow.setLeft_over_one_c(e.getValue().getLeftOver());
//					tmpRow.setDays_first_stock("0");
//					tmpRow.setPrice_sam_mb("");
//					tmpRow.setLeft_over_sam_mb("");
//					tmpRow.setDays_second_stock("");

					allDataMap.put(e.getKey(), tmpRow);
				} else {

					tmpRow = allDataMap.get(e.getKey());

					tmpRow.setPriceOneC(e.getValue().getPrice());
					tmpRow.setLeftOverOneC(e.getValue().getLeftOver());
					tmpRow.setDaysExpressStock("0");

					allDataMap.put(e.getKey(), tmpRow);

				}
			});
			mapDataSecond.entrySet().stream().forEach(e -> {

				AllDataApiRow tmpRow = new AllDataApiRow();

				if (!allDataMap.containsKey(e.getKey())) {
					tmpRow.setId(e.getKey());
					tmpRow.setPriceSecond(e.getValue().getPrice());
					tmpRow.setLeftOverSecond(e.getValue().getLeftOvers());
					tmpRow.setDaysSecondStock(String.valueOf(dayToDeliveryPerm));

//					tmpRow.setPrice_one_c(e.getValue().getPrice());
//					tmpRow.setLeft_over_one_c(e.getValue().getLeftOver());
//					tmpRow.setDays_first_stock("0");
//					tmpRow.setPrice_sam_mb("");
//					tmpRow.setLeft_over_sam_mb("");
//					tmpRow.setDays_second_stock("");

					allDataMap.put(e.getKey(), tmpRow);
				} else {

					tmpRow = allDataMap.get(e.getKey());

					tmpRow.setPriceSecond(e.getValue().getPrice());
					tmpRow.setLeftOverSecond(e.getValue().getLeftOvers());
					tmpRow.setDaysSecondStock(String.valueOf(dayToDeliveryPerm));

					allDataMap.put(e.getKey(), tmpRow);

				}
			});

//			System.out.println(
//					"======================================================CONFIGURING UPLOAD======================================================");
//			System.out.println("OneCData have - " + countContainsInSAMMap + " elements which contained in SAMMBData");
//			System.out.println(
//					"OneCData have - " + countNotContainsInSAMMap + " elements which NOT contained in SAMMBData");
//			System.out.println("The number of Nomenclature rows = " + allDataMap.size());
			deleteBadEntryZeroPrice();
			return allDataMap;

		} else {
			throw new Exception("No data in maps");
		}
	}

	public void deleteBadEntryZeroPrice() {
		HashMap<String, AllDataApiRow> mapToDelete = new HashMap<String, AllDataApiRow>();

		allDataMap.entrySet().stream().forEach(e -> {
			if (e.getValue().getPriceFirst() != null) {
				if (e.getValue().getPriceFirst().equals("0")) {
					mapToDelete.put(e.getKey(), e.getValue());
				}
			}
			if (e.getValue().getPriceSecond() != null) {
				if (e.getValue().getPriceSecond().equals("0")) {
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
							+ v.getDaysExpressStock() + semilicon + v.getPriceSecond() + semilicon
							+ v.getLeftOverSecond() + semilicon + v.getDaysSecondStock() + semilicon + v.getPriceFirst()
							+ semilicon + v.getLeftOverFirst() + semilicon + v.getDaysFirstStock() + n);
			FileOutputStream outputStream = new FileOutputStream(fileName);

			byte[] strToBytes = finalData.getBytes();

			outputStream.write(strToBytes);

			outputStream.close();
		}
	}

}
