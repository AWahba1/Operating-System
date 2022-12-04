package operatingSystemProject;

public class MemoryWord {
	private String name;
	private Object value;
	
	
	public MemoryWord(String name, Object value)
	{
		this.name=name;
		this.value=value;
	}
	
	public String toString()
	{
		return "{"+this.name+","+this.value+"}";
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	

}
