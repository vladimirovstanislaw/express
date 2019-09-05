package express.avto.rows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OneCAllDataRow {
	private String id;
	private String name;
	private String leftOver;
	private String price;
	private static final Pattern patternPrice = Pattern.compile("(\\,[0-9]*( )*)|(\\.[0-9]*( )*)$");
	private static Matcher matcherPrice = null;
	private static final String replaceWithEmptyString = "";

	private static final Pattern patternId = Pattern.compile("( )*");
	private static Matcher matcherId = null;

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
