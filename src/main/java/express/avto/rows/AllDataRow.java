package express.avto.rows;

public class AllDataRow {
	private String id;

	private String price_one_c;
	private String left_over_one_c;
	private String days_first_stock; // allways 0

	private String price_sam_mb;
	private String left_over_sam_mb;
	private String days_second_stock;

	public AllDataRow() {
		super();
	}

	public AllDataRow(String id, String price_one_c, String left_over_one_c, String days_first_stock,
			String price_sam_mb, String left_over_sam_mb, String days_second_stock) {
		super();
		this.id = id;
		this.price_one_c = price_one_c;
		this.left_over_one_c = left_over_one_c;
		this.days_first_stock = days_first_stock;
		this.price_sam_mb = price_sam_mb;
		this.left_over_sam_mb = left_over_sam_mb;
		this.days_second_stock = days_second_stock;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrice_one_c() {
		return price_one_c;
	}

	public void setPrice_one_c(String price_one_c) {
		this.price_one_c = price_one_c;
	}

	public String getLeft_over_one_c() {
		return left_over_one_c;
	}

	public void setLeft_over_one_c(String left_over_one_c) {
		this.left_over_one_c = left_over_one_c;
	}

	public String getDays_first_stock() {
		return days_first_stock;
	}

	public void setDays_first_stock(String days_first_stock) {
		this.days_first_stock = days_first_stock;
	}

	public String getPrice_sam_mb() {
		return price_sam_mb;
	}

	public void setPrice_sam_mb(String price_sam_mb) {
		this.price_sam_mb = price_sam_mb;
	}

	public String getLeft_over_sam_mb() {
		return left_over_sam_mb;
	}

	public void setLeft_over_sam_mb(String left_over_sam_mb) {
		this.left_over_sam_mb = left_over_sam_mb;
	}

	public String getDays_second_stock() {
		return days_second_stock;
	}

	public void setDays_second_stock(String days_second_stock) {
		this.days_second_stock = days_second_stock;
	}

	@Override
	public String toString() {
		return "AllDataRow [id=" + id + ", price_one_c=" + price_one_c + ", left_over_one_c=" + left_over_one_c
				+ ", days_first_stock=" + days_first_stock + ", price_sam_mb=" + price_sam_mb + ", left_over_sam_mb="
				+ left_over_sam_mb + ", days_second_stock=" + days_second_stock + "]";
	}

}