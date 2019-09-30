package express.avto.files.upload;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import express.avto.rows.AllDataRow;
import express.avto.rows.OneCAllDataRow;

public class UploadWithoutSamMb {
	private static UploadWithoutSamMb upload = new UploadWithoutSamMb();
	private static final String n = "\r\n";
	private static final String semilicon = ";";
	private static final String replaceWith = "";
	private final Pattern p = Pattern.compile("( )*$");

	private HashMap<String, OneCAllDataRow> oneCMap = null;
	private HashMap<String, AllDataRow> allDataMap;
	private String finalData;
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public HashMap<String, OneCAllDataRow> getOneCMap() {
		return oneCMap;
	}

	public void setOneCMap(HashMap<String, OneCAllDataRow> oneCMap) {
		this.oneCMap = oneCMap;
	}

	private UploadWithoutSamMb() {
		super();
		allDataMap = new HashMap<String, AllDataRow>();
		finalData = "";
	}

	public static UploadWithoutSamMb getInstanceUpload() {
		return upload;
	}

	public HashMap<String, AllDataRow> configureUploadMap() throws Exception {

		if (oneCMap != null) {

			oneCMap.entrySet().stream().forEach(e -> {
				AllDataRow tmpRow = new AllDataRow();

				tmpRow.setId(e.getKey());
				tmpRow.setPrice_one_c(e.getValue().getPrice());
				tmpRow.setLeft_over_one_c(e.getValue().getLeftOver());
				tmpRow.setDays_first_stock("0");
				tmpRow.setPrice_sam_mb("");
				tmpRow.setLeft_over_sam_mb("");
				tmpRow.setDays_second_stock("");

				allDataMap.put(e.getKey(), tmpRow);

			});

			return allDataMap;

		} else {
			throw new Exception("No data in OneCMap");
		}
	}

	public void writeFile() throws IOException {
		if (allDataMap != null) {

			allDataMap.forEach((k,
					v) -> finalData += k + semilicon + v.getPrice_one_c() + semilicon + v.getLeft_over_one_c()
							+ semilicon + v.getDays_first_stock() + semilicon + v.getPrice_sam_mb() + semilicon
							+ v.getLeft_over_sam_mb() + semilicon + v.getDays_second_stock() + n);
			FileOutputStream outputStream = new FileOutputStream(fileName);

			byte[] strToBytes = finalData.getBytes();

			outputStream.write(strToBytes);

			outputStream.close();
		}
	}
}
