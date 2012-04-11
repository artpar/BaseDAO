package Example;

import java.util.ArrayList;
import java.util.Arrays;
import Base.DTO;

public class StationDTO extends DTO
{
	private String id;
	private String name;
	public final static String tablename = "station";
	public final static ArrayList<String> columns = new ArrayList<String>(Arrays.asList("Id", "Name"));
	
	public void setId(String id)
	{
		this.id = id;
	}
	public String getId()
	{
		return id;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return name;
	}
	
}
