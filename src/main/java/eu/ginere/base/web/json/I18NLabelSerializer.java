package eu.ginere.base.web.json;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.ginere.base.util.i18n.I18NLabel;


public class I18NLabelSerializer implements JsonSerializer<I18NLabel>{
	static final Logger log = Logger.getLogger(I18NLabelSerializer.class);
	
	public JsonElement serialize(I18NLabel src, 
								 Type typeOfSrc,
								 JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}
}
