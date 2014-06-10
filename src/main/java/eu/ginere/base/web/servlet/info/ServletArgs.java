package eu.ginere.base.web.servlet.info;

import eu.ginere.base.util.enumeration.SQLEnum;



public class ServletArgs {
	
	public static final ServletArgs[] NO_ARGS = null;

	private static final String STRING = "String";

	private static final String MANDATORY = "Mandatory";

	private static final String BOOLEAN = "Boolean";

	private static final String SQLENUM = "SQLEnum";

	private static final String DOUBLE = "Double";
	private static final String INT = "Int";
	
//	public static final String name;
//	public static final String type;
//	public static final String description;
//	public static final String extra;
	
	public final String value;
	
	public static ServletArgs getMandatoryStringParameter(String description,String name) {
		return new ServletArgs(name,
							   STRING,
							   description,
							   MANDATORY);
	}
	
	public static ServletArgs getMandatoryStringParameter(String name) {
		return new ServletArgs(name,
							   STRING,
							   null,
							   MANDATORY);
	}
	
	
	public static ServletArgs getStringParameter(String description,String name) {
		return new ServletArgs(name,
				   STRING,
				   description,
				   null);
	}
	
	public static ServletArgs getStringParameter(String name) {
		return new ServletArgs(name,
				   STRING,
				   null,
				   null);
	}
	

	public static ServletArgs getSQLEnumParameter(String name,
			Class<? extends SQLEnum> clazz) {
		return new ServletArgs(name,
								SQLENUM,
								null,
								null);
	}


	public static ServletArgs getMandatorySQLEnumParameter(String name) {
		return new ServletArgs(name,
				SQLENUM,
				null,
				MANDATORY);
	}

	public static ServletArgs getBooleanParameter(String description, String name) {
		return new ServletArgs(name,
				   BOOLEAN,
				   description,
				   null);
	}


	public static ServletArgs getMandatoryBooleanParameter(String description, String name) {
		return new ServletArgs(name,
				   BOOLEAN,
				   description,
				   MANDATORY);
	}
	
	public static ServletArgs getDoubleParameter(String description, String name) {
		return new ServletArgs(name,
				   DOUBLE,
				   description,
				   null);
	}

	public static ServletArgs getMandatoryDoubleParameter(String description, String name) {
		return new ServletArgs(name,
				   DOUBLE,
				   description,
				   MANDATORY);
	}
	
	public static ServletArgs getMandatoryIntParameter(String description, String name) {
		return new ServletArgs(name,
				   INT,
				   description,
				   MANDATORY);
	}
	
	public static ServletArgs getIntParameter(String description, String name) {
		return new ServletArgs(name,
				   INT,
				   description,
				   null);
	}
	
	public static ServletArgs getIntParameter(String name) {
		return new ServletArgs(name,
				   INT,
				   null,
				   null);
	}
	
	public static ServletArgs getDoubleParameter(String name) {
		return new ServletArgs(name,
				   DOUBLE,
				   null,
				   null);
	}
	
	
	
	private ServletArgs(String name,
						String type,
						String description,
						String extra
						){
		
		StringBuilder buffer=new StringBuilder();
		
		buffer.append(" - ");
		buffer.append(name);
		buffer.append(" [ ");
		buffer.append(type);
		buffer.append(" ] ");
		if (extra!=null) {
			buffer.append(extra);
		}
		if (description!=null) {
			buffer.append(" : ");
			buffer.append(description);
		}

		this.value=buffer.toString();
	}
	
	public String toString(){
		return value;
	}

}
