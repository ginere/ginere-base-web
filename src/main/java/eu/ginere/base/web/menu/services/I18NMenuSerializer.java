package eu.ginere.base.web.menu.services;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.ginere.base.web.menu.MenuItem;




public class I18NMenuSerializer implements JsonSerializer<MenuItem>{
	
	public JsonElement serialize(MenuItem src, 
								 Type typeOfSrc,
								 JsonSerializationContext context) {
		
		if (src.isSeparator()){
			return new JsonPrimitive("-");
		} else {
			JsonObject ret=new JsonObject();
			
			ret.addProperty("label",src.getLabel());
			ret.addProperty("id",src.getId());
			ret.addProperty("right",src.getRight());
			ret.addProperty("url",src.getUrl());
			ret.addProperty("cls",src.getCls());
			ret.addProperty("window",src.isWindow());
			
			ret.add("itemList",context.serialize(src.getItemList()));
			return ret;
		
		}
	}
}
