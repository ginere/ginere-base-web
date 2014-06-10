package eu.ginere.base.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.dao.DaoManagerException;

/**
 * @author ventura
 *
 * This retnrs a string, if null is returned no string will be used
 */
abstract public class SimpleDataServlet extends MainServlet {

	private static final long serialVersionUID = 1L;
	protected static final String TRUE = "true";
	protected static final String FALSE = "false";
	
	protected String getDefaultString(){
		return "";
	}
	
	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,DaoManagerException {
		String simpleData = doSimpleDataService(request, response);

//		PrintWriter writer = response.getWriter();
//		if (simpleData!=null){
//			writer.print(simpleData);
//		} else {
//			writer.print(getDefaultString());
//		}
//		writer.flush();
//		writer.close();
		write(request, response, simpleData, getDefaultString());
	}

	public static void write(HttpServletRequest request,
			HttpServletResponse response,String simpleData,String defaultString)throws ServletException, IOException{
		
		PrintWriter writer = response.getWriter();
		if (simpleData!=null){
			writer.print(simpleData);
		} else {
			writer.print(defaultString);
		}
		writer.flush();
		writer.close();
		
	}
	abstract protected String doSimpleDataService(HttpServletRequest request,
												  HttpServletResponse response) throws ServletException,IOException,DaoManagerException;

}
