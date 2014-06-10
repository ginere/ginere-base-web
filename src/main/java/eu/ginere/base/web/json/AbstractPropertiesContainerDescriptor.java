package eu.ginere.base.web.json;

import java.util.List;

import eu.ginere.base.util.descriptor.AbstractClassDescriptor;
import eu.ginere.base.util.descriptor.InnerPropertyDescriptor;

public abstract class AbstractPropertiesContainerDescriptor {

	
    public static  String getDescriptionFromPropertiesContainer(
    		AbstractClassDescriptor propertiesContainer) {

    	List  <InnerPropertyDescriptor> list=propertiesContainer.getList();
    	StringBuilder buffer=new StringBuilder();
    	
    	for (InnerPropertyDescriptor property:list){
    		buffer.append(property.getDisplayName());
    		buffer.append(" [");
    		buffer.append(property.getClassName());

    		buffer.append("]: ");
    		buffer.append(property.getDisplayDescription());

    		
    		buffer.append('\n');
    	}
    	
    	return buffer.toString(); 	
	}
}
