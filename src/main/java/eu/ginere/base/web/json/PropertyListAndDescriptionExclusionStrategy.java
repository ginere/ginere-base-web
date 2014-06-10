package eu.ginere.base.web.json;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;

import com.google.gson.FieldAttributes;

import eu.ginere.base.util.descriptor.annotation.Description;

/**
 * For the class passed in parameter this tells that all de parameters not present into the array will be excluded.
 * The rest parameters of the rest classes will show the parameter annotes with the @Description tag.
 * 
 * @author ventura
 *
 */
public class PropertyListAndDescriptionExclusionStrategy extends PropertyListExclusionStrategy implements JSONSerializarDescriptor {

	
    public PropertyListAndDescriptionExclusionStrategy(Class<?> clazzToDescrive,String [][]propertyList) throws IntrospectionException {
    	super(clazzToDescrive,propertyList);
     }
    
    public boolean shouldSkipField(FieldAttributes f) {
    	if (f.getDeclaringClass()== clazzToDescrive) {
    		return !(properties.containsKey(f.getName()));
    	} else {
    		Annotation anotation = f.getAnnotation(Description.class);
    		return anotation == null;
    	}
    }

  }