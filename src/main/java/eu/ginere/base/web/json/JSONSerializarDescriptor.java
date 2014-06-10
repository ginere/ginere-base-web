package eu.ginere.base.web.json;

import com.google.gson.ExclusionStrategy;

public interface JSONSerializarDescriptor extends ExclusionStrategy{
	
	/**
	 * Returns a human readable description of the properties that this JSON Serialize will provide.
	 * @return
	 */
	public String getDescription();

}
