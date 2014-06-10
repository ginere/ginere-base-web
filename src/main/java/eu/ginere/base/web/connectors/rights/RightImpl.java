package eu.ginere.base.web.connectors.rights;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;


public class RightImpl implements RightInterface,Serializable {
	public static final Logger log = Logger.getLogger(RightImpl.class);
	
	private final String id;
	private final String name;
	private final String description ;
	private final String appId;

	public RightImpl(String id,String name,String description,String appId){
		this.id=id;
		this.name=name;
		this.description=description;
		this.appId=appId;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getAppId() {
		return appId;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
