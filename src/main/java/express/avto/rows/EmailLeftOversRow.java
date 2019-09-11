package express.avto.rows;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailLeftOversRow {
	private String id;
	private String name;
	private String leftOver;
	private String price;

	private static final Pattern patternPrice = Pattern.compile("(\\,[0-9]*( )*)|(\\.[0-9]*( )*)$");
	private static Matcher matcherPrice = null;
	private static final String replaceWithEmptyString = "";

	private static final Pattern patternId = Pattern.compile("( )*");
	private static Matcher matcherId = null;

	public EmailLeftOversRow() {
		super();
	}

	public EmailLeftOversRow(String id, String name, String leftOver, String price) {
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
		matcherPrice = patternPrice.matcher(leftOver);
		this.leftOver = matcherPrice.replaceAll(replaceWithEmptyString);
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		matcherPrice = patternPrice.matcher(price);
		this.price = matcherPrice.replaceAll(replaceWithEmptyString);
	}

	@Override
	public String toString() {
		return "EmailLeftOversRow [id=" + id + ", name=" + name + ", leftOver=" + leftOver + ", price=" + price + "]";
	}

}
