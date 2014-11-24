/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: ExportExcelFile.java,v 1.3 2006/05/07 14:42:40 ventura Exp $
 */
package eu.ginere.web.excel;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.ginere.base.web.servlet.MainServlet;

//import venturasoft.list.ListLayout;
//import venturasoft.servlet.Servlet;
//import venturasoft.taglib.list.FilterListUtils;

/**
 * This is the simple way to export a excel file
 *
 * @author Angel Mendo
 * @version $Revision: 1.3 $
 */
public abstract class ExportExcelFile extends MainServlet {

	public static final Logger log = Logger.getLogger(ExportExcelFile.class);

//	private static final boolean APPLY_FILTER=true;
//	private static final boolean APPLY_SORT=true;
//	private static final boolean APPLY_PAGINATION=false;

	public void doService(
		HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {

		String listName=getStringParameter(request,"LIST_NAME",null);
		//		ListLayout listLayout= FilterListUtils.getListLayout(request,listName);		
		//		List list=listLayout.apply(getObjectList(request,response),APPLY_FILTER,APPLY_SORT,APPLY_PAGINATION);

		List list=getObjectList(request,response);;

        response.setContentType("application/vnd.ms-excel");

        response.setHeader("Content-Disposition",
						   // "attachment; filename="+
						   "filename=" + 
						   getFileName(request, response) + ";");

		// open
		Object auxObj=open(request,response);

		// The header line
		String array[]=getHeaderLine(request,response,auxObj);
		displayLine(request,response,array);

		// the elements
		for (Iterator i=list.iterator();i.hasNext();){
			Object object=i.next();
			array=getLine(request,response,auxObj,object);
			displayLine(request,response,array);
		}

		// The footer line
		array=getFooterLine(request,response,auxObj);
		displayLine(request,response,array);
		
		// close
		close(request,response,auxObj);

		return;
	}
	
	/**
	 * this display the line for this array if array is null the line will not 
	 * be displayed
	 */
	private void displayLine(HttpServletRequest request,
							 HttpServletResponse response,
							 String array[])throws IOException{
		if(array==null){
			return ;
		}
	
		response.setCharacterEncoding(getCharacterEncoding(request,response));
		PrintWriter writer = response.getWriter();
		for (int i=0;i<array.length;i++){
			String value=ExcelUtils.escape(array[i]);			
			writer.print(value);
			if (i<(array.length-1)){
				writer.print(ExcelUtils.COLUMN_SEPARATOR);
			}
		}
		writer.print(ExcelUtils.LINE_SEPARATOR);		
		
//		response.setCharacterEncoding(getCharacterEncoding(request,response));
//		ServletOutputStream out=response.getOutputStream();
//		for (int i=0;i<array.length;i++){
//			String value=ExcelUtils.escape(array[i]);			
//			out.print(value);
//			if (i<(array.length-1)){
//				out.print(ExcelUtils.COLUMN_SEPARATOR);
//			}
//		}
//		out.print(ExcelUtils.LINE_SEPARATOR);
//		
		return ;
	}

	public String getCharacterEncoding(HttpServletRequest request,
			HttpServletResponse response) {
		return request.getCharacterEncoding();
	}

	/**
	 * This will retun the list to export. The current filter will be applied to 
	 * this list.	 
	 */
	protected abstract List getObjectList(HttpServletRequest request,
										  HttpServletResponse response);


	/**
	 * Overwrite this function to change de filename
	 */
	protected String getFileName(HttpServletRequest request,
								 HttpServletResponse response){
		return "ExcelFile.xls";
	}

	/**
	 * This function return the stringto be displayed at the top of 
	 * the execel file.  If This function return null The line will not be displayed<br>
	 * Overwrite this function to set the the header line
	 */
	protected String[] getHeaderLine(HttpServletRequest request,
									 HttpServletResponse response,
									 Object auxObj){
		return null;
		// return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	/**
	 * This function return the string to be displayed at the bottom of 
	 * the execel file. If This function return null The line will not be displayed<br>
	 * Overwrite this function to set the the header line
	 */
	protected String[] getFooterLine(HttpServletRequest request,
									 HttpServletResponse response,
									 Object auxObj){
		return null;
		// return ArrayUtils.EMPTY_STRING_ARRAY;
	}

	/**
	 * This function return the string array to be displayed for this object. 
	 * This function must not return null use ArrayUtils.EMPTY_STRING_ARRAY<br>
	 * Overwrite this function to set the the header line
	 */
	protected abstract String[] getLine(HttpServletRequest request,
										HttpServletResponse response,
										Object auxObject,
										Object objectToExport);

	/**
	 * This function will be called at the begging. the object returned by 
	 * this parameter will be passed to all the functions
	 * @param parameter contains the parameters of the request. The value in the map for each key is a list containing the String values
	 */
	protected Object open(
		HttpServletRequest request,
		HttpServletResponse response){
		return null;
	}

	/**
	 * This function will be called at the end
	 * @param parameter contains the parameters of the request. The value in the map for each key is a list containing the String values
	 * @param object the object returned by the function open
	 */
	protected void close(
		HttpServletRequest request,
		HttpServletResponse response,
		Object auxObject){
		
	}

}
