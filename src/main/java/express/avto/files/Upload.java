package express.avto.files;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import express.avto.rows.AllDataRow;
import express.avto.rows.EmailLeftOversRow;
import express.avto.rows.OneCAllDataRow;

public class Upload {
	private static Upload upload = new Upload();
	private static final String n = "\r\n";
	private static final String semilicon = ";";
	private static final String replaceWith = "";
	private final Pattern p = Pattern.compile("( )*$");

	private HashMap<String, EmailLeftOversRow> samMap = null;
	private HashMap<String, OneCAllDataRow> oneCMap = null;
	private HashMap<String, AllDataRow> allDataMap;
	private String finalData;
	private String fileName;
	private int dayToDeliverySamMb;

	private final int DEFAULT_dayToDeliverySamMb = 5;

	int countContainsInSAMMap = 0;
	int countNotContainsInSAMMap = 0;

	public int getDayToDeliverySamMb() {
		return dayToDeliverySamMb;
	}

	public void setDayToDeliverySamMb(int dayToDeliverySamMb) {
		this.dayToDeliverySamMb = dayToDeliverySamMb;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public HashMap<String, EmailLeftOversRow> getSamMap() {
		return samMap;
	}

	public void setSamMap(HashMap<String, EmailLeftOversRow> samMap) {
		this.samMap = samMap;
	}

	public HashMap<String, OneCAllDataRow> getOneCMap() {
		return oneCMap;
	}

	public void setOneCMap(HashMap<String, OneCAllDataRow> oneCMap) {
		this.oneCMap = oneCMap;
	}

	private Upload() {
		super();
		allDataMap = new HashMap<String, AllDataRow>();
		finalData = "";
		dayToDeliverySamMb = DEFAULT_dayToDeliverySamMb;
	}

	public static Upload getInstanceUpload() {
		return upload;
	}

	public HashMap<String, AllDataRow> configureUploadMap() throws Exception {

		if (samMap != null && oneCMap != null) {

			samMap.entrySet().stream().forEach(e -> {
				AllDataRow tmpRowSam = new AllDataRow();
				tmpRowSam.setId(e.getKey());
				tmpRowSam.setPrice_one_c("");
				tmpRowSam.setLeft_over_one_c("");
				tmpRowSam.setDays_first_stock("");
				tmpRowSam.setPrice_sam_mb(e.getValue().getPrice());
				tmpRowSam.setLeft_over_sam_mb(e.getValue().getLeftOver());
				tmpRowSam.setDays_second_stock(String.valueOf(dayToDeliverySamMb));

				allDataMap.put(e.getKey(), tmpRowSam);
			});
			oneCMap.entrySet().stream().forEach(e -> {
				AllDataRow tmpRow = new AllDataRow();
				EmailLeftOversRow tmpEmailRow = null;
				if (!samMap.containsKey(e.getKey())) {
					tmpRow.setId(e.getKey());
					tmpRow.setPrice_one_c(e.getValue().getPrice());
					tmpRow.setLeft_over_one_c(e.getValue().getLeftOver());
					tmpRow.setDays_first_stock("0");
					tmpRow.setPrice_sam_mb("");
					tmpRow.setLeft_over_sam_mb("");
					tmpRow.setDays_second_stock("");

					allDataMap.put(e.getKey(), tmpRow);
					countNotContainsInSAMMap++;
				} else {
					tmpEmailRow = samMap.get(e.getKey());
					tmpRow.setId(e.getKey());
					tmpRow.setPrice_one_c(e.getValue().getPrice());
					tmpRow.setLeft_over_one_c(e.getValue().getLeftOver());
					tmpRow.setDays_first_stock("0");
					tmpRow.setPrice_sam_mb(tmpEmailRow.getPrice());
					tmpRow.setLeft_over_sam_mb(tmpEmailRow.getLeftOver());
					tmpRow.setDays_second_stock(String.valueOf(dayToDeliverySamMb));

					allDataMap.put(e.getKey(), tmpRow);
					countContainsInSAMMap++;
				}
			});
			System.out.println("======================================================CONFIGURING UPLOAD======================================================");
			System.out.println("OneCData have - " + countContainsInSAMMap + " elements which contained in SAMMBData");
			System.out.println(
					"OneCData have - " + countNotContainsInSAMMap + " elements which NOT contained in SAMMBData");
			System.out.println("The number of Nomenclature rows = " + allDataMap.size());
			return allDataMap;

		} else {
			throw new Exception("No data in maps");
		}
	}

	public void writeFile() throws IOException {
		if (allDataMap != null) {

			allDataMap.forEach((k,
					v) -> finalData += k + semilicon 
									 + v.getPrice_one_c() + semilicon 
									 + v.getLeft_over_one_c() + semilicon 
									 + v.getDays_first_stock() + semilicon 
									 + v.getPrice_sam_mb() + semilicon 
									 + v.getLeft_over_sam_mb() + semilicon
									 + v.getDays_second_stock() + n);
			FileOutputStream outputStream = new FileOutputStream(fileName);

			byte[] strToBytes = finalData.getBytes();

			outputStream.write(strToBytes);

			outputStream.close();
		}
	}

}
