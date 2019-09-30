package express.avto.files.nomenclature;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import express.avto.rows.ApiSamMbRow;
import express.avto.rows.EmailLeftOversRow;
import express.avto.rows.OneCAllDataRow;

public class NomenclatureApiOneStock {

	private static final String n = "\r\n";
	private static final String colon = ";";
	private static final String replaceWith = "";
	private final Pattern p = Pattern.compile("( )*$");

	HashMap<String, ApiSamMbRow> mapData = null;
	HashMap<String, OneCAllDataRow> oneCMap = null;
	HashMap<String, String> nomenclatureMap;
	String finalData;
	String fileName;

	int intersectionOneCAndMsk = 0;

	public NomenclatureApiOneStock() {
		super();
		nomenclatureMap = new HashMap<String, String>();
		finalData = "";
	}

	public NomenclatureApiOneStock(HashMap<String, ApiSamMbRow> mapData, HashMap<String, OneCAllDataRow> oneCMap,
			HashMap<String, String> nomenclatureMap, String finalData, String fileName, int intersectionOneCAndMsk) {
		super();
		this.mapData = mapData;
		this.oneCMap = oneCMap;
		this.nomenclatureMap = nomenclatureMap;
		this.finalData = finalData;
		this.fileName = fileName;
		this.intersectionOneCAndMsk = intersectionOneCAndMsk;
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

	public HashMap<String, String> getNomenclatureMap() {
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

	public HashMap<String, String> configureNomenclatureMap() throws Exception {

		if (mapData != null && oneCMap != null) {

			mapData.entrySet().stream().forEach(e -> {
				nomenclatureMap.put(e.getKey(), e.getValue().getFullName());
			});
			oneCMap.entrySet().stream().forEach(e -> {
				if (!nomenclatureMap.containsKey(e.getKey())) {
					nomenclatureMap.put(e.getKey(), e.getValue().getName());
					intersectionOneCAndMsk++;
				}
			});
			System.out.println(
					"======================================================CONFIGURING NOMENCLATURE======================================================");
			System.out
					.println("OneCData have - " + intersectionOneCAndMsk + " elements which NOT contained in MskData");
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
