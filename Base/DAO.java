package Base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.ticketgoose.utilities.TBDBConnect;
import com.ticketgoose.utilities.UniqueIDGenerator;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MainDAO
{
	protected String capitalize(String x){
		char[] s = x.toCharArray();
		s[0] = Character.toUpperCase(s[0]);
		x = new String(s);
		return x;
	}
	
	/**
	 * get a Connection for Database to be used in execution of SQL
	 * @return java.sql.Connection
	 */
	protected Connection getConnection(){
		Connection connection = null;
		//Make a new Connection to the Database
		return connection;
	}
	
	/**
	 * Generate a New ID
	 * @return String ID
	 */
	protected String newId(){
		String x;
		//Generate a New ID in X
		return x;
	}
	
	/**
	 * Generate a String by Joining the Elements of Array, seperated By Glue
	 * @param glue The String to be placed between two array elements
	 * @param Array The Array to be converted to String
	 * @return String
	 */
	public String Join(String glue, List Array){
		String res ="";
		int i=0;
		for(i=0;i<Array.size()-1;i++){
			res = res + Array.get(i) + glue;
		}
		res = res + Array.get(i);
		return res;
	}
	
	/**
	 * Generate a String of "repeat" repeted count times, seperated by glue
	 * @param glue The part which comes between the "repeat"
	 * @param repeat The string to be repeted Count times
	 * @param count The number of times "repeat" String is repeated
	 * @return String
	 * Repeat(", ", "?", 3) = "?, ?"
	 * Repeat(", ", "Repeat", 3) = "Repeat, Repeat, Repeat" 
	 */
	public String Repeat(String glue, String repeat, Integer count){
		String res ="";
		int i=0;
		for(i=0;i<count-1;i++){
			res = res + repeat + glue;
		}
		res = res + repeat;
		return res;
	}
	
	/**
	 * Pass a Class Object DTO representing a DB Table
	 * DTO should have
	 * 1. public String tablename = name of the table represented by this Object
	 * 2. public ArrayList<String> columns = ArrayList of columns in the Table
	 * 3. private/public String member for each Column in the table
	 * 4. getColumn/setColumn function for each member in #3
	 * 5. ArrayList of columns should have Promary key as the first Element
	 * 
	 * @param <T> Any Class representing a Table and satisfying above properties
	 * @param dto The DTO object of class T
	 * @return a DTO object which was inserted. If ID was absent, ID will be set Using the newID function
	 */
	public <T> T insert(T dto){
		Connection connection = getConnection();
		try{
			ArrayList<String> columns = (ArrayList<String>) dto.getClass().getDeclaredField("columns").get(dto);
			
			String tablename = (String)dto.getClass().getDeclaredField("tablename").get(dto).toString().toUpperCase();
			String sql = "insert into " + tablename + "(`" + Join("`, `", columns) + "`) values (" + Repeat(", ", "?", columns.size()) + ")";
			
			//Field query = new TravelBlogConstants().getClass().getDeclaredField("INSERT_" + (String)dto.getClass().getDeclaredField("tablename").get(dto).toString().toUpperCase());
			//sql = (String) query.get(new TravelBlogConstants());
			PreparedStatement statement = connection.prepareStatement(sql);

			
			if(dto.getClass().getMethod("get" + capitalize(columns.get(0))).invoke(dto) == null){
				String newId = newId();
				Class[] argTypes = new Class[] { String.class };
				dto.getClass().getMethod("set" + capitalize(columns.get(0)), argTypes).invoke(dto, newId);
			}
			for(int i=0; i<columns.size();i++){
				statement.setString(i+1, (String) dto.getClass().getMethod("get" + capitalize(columns.get(i))).invoke(dto));
			}
			statement.execute();
			Class[] argTypes = new Class[] { Boolean.class };
			dto.getClass().getMethod("setC", argTypes).invoke(dto, true);
		}catch(Exception e){
			Class[] argTypes = new Class[] { Boolean.class };
			try
			{
				dto.getClass().getMethod("setC", argTypes).invoke(dto, false);
			} catch (Exception e1)
			{
				e1.printStackTrace();
			} 
			e.printStackTrace();
			return null;
		}
		return dto;
	}

	/**
	 * Insert many Rows at once, Pass an ArrayList of DTO's
	 * This Calls insert for each DTO
	 * DTO should have
	 * 1. public String tablename = name of the table represented by this Object
	 * 2. public ArrayList<String> columns = ArrayList of columns in the Table
	 * 3. private/public String member for each Column in the table
	 * 4. getColumn/setColumn function for each member in #3
	 * 5. ArrayList of columns should have Promary key as the first Element
	 * 
	 * @param <t> Any DTO class which satisfies the above properties
	 * @param dtos An ArrayList of T
	 * @return Returns True
	 */
	public <t> boolean insertMany(ArrayList<t> dtos){
		for(int i=0;i<dtos.size();i++){
			insert(dtos.get(i));
		}
		return true;
	}

	/**
	 * Get a Single Row from the Table where primary Key = id
	 * DTO should have
	 * 1. public String tablename = name of the table represented by this Object
	 * 2. public ArrayList<String> columns = ArrayList of columns in the Table
	 * 3. private/public String member for each Column in the table
	 * 4. getColumn/setColumn function for each member in #3
	 * 5. ArrayList of columns should have Promary key as the first Element
	 * 
	 * @param <T> a DTO Class
	 * @param dto an Object of T, can have all members as Null, no Getter is used
	 * @param id The primary key id
	 * @return DTO object returned byb the sql "select * from tablename where id=?"
	 */
	public <T> T selectById(T dto, String id) {
		try{
			ArrayList<String> columns = (ArrayList<String>) dto.getClass().getDeclaredField("columns").get(dto);
			Class[] argTypes = new Class[] { String.class };
			dto.getClass().getMethod("set" + capitalize(columns.get(0)), argTypes).invoke(dto, id);
			return selectById(dto);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get a Single Row from the Table where primary Key = id
	 * DTO should have
	 * 1. public String tablename = name of the table represented by this Object
	 * 2. public ArrayList<String> columns = ArrayList of columns in the Table
	 * 3. private/public String member for each Column in the table
	 * 4. getColumn/setColumn function for each member in #3
	 * 5. ArrayList of columns should have Promary key as the first Element
	 * 
	 * @param <T> a DTO Class
	 * @param dto an Object of T, should have id(primary key column member) set.
	 * @return DTO object returned byb the sql "select * from tablename where id=?"
	 * 
	 */
	public <T> T selectById(T dto){

		Connection connection = getConnection();
		try{
			ArrayList<String> columns = (ArrayList<String>) dto.getClass().getDeclaredField("columns").get(dto);
			if(dto.getClass().getMethod("get" + columns.get(0)).invoke(dto) == null){
				return null;
			}
			String sql = "select * from " + (String)dto.getClass().getDeclaredField("tablename").get(dto) + " where " + columns.get(0) + "=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, (String)dto.getClass().getMethod("get" + columns.get(0)).invoke(dto));
			ResultSet rs = statement.executeQuery();
			if(rs.next()){
				Class[] argTypes = new Class[] { String.class };
				for(int i=1;i<columns.size();i++){
					String field = columns.get(i);
					dto.getClass().getMethod("set" + capitalize(field), argTypes).invoke(dto, rs.getString(field));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return dto;
	}

	
	/**
	 * Select all Rows and columns from the table, return an ArrayList of DTO
	 * DTO should have
	 * 1. public String tablename = name of the table represented by this Object
	 * 2. public ArrayList<String> columns = ArrayList of columns in the Table
	 * 3. private/public String member for each Column in the table
	 * 4. getColumn/setColumn function for each member in #3
	 * 5. ArrayList of columns should have Promary key as the first Element
	 * 
	 * @param <T> DTO Class
	 * @param dto DTO object, Empty Object, no Getter method is used
	 * @return ArrayList of DTO
	 */
	public <T> ArrayList<T> selectAll(T dto){
		ArrayList<T> dtos = new ArrayList<T>();
		Connection connection = getConnection();
		try{
			String sql = "select * from " + (String)dto.getClass().getDeclaredField("tablename").get(dto);
			PreparedStatement statement = connection.prepareStatement(sql);
			ArrayList<String> columns = (ArrayList<String>) dto.getClass().getDeclaredField("columns").get(dto);
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				T dt = (T) dto.getClass().newInstance();
				Class[] argTypes = new Class[] { String.class };
				for(int i=0;i<columns.size();i++){
					String field = columns.get(i);
					dt.getClass().getMethod("set" + capitalize(field), argTypes).invoke(dt, rs.getString(field));
				}
				dtos.add(dt);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return dtos;
	}

	/**
	 * Select rows from table, only columns listed in List<String> columns, Where
	 * DTO should have
	 * 1. public String tablename = name of the table represented by this Object
	 * 2. public ArrayList<String> columns = ArrayList of columns in the Table
	 * 3. private/public String member for each Column in the table
	 * 4. getColumn/setColumn function for each member in #3
	 * 5. ArrayList of columns should have Promary key as the first Element
	 * 
	 * @param <T> Class DTO
	 * @param dto Object of DTO type having the columns listed in "where" List NOT NULL
	 * @param columns the Columns to Select, Only these columns will have a value in the returned ArrayList<DTO>
	 * @param where The select condition columns, will fetch values for "colName"="value" from the DTO object passed.
	 * @return ArrayList of DTO
	 */
	public <T> ArrayList<T> selectWhere(T dto, List<String> columns, List<String> where){
		ArrayList<T> dtos = new ArrayList<T>();
		String columnClause = "";
		int i;
		if(columns.size()> 0){
			for(i=0;i<columns.size()-1;i++){
				columnClause =  columnClause + columns.get(i) + ", ";
			}
			columnClause =  columnClause + columns.get(i);
		}else{
			try{
				columns = (ArrayList<String>) dto.getClass().getDeclaredField("columns").get(dto);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			columnClause = "*";	
		}

		String whereClause = "";
		if(where.size()>0){
			whereClause = " where ";
			for(i=0;i<where.size()-1;i++){
				whereClause += where.get(i) + " LIKE ?,";
			}
			whereClause += where.get(i) + " LIKE ?";	
		}
		
		try{
			String tablename = (String) dto.getClass().getDeclaredField("tablename").get(dto);
			String sql = "select " + columnClause + " from " + tablename + whereClause;
			Connection connection = getConnection();
			
			PreparedStatement statement = connection.prepareStatement(sql);
			for(i=0;i<where.size();i++){
				statement.setString(i+1, (String) dto.getClass().getMethod("get" + capitalize(where.get(i))).invoke(dto));
			}
			
			ResultSet rs = statement.executeQuery();
			while(rs.next()){
				T dt = (T) dto.getClass().newInstance();
				Class[] argTypes = new Class[] { String.class };
				for(i=0;i<columns.size();i++){
					String field = columns.get(i);
					dt.getClass().getMethod("set" + capitalize(field), argTypes).invoke(dt, rs.getString(field));
				}
				dtos.add(dt);
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return dtos;
	}

	/**
	 * Select all Columns, but where the whereColumn Column match value with DTO object
	 * @param <T> DTO Class
	 * @param dto Object T
	 * @param Single whereColumn column name for where clause
	 * @return ArrayList of DTO
	 * @throws Exception
	 */
	public <T> ArrayList<T> selectWhere(T dto, String whereColumn) throws Exception{
		return selectWhere(dto, new ArrayList(), Arrays.asList(whereColumn));
	}

	/**
	 * Select All rows on more then One condition
	 * @param <T> DTO class
	 * @param dto Object T
	 * @param where List of Columns names to be included in the where clause. Values obtained from DTO
	 * @return
	 */
	public <T> ArrayList<T> selectWhere(T dto, List<String> where){
		return selectWhere(dto, new ArrayList(), where);
	}

	/**
	 * Select Single Column on single Where condition
	 * @param <T> DTO Class
	 * @param dto T Dto
	 * @param selectColumn Which column to select
	 * @param whereColumn where Condition column
	 * @return ArrayList<DTO>
	 */
	public <T> ArrayList<T> selectWhere(T dto, String selectColumn, String whereColumn){
		return selectWhere(dto, Arrays.asList(selectColumn), Arrays.asList(whereColumn));
	}

	/**
	 * Select Multiple columns, but not all, based on one where condition
	 * @param <T> DTO class
	 * @param dto Object T
	 * @param select List of column names to be selected
	 * @param whereColumn where Clause column name, value obtained from dto
	 * @return
	 */
	public <T> ArrayList<T> selectWhere(T dto, List<String> select, String whereColumn){
		return selectWhere(dto, select, Arrays.asList(whereColumn));
	}
	
	/**
	 * Delete a Row in table where ID matches
	 * @param <T> DTO class
	 * @param dto Object T, having primary key set
	 */
	public <T> void deleteById(T dto)
	{

		Connection connection = getConnection();
		try{
			ArrayList<String> columns = (ArrayList<String>) dto.getClass().getDeclaredField("columns").get(dto);

			String sql = "delete  from " + (String)dto.getClass().getDeclaredField("tablename").get(dto) + " where " + columns.get(0) + "=?";
			PreparedStatement statement = connection.prepareStatement(sql);
			String value = (String)dto.getClass().getMethod("get" + capitalize(columns.get(0))).invoke(dto);
			if(value == null)return;
			statement.setString(1, (String)dto.getClass().getMethod("get" + capitalize(columns.get(0))).invoke(dto));
			statement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Delete rows where Column "columnsWhere" Value matches with dto's values
	 * @param <T> DTO Object
	 * @param dto Object T
	 * @param columnsWhere list of columns for the where clause, does not do anything if this is empty
	 */
	public <T> void deleteWhere(T dto, List<String> columnsWhere)
	{
		if(columnsWhere.size()<1)return;
		Connection connection = getConnection();
		try{
			String sql = "delete from " + (String)dto.getClass().getDeclaredField("tablename").get(dto) + " where ";
			int i;
			for(i=0;i<columnsWhere.size()-1;i++){
				sql = sql + columnsWhere.get(i) + "=?,";
			}
			sql = sql + columnsWhere.get(i) + "=?";
			
			PreparedStatement statement = connection.prepareStatement(sql);
			for(i=0;i<columnsWhere.size();i++){
				statement.setString(i+1, (String) dto.getClass().getMethod("get" + capitalize(columnsWhere.get(i))).invoke(dto));
			}
			statement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Update a row, By ID, Updates column Values for columns in the list columnsToUpdate
	 * ID fetched from this DTO
	 * 
	 * @param <T> DTO Class
	 * @param dto Object T
	 * @param columnsToUpdate List of columns to be updated
	 */
	public <T> void updateById(T dto, List<String> columnsToUpdate)
	{
		if(columnsToUpdate.size() < 1)return;
		Connection connection = getConnection();
		try{
			ArrayList<String> columns = (ArrayList<String>) dto.getClass().getDeclaredField("columns").get(dto);
			String sql = "update " + (String)dto.getClass().getDeclaredField("tablename").get(dto) + " set ";
			int i;
			for(i=0;i<columnsToUpdate.size()-1;i++){
				sql = sql + columnsToUpdate.get(i) + "=?,";
			}
			sql = sql + columnsToUpdate.get(i) + "=? where " + columns.get(0) + "=?";
			
			PreparedStatement statement = connection.prepareStatement(sql);
			for(i=0;i<columnsToUpdate.size();i++){
				statement.setString(i+1, (String) dto.getClass().getMethod("get" + capitalize(columnsToUpdate.get(i))).invoke(dto));
			}
			statement.setString(i+1, (String) dto.getClass().getMethod("get" + capitalize(columns.get(0))).invoke(dto));
			statement.executeUpdate();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
