package eu.ginere.base.web.services;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 *  Servlet que muestra los objetos en session si son serializables.
 * 
 * @author Pedro Toribio Guardiola
 *
 */
public class ServletSessionTest extends HttpServlet {

	/**
	 *  Serial UID.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constante para el nombre del fichero.
	 */
	private static final String FILE_NAME ="/tmp/test.bin";
	/**
	 * Metodo de servlet service.
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse 
	 * @throws ServletException Throwable 
	 * @throws IOException Throwable
	 */
	public final void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session=request.getSession();		
		PrintWriter out=response.getWriter();
		ObjectOutputStream outStream=new ObjectOutputStream(new FileOutputStream(FILE_NAME));

		out.write("<html>\n");
		out.write("<h1>Session Content : "+session.getId()+"</h1>\n");
		out.write("<h1>Session Content : "+request.isRequestedSessionIdValid()+"</h1>\n");
		out.write("<pre>\n");
		
		int cnt=0;
		int tab=0;
		for(Enumeration enumeration=session.getAttributeNames();enumeration.hasMoreElements();cnt++){
			String name=(String)enumeration.nextElement();
			Object obj=session.getAttribute(name);
			
			printObj(out,outStream,name,obj,cnt,tab);
			out.write("\n");
		}


		out.write("</pre>\n");
		out.write("</html>");

		outStream.close();
	}
	/**
     *  Lanza los objetos y comprueba la serializacion.
     *  
     * @param out PrintWriter
     * @param outStream ObjectOutputStream
     * @param name String
     * @param obj Object
     * @param cnt int
     * @param tab int 
     */
	private static void printObj(PrintWriter out,ObjectOutputStream outStream,String name,Object obj,int cnt,int tab){
		Class clazz=obj.getClass();
		printTab(out,tab);
		out.write(Integer.toString(cnt));
		out.write(") Object Name:'"+name+"'");
		out.write("<br>");
		
		printTab(out,tab);
		out.write("Class:'"+(clazz.getName())+"'");

		if (isSerializable(outStream,obj)){
			out.write(" serializable <br>");
		} else {
			out.write(" <b>SERIALIZATION FAILED!!!</b> <br>");
		}
		
		Field fields[]=clazz.getDeclaredFields();
		tab++;
		for (int i=0;i<fields.length;i++){
			Field field=fields[i];
			String fieldName=field.getName();
			Class returnType=field.getType();
			
			printTab(out,tab);
			out.write(Integer.toString(i));
			out.write(") "+(returnType.getName())+" "+fieldName );

			out.write("<br>");
			printField(out,outStream,field,obj,tab);
		}
		
//		printTab(out,tab);
//		out.write("Value:'"+(obj)+"'");
		
		
		out.write("<br>");
	}
	/**
	 * Lanza los campos y al tipo primitivo que corresponde.
	 *  
	 * @param out PrintWriter
	 * @param outStream ObjectOutputStream
	 * @param field Field
	 * @param obj Object
	 * @param tab int
	 */
	static void printField(PrintWriter out,ObjectOutputStream outStream,Field field,Object obj,int tab){
		Class returnType=field.getType();
		
		if (returnType.isPrimitive()){
			return;
		} else if (returnType.isArray()){
			return;
		} else if (Void.TYPE.equals(returnType)){
			return;
		} else if (Class.class.equals(returnType)){
			return;
		} else if (String.class.equals(returnType)){
			return;
//		} else if (Object.class.equals(returnType)){
//			return;
		}
		try {
			Object ret=field.get(obj);
			if (ret!=null){
				printObj(out,outStream,"",ret,0,tab);
			}
		} catch (Exception e) {
			printTab(out,tab);
//			out.write("Exception:"+e.getMessage()+"<br>\n");
			out.write("Private <br>\n");
		}
		


		
	}
	/**
	 * Comprueba si son serializables los objetos.
	 * 
	 * @param out ObjectOutputStream
	 * @param obj Object
	 * @return boolean
	 */
	private static boolean isSerializable(ObjectOutputStream out,Object obj){
		try {
			out.writeObject(obj);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 *  Lanza una Tabla.
	 *   
	 * @param out PrintWriter
	 * @param tab int
	 */
	private static void printTab(PrintWriter out,int tab){
		for (int i=0;i<tab;i++){
			out.write("\t");
		}
	}

}
