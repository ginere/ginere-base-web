package eu.ginere.base.web.vo;

import java.beans.IntrospectionException;

import eu.ginere.base.util.descriptor.AbstractClassDescriptor;
import eu.ginere.base.web.servlet.JSONServlet;

/**
 * @author ventura
 * 
 * 
 * 
allowBlank : Boolean
Used for validating a record, defaults to true. An empty value here will cause Ext.data.Record.isValid to evaluate to false.
Field
 
convert : Function
A function which converts the value provided by the Reader into an object that will be stored in the Record. It is pa...
Field
 
dateFormat : String
(Optional) Used when converting received data into a Date when the type is specified as "date".
A format string for the Date.parseDate function, or "timestamp" if the value provided by the Reader is a UNIX timestamp, or "time" if the value provided by the Reader is a javascript millisecond timestamp. See Date
Field
 
defaultValue : Mixed
The default value used when a Record is being created by a Reader when the item referenced by the mapping does not ex...
Field
 
mapping : String/Number
(Optional) A path expression for use by the Ext.data.DataReader implementation that is creating the Record to extract...
Field
 
name : String
The name by which the field is referenced within the Record. This is referenced by, for example, the dataIndex proper...
Field
 
sortDir : String
Initial direction to sort ("ASC" or "DESC"). Defaults to "ASC".
Field
 
sortType : Function
A function which converts a Field's value to a comparable value in order to ensure correct sort ordering. Predefined ...
Field
 
type : Mixed
The data type for automatic conversion from received data to the stored value if convert has not been specified. This may be specified as a string value. Possible values are
auto (Default, implies no conversion)
string
int
float
boolean
date
This may also be specified by referencing a member of the Ext.data.Types class.
Developers may create their own application-specific data types by defining new members of the Ext.data.Types class.
 
useNull : Boolean
(Optional) Use when converting received data into a Number type (either int or float). If the value cannot be parsed,...
 *
 */
public class XtFieldsWriter extends AbstractClassWriter {
//	private static final Logger log = Logger.getLogger(XtFieldsWriter.class);

//	private static Map <String,XtVoFieldsPrinter> cache=new Hashtable<String,XtVoFieldsPrinter>();
//
//	public static XtVoFieldsPrinter get(String className) throws ClassNotFoundException, IntrospectionException{
//		if (cache.containsKey(className)){
//			return cache.get(className);
//		} else {
//			Class clazz=Class.forName(className);
//			XtVoFieldsPrinter ret=new XtVoFieldsPrinter(clazz);
//
//			cache.put(className,ret);
//
//			return ret;
//		}
//	}
//
//	public static XtVoFieldsPrinter get(Class clazz) throws IntrospectionException{
//		String className=clazz.getName();
//
//		if (cache.containsKey(className)){
//			return cache.get(className);
//		} else {
//			XtVoFieldsPrinter ret=new XtVoFieldsPrinter(clazz);
//
//			cache.put(className,ret);
//
//			return ret;
//		}
//	}
	
	
	public XtFieldsWriter(AbstractClassDescriptor descriptor) throws IntrospectionException{
		super(descriptor);
	}
	

	public String innerPrinter() {
//		fields: ['name', 'url', {name:'size', type: 'float'}, {name:'lastmod', type:'date'}]
		         

		StringBuilder builder=new StringBuilder();
		
		builder.append('[');
		int i=0;
		for (VoPropertyDescriptor descriptor:list){
			i++;
			builder.append('{');
			builder.append("name:\"");
			builder.append(descriptor.getName());
			builder.append("\",type:\"");
			builder.append(descriptor.getType());
			builder.append('\"');

			if (descriptor.isDate()){
				builder.append(", dateFormat: '");
				builder.append(getDateFormat());
				builder.append("'");
			}
			builder.append('}');
		

			// La coma
			if (i<list.size()){
				builder.append(',');

			}
		}
		builder.append(']');

		return builder.toString();
	}


	private Object getDateFormat() {
//		return "d/m/Y H:i:s";
		return JSONServlet.EXT_DATE_FORMAT;
		
//		private static final String JSON_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

//		return "time";
		
	}
}
