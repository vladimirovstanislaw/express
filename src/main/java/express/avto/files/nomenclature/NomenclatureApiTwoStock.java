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

public class NomenclatureApiTwoStock {

	private static final String n = "\r\n";
	private static final String colon = ";";
	private static final String replaceWith = "";
	private final Pattern p = Pattern.compile("( )*$");

	HashMap<String, ApiSamMbRow> mapDataFirst = null;
	HashMap<String, ApiSamMbRow> mapDataSecond = null;
	HashMap<String, OneCAllDataRow> oneCMap = null;
	HashMap<String, String> nomenclatureMap;
	String finalData;
	String fileName;

	int intersectionOneCAndMsk = 0;
	int intersectionOneCAndMsk_AndPerm = 0;

	public NomenclatureApiTwoStock() {
		super();
		nomenclatureMap = new HashMap<String, String>();
		finalData = "";
	}

	public NomenclatureApiTwoStock(HashMap<String, ApiSamMbRow> mapDataFirst,
			HashMap<String, ApiSamMbRow> mapDataSecond, HashMap<String, OneCAllDataRow> oneCMap,
			HashMap<String, String> nomenclatureMap, String finalData, String fileName) {
		super();
		this.mapDataFirst = mapDataFirst;
		this.mapDataSecond = mapDataSecond;
		this.oneCMap = oneCMap;
		this.nomenclatureMap = nomenclatureMap;
		this.finalData = finalData;
		this.fileName = fileName;
	}

	public HashMap<String, ApiSamMbRow> getMapDataFirst() {
		return mapDataFirst;
	}

	public void setMapDataFirst(HashMap<String, ApiSamMbRow> mapDataMsk) {
		this.mapDataFirst = mapDataMsk;
	}

	public HashMap<String, ApiSamMbRow> getMapDataSecond() {
		return mapDataSecond;
	}

	public void setMapDataSecond(HashMap<String, ApiSamMbRow> mapDataPerm) {
		this.mapDataSecond = mapDataPerm;
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

	public HashMap<String, String> configureNomenclatureMap() throws Exception {

		if (mapDataFirst != null && mapDataSecond != null && oneCMap != null) {

			mapDataFirst.entrySet().stream().forEach(e -> {
				nomenclatureMap.put(e.getKey(), e.getValue().getFullName());
			});
			oneCMap.entrySet().stream().forEach(e -> {
				if (!nomenclatureMap.containsKey(e.getKey())) {
					nomenclatureMap.put(e.getKey(), e.getValue().getName());
					intersectionOneCAndMsk++;
				}
			});
			mapDataSecond.entrySet().stream().forEach(e -> {
				if (!nomenclatureMap.containsKey(e.getKey())) {
					nomenclatureMap.put(e.getKey(), e.getValue().getFullName());
					intersectionOneCAndMsk_AndPerm++;
				}
			});
			System.out.println(
					"======================================================CONFIGURING NOMENCLATURE======================================================");
			System.out
					.println("OneCData have - " + intersectionOneCAndMsk + " elements which NOT contained in MskData");
			System.out.println("PermData have - " + intersectionOneCAndMsk_AndPerm
					+ " elements which NOT contained in MskData_AndOneCData");
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
