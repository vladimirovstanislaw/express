package express.avto.parsers;

import java.util.HashMap;
import java.util.Map;

import express.avto.rows.OneCAllDataRow;

public class OneCParser {
	private String filenameFrom = null;
	private Map<String, OneCAllDataRow> oneCMap;

	private static OneCParser parser = new OneCParser();

	private OneCParser() {
		super();
		oneCMap = new HashMap<String, OneCAllDataRow>();
	}

	public static OneCParser getInstance() {
		return parser;
	}

	private OneCParser(String filenameFrom, Map<String, OneCAllDataRow> oneCMap) {
		super();
		this.filenameFrom = filenameFrom;
		this.oneCMap = oneCMap;
	}

	@Override
	public String toString() {
		return "OneCParser [filenameFrom=" + filenameFrom + ", asIsCentralProviderMap=" + oneCMap + "]";
	}

}
