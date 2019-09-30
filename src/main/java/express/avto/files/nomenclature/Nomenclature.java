package express.avto.files.nomenclature;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import express.avto.rows.EmailLeftOversRow;
import express.avto.rows.OneCAllDataRow;

public class Nomenclature {
	private static Nomenclature nomenclature = new Nomenclature();
	private static final String n = "\r\n";
	private static final String colon = ";";
	private static final String replaceWith = "";
	private final Pattern p = Pattern.compile("( )*$");

	HashMap<String, EmailLeftOversRow> samMap = null;
	HashMap<String, OneCAllDataRow> oneCMap = null;
	HashMap<String, String> nomenclatureMap;
	String finalData;
	String fileName;

	int countContainsInSAMMap = 0;
	int countNotContainsInSAMMap = 0;

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

	public Map<String, String> getNomenclatureMap() {
		return nomenclatureMap;
	}

	public void setNomenclatureMap(HashMap<String, String> nomenclatureMap) {
		this.nomenclatureMap = nomenclatureMap;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	private Nomenclature() {
		super();
		nomenclatureMap = new HashMap<String, String>();
		finalData = "";
	}

	public static Nomenclature getInstanceNomenclature() {
		return nomenclature;
	}

	public HashMap<String, String> configureNomenclatureMap() throws Exception {

		if (samMap != null && oneCMap != null) {

			samMap.entrySet().stream().forEach(e -> {
				nomenclatureMap.put(e.getKey(), e.getValue().getName());
			});
			oneCMap.entrySet().stream().forEach(e -> {
				if (!samMap.containsKey(e.getKey())) {
					nomenclatureMap.put(e.getKey(), e.getValue().getName());
					countNotContainsInSAMMap++;
				} else {
					countContainsInSAMMap++;
				}
			});
			System.out.println(
					"======================================================CONFIGURING NOMENCLATURE======================================================");
			System.out.println("OneCData have - " + countContainsInSAMMap + " elements which contained in SAMMBData");
			System.out.println(
					"OneCData have - " + countNotContainsInSAMMap + " elements which NOT contained in SAMMBData");
			System.out.println("The number of Nomenclature rows = " + nomenclatureMap.size());
			return nomenclatureMap;

		} else {
			throw new Exception("No data in maps");
		}
	}

	public void writeFile() throws IOException {
		if (nomenclatureMap != null) {

			nomenclatureMap.forEach((k, v) -> finalData += k + colon + v + n);
			FileOutputStream outputStream = new FileOutputStream(fileName);

			byte[] strToBytes = finalData.getBytes();

			outputStream.write(strToBytes);

			outputStream.close();
		}
	}

}
