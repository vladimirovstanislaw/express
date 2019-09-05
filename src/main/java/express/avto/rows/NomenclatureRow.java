package express.avto.rows;

public class NomenclatureRow {
	private String id;
	private String name;

	public NomenclatureRow() {
		super();
	}

	public NomenclatureRow(String id, String name) {
		super();
		this.id = id;
		this.name = name;
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

	@Override
	public String toString() {
		return "NomenclatureRow [id=" + id + ", name=" + name + "]";
	}

}
