package eu.ginere.base.web.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.enumeration.SQLEnum;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;

@SuppressWarnings("serial")
public abstract class Constants extends MainServlet{

	public static final String URI = "/servlet/util/Constants";
	public static final String DESCRIPTION = "Returns the constants";

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}
	
	protected abstract void load();
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
		load();
		}catch (Exception e) {
			log.error("Whiles loadind constants",e);
		}
	}
//	private static Class<? extends SQLEnum> classArray[]=new Class[]{
//		UserStatus.class,
//		FriendStatus.class,
//	};
//	
	protected long getLastModifiedException(HttpServletRequest request) {
		long sessionLastModified=getSessionLastModified(request);
		long sqlLastModified=SQLEnum.getLastModified();
		if (sqlLastModified>sessionLastModified){
			return sqlLastModified;
		} else {
			return sessionLastModified;
		}
	}
	
	protected void doService(HttpServletRequest request,
			 HttpServletResponse response) throws ServletException, IOException,DaoManagerException{

		response.setContentType("text/javascript");
		response.setCharacterEncoding("UTF-8");
		
		PrintWriter writer=response.getWriter();
		

//		writer.println("var APP_STORES = {");

//		boolean isFirst=true;
		
		Set<Class<? extends SQLEnum>>set=SQLEnum.getChildClasses();
		
		for (Class clazz:set){
//			if (isFirst){
//				isFirst=false;
//			} else {
//				writer.println(',');
//			}
			
			printStore(writer,clazz);
		}
		
//		writer.println("}");
		
		writer.close();
	}
	
	
	private void printStore(PrintWriter writer, Class clazz) {
//	    tipoViaStore : new Ext.data.JsonStore({  
//	        fields: ['id', 'name'], 
//	        data: <%= gSonData.toJson(TipoVia.values()) %>
//	    })


	    String name=getName(clazz);
		List<SQLEnum> list=SQLEnum.values(clazz);
		
		writer.print(StringEscapeUtils.escapeJavaScript(name));
		writer.println(" = new Ext.data.JsonStore({  ");		
		writer.println(" fields: ['id', 'name','description'],"); 
		writer.println(" data: [	");		 
		
		boolean isFirst=true;
		for (SQLEnum value:list){
//			{id:'false',name:'Disponible'},
			if (isFirst){
				isFirst=false;
			} else {
				writer.println(',');
			}

			writer.print("{id:'");
			writer.print(StringEscapeUtils.escapeJavaScript(value.getId()));
			writer.print("',name:'");
			writer.print(StringEscapeUtils.escapeJavaScript(value.getName()));
			writer.print("',description:'");
			writer.print(StringEscapeUtils.escapeJavaScript(value.getDescription()));
			writer.print("'}");
			
		}
		writer.println("]});"); 
	
	}

	private String getName(Class clazz) {
		StringBuilder buffer=new StringBuilder();
		
		buffer.append("app.stores");
		
		String className=clazz.getName();
		int index=className.lastIndexOf('.');
		
		buffer.append(className.substring(index));
		
		
		return buffer.toString();
	}


	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return MainServlet.PUBLIC_ACCESS;
	}

	@Override
	protected String getUri() {
		return URI;
	}

	@Override
	protected String getDescription() {
		return DESCRIPTION;
	}
}
