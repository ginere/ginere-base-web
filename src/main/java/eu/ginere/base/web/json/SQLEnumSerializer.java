package eu.ginere.base.web.json;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.ginere.base.util.enumeration.SQLEnum;


public class SQLEnumSerializer implements JsonSerializer<SQLEnum>{
	public JsonElement serialize(SQLEnum src, 
								 Type typeOfSrc,
								 JsonSerializationContext context) {
		JsonObject ret=new JsonObject();
		
		ret.addProperty("id",src.getId());
		ret.addProperty("name",src.getName());
		ret.addProperty("description",src.getDescription());
		
		return ret;
	}
}
