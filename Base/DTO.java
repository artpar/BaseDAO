package Base;

import java.util.ArrayList;

public class MainDTO
{
	private Boolean c;
	public ArrayList<String> columns;
	public String tablename;
	public void setC(Boolean c)
	{
		this.c = c;
	}

	public Boolean getC()
	{
		return c;
	}

	public void setTablename(String tablename)
	{
		this.tablename = tablename;
	}

	public String getTablename()
	{
		return tablename;
	}
}
