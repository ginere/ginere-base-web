package eu.ginere.base.web.vo;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import eu.ginere.base.util.descriptor.AbstractClassDescriptor;
import eu.ginere.base.util.descriptor.InnerPropertyDescriptor;

public abstract class AbstractClassWriter {

//	protected final Class clazz;
	protected final List<VoPropertyDescriptor> list;
	protected final Map<String,VoPropertyDescriptor> map;
	
	protected AbstractClassDescriptor descriptor;
	
	public AbstractClassWriter(AbstractClassDescriptor descriptor) throws IntrospectionException{
//		this.clazz=clazz;
		
		List<InnerPropertyDescriptor> innerList=descriptor.getList();
		
		this.list=getProperties(innerList);

		this.map=new Hashtable<String,VoPropertyDescriptor>(list.size());

		for (VoPropertyDescriptor voProperty:list){
			map.put(voProperty.getName(),voProperty);
		}
		
		this.descriptor=descriptor;

	}
	
	private static List<VoPropertyDescriptor> getProperties(List<InnerPropertyDescriptor> innerList) throws IntrospectionException{
		 List <VoPropertyDescriptor>list=new ArrayList<VoPropertyDescriptor>(innerList.size());
		 
		 for (InnerPropertyDescriptor propertyDescriptor:innerList){
			 list.add(new VoPropertyDescriptor(propertyDescriptor));
		 }

		 return list;
	}
	
	public void setVisible(String name,boolean value){

		if (map.containsKey(name)){
			VoPropertyDescriptor property=map.get(name);
			property.setVisible(value);
		}		
	}

	public void setType(String name,String value){
		if (map.containsKey(name)){
			VoPropertyDescriptor property=map.get(name);
			property.setType(value);
		}		
	}

	public void setFormat(String name,String value){
		if (map.containsKey(name)){
			VoPropertyDescriptor property=map.get(name);
			property.setFormat(value);
		}		
	}

	public String print(){
		return innerPrinter();
	}
	
	
	protected abstract String innerPrinter();
}
