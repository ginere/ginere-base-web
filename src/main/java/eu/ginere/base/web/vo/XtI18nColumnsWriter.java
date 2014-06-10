package eu.ginere.base.web.vo;

import java.beans.IntrospectionException;

import org.apache.log4j.Logger;

import eu.ginere.base.util.descriptor.AbstractClassDescriptor;
import eu.ginere.base.util.i18n.Language;

public class XtI18nColumnsWriter extends AbstractI18nClassWriter {
	private static final Logger log = Logger.getLogger(XtI18nColumnsWriter.class);
	
//	private static Map <String,XtI18nColumnsWriter> cache=new Hashtable<String,XtI18nColumnsWriter>();
//
//
//	public static XtI18nColumnsWriter get(String className) throws ClassNotFoundException, IntrospectionException{
//		if (cache.containsKey(className)){
//			return cache.get(className);
//		} else {
//			Class clazz=Class.forName(className);
//			XtI18nColumnsWriter ret=new XtI18nColumnsWriter(clazz);
//
//			cache.put(className,ret);
//
//			return ret;
//		}
//	}
//
//	public static XtI18nColumnsWriter get(Class clazz) throws IntrospectionException{
//		String className=clazz.getName();
//
//		if (cache.containsKey(className)){
//			return cache.get(className);
//		} else {
//			XtI18nColumnsWriter ret=new XtI18nColumnsWriter(clazz);
//
//			cache.put(className,ret);
//
//			return ret;
//		}
//	}


	private XtI18nColumnsWriter(AbstractClassDescriptor descriptor) throws IntrospectionException{
		super(descriptor);
	}
	
	public String innerPrinter(Language language) {
		// Only the set the mandatory parameters
		StringBuilder builder=new StringBuilder();
		
		builder.append('[');
		int i=0;
		for (VoPropertyDescriptor descriptor:list){
			i++;
			builder.append('{');
			builder.append("dataIndex:\"");
			builder.append(descriptor.getName());
			builder.append("\",header:\"");
			builder.append(descriptor.getDisplayName(language));
			builder.append("\"}");

			// La coma
			if (i<list.size()){
				builder.append(',');

			}
		}
		builder.append(']');

		return builder.toString();
	}
}
