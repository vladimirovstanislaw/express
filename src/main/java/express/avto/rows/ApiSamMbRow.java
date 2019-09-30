package express.avto.rows;

public class ApiSamMbRow {// aka COMMODITY
	public static String codeManufacturerXMLName = "SMNFCODE";
	public static String priceXMLName = "NPRICE_RRP";
	public static String leftOversXMLName = "NREST";
	public static String fullNameXMLName = "SMODIFNAME";
	public static String idSamMbXMLName = "NNOMMODIF";

	public static String descriptionName = "DESCRIPTION";

	String codeManufacturer;
	String price;
	String leftOvers;
	String fullName;
	String idSamMb;

	public ApiSamMbRow() {
		super();
	}

	public ApiSamMbRow(String codeManufacturer, String price, String leftOvers, String fullName, String idSamMb) {
		super();
		this.codeManufacturer = codeManufacturer;
		this.price = price;
		this.leftOvers = leftOvers;
		this.fullName = fullName;
		this.idSamMb = idSamMb;
	}

	public String getCodeManufacturer() {
		return codeManufacturer;
	}

	public void setCodeManufacturer(String codeManufacturer) {
		this.codeManufacturer = codeManufacturer;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getLeftOvers() {
		return leftOvers;
	}

	public void setLeftOvers(String leftOvers) {
		this.leftOvers = leftOvers;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getIdSamMb() {
		return idSamMb;
	}

	public void setIdSamMb(String idSamMb) {
		this.idSamMb = idSamMb;
	}

	@Override
	public String toString() {
		return "ApiSamMbRow [codeManufacturer=" + codeManufacturer + ", price=" + price + ", leftOvers=" + leftOvers
				+ ", fullName=" + fullName + ", idSamMb=" + idSamMb + "]";
	}

}
