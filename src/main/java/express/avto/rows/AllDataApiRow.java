package express.avto.rows;

public class AllDataApiRow {
	private String id;

	private String priceOneC = "";
	private String leftOverOneC = "";
	private String daysExpressStock = ""; // allways 0

	private String priceFirst = "";
	private String leftOverFirst = "";
	private String daysFirstStock = "";

	private String priceSecond = "";
	private String leftOverSecond = "";
	private String daysSecondStock = "";

	public AllDataApiRow() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPriceOneC() {
		return priceOneC;
	}

	public void setPriceOneC(String priceOneC) {
		this.priceOneC = priceOneC;
	}

	public String getLeftOverOneC() {
		return leftOverOneC;
	}

	public void setLeftOverOneC(String leftOverOneC) {
		this.leftOverOneC = leftOverOneC;
	}

	public String getDaysExpressStock() {
		return daysExpressStock;
	}

	public void setDaysExpressStock(String daysExpressStock) {
		this.daysExpressStock = daysExpressStock;
	}

	public String getPriceFirst() {
		return priceFirst;
	}

	public void setPriceFirst(String priceFirst) {
		this.priceFirst = priceFirst;
	}

	public String getLeftOverFirst() {
		return leftOverFirst;
	}

	public void setLeftOverFirst(String leftOverFirst) {
		this.leftOverFirst = leftOverFirst;
	}

	public String getDaysFirstStock() {
		return daysFirstStock;
	}

	public void setDaysFirstStock(String daysFirstStock) {
		this.daysFirstStock = daysFirstStock;
	}

	public String getPriceSecond() {
		return priceSecond;
	}

	public void setPriceSecond(String priceSecond) {
		this.priceSecond = priceSecond;
	}

	public String getLeftOverSecond() {
		return leftOverSecond;
	}

	public void setLeftOverSecond(String leftOverSecond) {
		this.leftOverSecond = leftOverSecond;
	}

	public String getDaysSecondStock() {
		return daysSecondStock;
	}

	public void setDaysSecondStock(String daysSecondStock) {
		this.daysSecondStock = daysSecondStock;
	}

}