package eu.ginere.base.web.vo;


import java.util.Date;

import org.apache.log4j.Logger;

import eu.ginere.base.util.descriptor.InnerPropertyDescriptor;


public class VoPropertyDescriptor extends InnerPropertyDescriptor{
	private static final Logger log = Logger.getLogger(VoPropertyDescriptor.class);

//	private final Class parentClass;
//	private final String name;
//	private final Class propertyClazz;
	private boolean visible;
	private String format;
	private String type;

	public VoPropertyDescriptor(InnerPropertyDescriptor propertyDescriptor) {
		super(propertyDescriptor);
		
//		this.parentClass = propertyDescriptor.parentClass;
//		this.name = propertyName;
//		this.propertyClazz = clazz;
		this.visible = true;
		this.format = null;
		this.type = null;
	}
	
	public VoPropertyDescriptor(Class<?> parentClass,
								String propertyName,
								Class<?> propertyClazz,
								boolean visible){
		
		super(parentClass,propertyName,null,propertyClazz,null);
//		this.parentClass=originalClass;
//		this.name=propertyName;
//		this.propertyClazz=clazz;
		this.visible=visible;		
		this.format=null;
		this.type=null;
	}

//	/**
//	 * @return the propertyName
//	 */
//	public String getName() {
//		return name;
//	}

//	/**
//	 * @return the propertyClassName
//	 */
//	public String getClassName() {
//		return propertyClazz.getName();
//	}

	/**
	 * @return the visible
	 */
	public boolean isVisible() {
		return visible;
	}

//	public Object getLabel(Language language) {
//		return I18NConnector.getLabel(language, parentClass.getName(), name);	
//	}

	public boolean isDate() {
		if (Date.class.equals(getPropertyClazz())){
			return true;
		} else {
			return false;
		}
	}
	
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format=format;
	}

	public String getType() {
		if (type!=null){
			return type;
		} else {
			return classToTypeName(getPropertyClazz());
		}
	}

	public void setType(String type) {
		this.type=format;
	}

	/**
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}


	private static final String classToTypeName(Class<?> clazz){
		if (String.class.equals(clazz)){
			return "string";
		} else if (Date.class.equals(clazz)){
			return "date";
		} if (Integer.class.equals(clazz)){
			return "int";
		} if (Float.class.equals(clazz)){
			return "float";		
		} if (Double.class.equals(clazz)){
			return "float";			
		} if (Boolean.class.equals(clazz)){
			return "boolean";	
		} else {
			log.warn("Classe:"+clazz+" not found. Return String");
			return "auto";
		}
	}
	
}
