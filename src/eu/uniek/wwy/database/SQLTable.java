package eu.uniek.wwy.database;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * woohoo sql table
 * @author your mother
 * @see www.redtube.com
 *
 */
public class SQLTable {
	
	private String name; 
	private Map<String, ColumnType> columns = new LinkedHashMap<String, ColumnType>();
	
	public SQLTable(String name) {
		columns.put("KEY_ID", ColumnType.INTEGER_PRIMARY_KEY);
		this.name = name;
	}
	public SQLTable(String name, String columnName, ColumnType columnType) {
		columns.put(columnName, columnType);
		this.name = name;
	}
	public enum ColumnType {
		INTEGER("INTEGER"),
		INTEGER_PRIMARY_KEY("INTEGER PRIMARY KEY"),
		TEXT("TEXT");
		
		String name;
		
		ColumnType(String s) {
			name = s;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, ColumnType> getColumns() {
		return columns;
	}

	public void setColumns(Map<String, ColumnType> columns) {
		this.columns = columns;
	}
	
}
