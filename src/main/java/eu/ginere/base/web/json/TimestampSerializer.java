package eu.ginere.base.web.json;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;


public class TimestampSerializer implements JsonSerializer<Timestamp>{
    private final DateFormat format;

	public TimestampSerializer(final String datePattern) {
		this.format = new SimpleDateFormat(datePattern);
    }

    // These methods need to be synchronized since JDK DateFormat classes are not thread-safe
    // See issue 162
    public JsonElement serialize(Timestamp src, Type typeOfSrc, JsonSerializationContext context) {
		synchronized (format) {
			String dateFormatAsString = format.format(src);
			return new JsonPrimitive(dateFormatAsString);
		}
    }
}
