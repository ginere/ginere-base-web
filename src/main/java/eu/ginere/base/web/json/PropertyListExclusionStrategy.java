package eu.ginere.base.web.json;

import java.beans.IntrospectionException;
import java.util.HashMap;

import com.google.gson.FieldAttributes;

import eu.ginere.base.util.descriptor.PropertyListClassDescriptor;

/**
 * For the class passed class in parameter this tells that all de parameters not present into the array will be excluded.
 * The rest parameters of the rest classes will be added
 * 
 * @author ventura
 *
 */
public class PropertyListExclusionStrategy extends AbstractPropertiesContainerDescriptor implements JSONSerializarDescriptor {

	protected final HashMap <String,String>properties=new HashMap<String,String>();
	protected final String description;
	
	protected final Class<?> clazzToDescrive;
	
    public PropertyListExclusionStrategy(Class<?> clazzToDescrive,String [][]propertyList) throws IntrospectionException {
    	this.clazzToDescrive=clazzToDescrive;
    	
    	for (String []array:propertyList){
    		String propertyName=array[0];
    		
    		properties.put(propertyName,propertyName);
    	}
    	
    	PropertyListClassDescriptor propertiesContainer=new PropertyListClassDescriptor(clazzToDescrive,propertyList);
		this.description=getDescriptionFromPropertiesContainer(propertiesContainer);
		
     }
    
    public boolean shouldSkipClass(Class<?> clazz) {
    	return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
    	if (f.getDeclaredClass() == clazzToDescrive) {
    		return !(properties.containsKey(f.getName()));
    	} else {
    		return true;
    	}
    }
    
	@Override
	public String getDescription() {
		return description;
	}
  }