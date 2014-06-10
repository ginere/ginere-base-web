package eu.ginere.base.web.json;

import java.lang.reflect.Type;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.file.FileId;


public class FileIdSerializer implements JsonSerializer<FileId>{
	static final Logger log = Logger.getLogger(FileIdSerializer.class);
	
	public JsonElement serialize(FileId src, 
								 Type typeOfSrc,
								 JsonSerializationContext context) {
		JsonObject ret=new JsonObject();
		
		ret.addProperty("id",src.getId());
		ret.addProperty("additionalInfo",src.getAdditionalInfo());
		try {
			ret.addProperty("url",src.getContentFileURL());
		} catch (DaoManagerException e) {
			log.error("File id:"+src.getId(),e);
		}
		
		return ret;
	}
}
