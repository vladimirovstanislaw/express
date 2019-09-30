package express.avto.get.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import express.avto.rows.ApiSamMbRow;

public class ApiData {
	private String urlString;
	private HashMap<String, ApiSamMbRow> map;
	private String typeOfData;

	public ApiData() {
		super();
		map = new HashMap<String, ApiSamMbRow>();
	}

	public ApiData(String urlString, HashMap<String, ApiSamMbRow> map, String typeOfData) {
		super();
		this.urlString = urlString;
		this.map = map;
		this.typeOfData = typeOfData;
	}

	public HashMap<String, ApiSamMbRow> getData() throws IOException, ParserConfigurationException, SAXException {

		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(conn.getInputStream());

		document.getDocumentElement().normalize();

		// получаем весь документ
		Element rootElement = document.getDocumentElement();

		// получаем root элементы COMMODITIES
		NodeList nodesCOMMODITIES = rootElement.getChildNodes();

		// проходим по всем COMMODITIES
		for (int i = 0; i < nodesCOMMODITIES.getLength(); i++) {

			Node COMMODITIES = nodesCOMMODITIES.item(i);

			if (COMMODITIES instanceof Element) {
				// a child element to process NAME="TYRE" ID="1" VALUE="Ўины"
				Element elCOMMODITIES = (Element) COMMODITIES;
//				System.out.println(elCOMMODITIES.toString());

				String id = elCOMMODITIES.getAttribute("ID");
//				String ruName = elCOMMODITIES.getAttribute("VALUE");
//				String name = elCOMMODITIES.getAttribute("NAME");

				if (id.equals("1")) {

					NodeList commodityList = elCOMMODITIES.getElementsByTagName("COMMODITY");

					for (int temp = 0; temp < commodityList.getLength(); temp++) {

						Node commodity = commodityList.item(temp);

//						System.out.println("\nCurrent Element :" + commodity.getNodeName());

						if (commodity.getNodeType() == Node.ELEMENT_NODE) {

							ApiSamMbRow tmpDataRow = new ApiSamMbRow();

							Element elCommodity = (Element) commodity;

							try {
//								System.out.println("Full Name : "
//										+ elCommodity.getElementsByTagName("SMODIFNAME").item(0).getTextContent());
//								System.out.println("Code Manufacturer : "
//										+ elCommodity.getElementsByTagName("SMNFCODE").item(0).getTextContent());
//								System.out.println("Id SamMb : "
//										+ elCommodity.getElementsByTagName("NNOMMODIF").item(0).getTextContent());
//								System.out.println("Left Overs : "
//										+ elCommodity.getElementsByTagName("NREST").item(0).getTextContent());
//								System.out.println("Price : "
//										+ elCommodity.getElementsByTagName("NPRICE_RRP").item(0).getTextContent());
								String fullName = elCommodity.getElementsByTagName("SMODIFNAME").item(0)
										.getTextContent();
								String CodeManufacturer = elCommodity.getElementsByTagName("SMNFCODE").item(0)
										.getTextContent();
								String IdSamMb = elCommodity.getElementsByTagName("NNOMMODIF").item(0).getTextContent();
								String LeftOvers = elCommodity.getElementsByTagName("NREST").item(0).getTextContent();
								String Price = elCommodity.getElementsByTagName("NPRICE_RRP").item(0).getTextContent();

								if (CodeManufacturer.contains(" ")) {
									continue;
								}
								if (IdSamMb.contains(" ")) {
									continue;
								}
								if (LeftOvers.contains(" ")) {
									continue;
								}
								if (Price.contains(" ")) {
									continue;
								}
								tmpDataRow.setFullName(
										elCommodity.getElementsByTagName("SMODIFNAME").item(0).getTextContent());
								tmpDataRow.setCodeManufacturer(
										elCommodity.getElementsByTagName("SMNFCODE").item(0).getTextContent());
								tmpDataRow.setIdSamMb(
										elCommodity.getElementsByTagName("NNOMMODIF").item(0).getTextContent());
								tmpDataRow.setLeftOvers(
										elCommodity.getElementsByTagName("NREST").item(0).getTextContent());
								tmpDataRow.setPrice(
										elCommodity.getElementsByTagName("NPRICE_RRP").item(0).getTextContent());
								map.put(tmpDataRow.getCodeManufacturer(), tmpDataRow);
							} catch (Exception e) {

							}

						}
					}
				}
			}
		}
		Date date = new Date();
		System.out.println("Done: " + date.getHours() + ":" + date.getMinutes());
		return map;

	}

	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
		ApiData ad = new ApiData();
		ad.setUrlString(
				"https://webmim.svrauto.ru/api/v1/catalog/unload?access-token=pDEUlhnRn7bfA5FhLH6ddnjIBaeWWmO8&format=xml");
		ad.getData();
		ad.showMeMap();
	}

	public String getUrlString() {
		return urlString;
	}

	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	public HashMap<String, ApiSamMbRow> getMap() {
		return map;
	}

	public void setMap(HashMap<String, ApiSamMbRow> map) {
		this.map = map;
	}

	public void showMeMap() {
		map.entrySet().stream().forEach((o) -> {
			System.out.println("\"" + o.getKey() + "\"" + "; " + "\"" + o.getValue().getLeftOvers() + "\"" + "; " + "\""
					+ o.getValue().getFullName() + "\"" + "; " + "\"" + o.getValue().getIdSamMb() + "\"" + "; " + "\""
					+ o.getValue().getPrice() + "\"" + "; ");
		});
		System.out.println("Map size = " + map.size());
	}

	public String getTypeOfData() {
		return typeOfData;
	}

	public void setTypeOfData(String typeOfData) {
		this.typeOfData = typeOfData;
	}

	@Override
	public String toString() {
		return "ApiData [urlString=" + urlString + ", map=" + map + ", typeOfData=" + typeOfData + "]";
	}

}
