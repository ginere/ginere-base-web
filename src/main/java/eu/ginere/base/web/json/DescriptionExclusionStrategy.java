package eu.ginere.base.web.json;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.FieldAttributes;

import eu.ginere.base.util.descriptor.DescriptionAnnotationClassDescriptor;
import eu.ginere.base.util.descriptor.annotation.Description;

public class DescriptionExclusionStrategy extends AbstractPropertiesContainerDescriptor implements JSONSerializarDescriptor {

	
	private static final Map<Class,String> cache=new HashMap<Class, String>();
	private final String description;
	

    public DescriptionExclusionStrategy(Class clazzToDescrive) throws IntrospectionException{
    	if (cache.containsKey(clazzToDescrive)){
    		this.description=cache.get(clazzToDescrive);
    	} else {
    		DescriptionAnnotationClassDescriptor propertiesContainer=new DescriptionAnnotationClassDescriptor(clazzToDescrive);
    		this.description=getDescriptionFromPropertiesContainer(propertiesContainer);
    		
    		cache.put(clazzToDescrive, this.description);
    	}
    }

	public boolean shouldSkipClass(Class<?> clazz) {
    	return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {
		Annotation anotation = f.getAnnotation(Description.class);
		return anotation == null;
    }

	@Override
	public String getDescription() {
		return description;
	}
  }