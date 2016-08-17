package eu.ginere.base.web.servlet;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.enumeration.SQLEnum;
import eu.ginere.base.util.file.FileId;
import eu.ginere.base.util.i18n.I18NLabel;
import eu.ginere.base.util.properties.GlobalFileProperties;
import eu.ginere.base.web.json.FileIdSerializer;
import eu.ginere.base.web.json.I18NLabelSerializer;
import eu.ginere.base.web.json.SQLEnumSerializer;
import eu.ginere.base.web.json.TimestampSerializer;

@SuppressWarnings("serial")
public abstract class JSONServlet extends MainServlet {
	public static final Logger log = Logger.getLogger(JSONServlet.class);

	// constructor gsons
	protected Gson gSonData;
//	private static final String JSON_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss ZZZZ"; // Ejemplo 07/10/2011 11:03:01 +0300 Est json format "d/m/Y H:i:s O" 
	private static final String JSON_DATE_FORMAT = "dd/MM/yyyy HH:mm"; // Ejemplo 07/10/2011 11:03:01 +0300 Est json format "d/m/Y H:i:s O" 
	public static final String EXT_DATE_FORMAT = "d/m/Y H:i:s O"; // el formato inverso a JSON_DATE_FORMAT para usar en Ext
	
	
	public static final String CHARSET = "UTF-8";
	public static final String CONTENT_TYPE_JSON = "application/json"; // application/json  // text/javascript
	public static final String CONTENT_TYPE_JAVASCRIPT = "text/javascript"; // application/json  // text/javascript

	public static final String JSONP_PARAMETER=GlobalFileProperties.getStringValue(JSONServlet.class, "JSONP_PARAMETER","The property name that will be used in JSONP calls",null);


	/**
	 * Iniciación del servlet
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
//		GsonBuilder gsonBuilder = new GsonBuilder();
//		gsonBuilder.setDateFormat(JSON_DATE_FORMAT);
//		
//		// Registrando las enumeraciones Pero no funciona por que no se pueden registrar interfaces.
//		// gsonBuilder.registerTypeAdapter(SQLEnum.class, new SQLEnumSerializer());
//		gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampSerializer(JSON_DATE_FORMAT));
//		gsonBuilder.registerTypeAdapter(SQLEnum.class, new SQLEnumSerializer());
//		gsonBuilder.registerTypeAdapter(FileId.class, new FileIdSerializer());
		
		GsonBuilder gsonBuilder=createGsonBuilder(getDateFormat());
		
//		gsonBuilder.setPrettyPrinting();
		try {
			this.servletReturn=configureGsonBuilder(gsonBuilder);
		} catch (IntrospectionException e) {
			throw new ServletException("",e);
		}

		//		configureGsonBuilder(gsonBuilder);
		
		gSonData = gsonBuilder.create();
	}
	
	public String getDateFormat(){
		return JSON_DATE_FORMAT;
	}
	public static GsonBuilder createGsonBuilder(String dateFormat) throws ServletException {

		GsonBuilder gsonBuilder = new GsonBuilder();
		
		gsonBuilder.serializeNulls();
		gsonBuilder.setDateFormat(dateFormat);
		
		// Registrando las enumeraciones Pero no funciona por que no se pueden registrar interfaces.
		// gsonBuilder.registerTypeAdapter(SQLEnum.class, new SQLEnumSerializer());
		gsonBuilder.registerTypeAdapter(Timestamp.class, new TimestampSerializer(dateFormat));
		gsonBuilder.registerTypeAdapter(SQLEnum.class, new SQLEnumSerializer());
		gsonBuilder.registerTypeAdapter(FileId.class, new FileIdSerializer());
		gsonBuilder.registerTypeAdapter(I18NLabel.class, new I18NLabelSerializer());
		
		if (GlobalFileProperties.getBooleanValue(JSONServlet.class, "SetPrettyPrinting","If true this preatyprenting the json of the services", false)){
			gsonBuilder.setPrettyPrinting();
		}
		
//		try {
//			servlet.servletReturn=servlet.configureGsonBuilder(gsonBuilder);
//		} catch (IntrospectionException e) {
//			throw new ServletException("",e);
//		}
//
//		//		configureGsonBuilder(gsonBuilder);
//		
//		servlet.gSonData = gsonBuilder.create();
		
		return gsonBuilder;
	}


	protected  abstract String configureGsonBuilder(GsonBuilder gsonBuilder) throws IntrospectionException;

	/**
	 * Para configurar los adaptadores que sean necesarios
	 * @param gsonBuilder
	 */
	//	protected abstract void configureGsonBuilder(GsonBuilder gsonBuilder);
	

	protected void doService(HttpServletRequest request,
							 HttpServletResponse response) throws ServletException, IOException,DaoManagerException {
		
		String callback=null;
		
		if (JSONP_PARAMETER!=null){
			callback=getStringParameter(request, JSONP_PARAMETER, null);	
		}		

		Object obj = doTaskJSONService(request, response);
		String jsonString = gSonData.toJson(obj);
		//		ServletOutputStream output = response.getOutputStream();

		response.setCharacterEncoding(CHARSET);
		PrintWriter writer = response.getWriter();


		if (callback == null){
			response.setContentType(CONTENT_TYPE_JSON);

			writer.print(jsonString);		
		} else {
			response.setContentType(CONTENT_TYPE_JAVASCRIPT);

			writer.print(callback);
			writer.print("(");
			writer.print(jsonString);		
			writer.print(");");
		}
				
		//				output.write(jsonString.getBytes(CHARSET));
		//				output.flush();
		//				output.close();

		writer.flush();
		writer.close();
	}

	/**
	 * Devuelve un String en formato JSON. Debe crear una instancia json
	 * llamando a la variable de la clase {@link #GSonData} y a su método
	 * toJson
	 * 
	 * @see Gson#toJson(Object)
	 * @param request
	 * @return String con formato Json
	 * @throws ServletException
	 * @throws IOException
	 */
	abstract protected Object doTaskJSONService(HttpServletRequest request,
												HttpServletResponse response) throws ServletException, IOException,DaoManagerException;

}
