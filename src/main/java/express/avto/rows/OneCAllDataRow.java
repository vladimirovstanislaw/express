package express.avto.rows;

public class OneCAllDataRow {
	private String id;
	private String name;
	private String leftOver;
	private String price;

	public OneCAllDataRow() {
		super();
	}

	public OneCAllDataRow(String id, String name, String leftOver, String price) {
		super();
		this.id = id;
		this.name = name;
		this.leftOver = leftOver;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLeftOver() {
		return leftOver;
	}

	public void setLeftOver(String leftOver) {
		this.leftOver = leftOver;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "EmailLeftOversRow [id=" + id + ", name=" + name + ", leftOver=" + leftOver + ", price=" + price + "]";
	}

}
