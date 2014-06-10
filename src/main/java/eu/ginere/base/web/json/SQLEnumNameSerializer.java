package eu.ginere.base.web.json;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.ginere.base.util.enumeration.SQLEnum;


public class SQLEnumNameSerializer implements JsonSerializer<SQLEnum>{
	public JsonElement serialize(SQLEnum src, 
								 Type typeOfSrc,
								 JsonSerializationContext context) {
		
		return new JsonPrimitive(src.getName());
	}
}
