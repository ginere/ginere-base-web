package eu.ginere.base.web.services;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
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
 */
public class ServletSessionTestLite extends HttpServlet {

	/**
	 * Serial UID.
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
		out.write("<h1>Session Content</h1>\n");
		out.write("<pre>\n");
		
		int cnt=0;
		int tab=0;
		for(Enumeration enumeration=session.getAttributeNames();enumeration.hasMoreElements();cnt++){
			String name=(String)enumeration.nextElement();
			Object obj=session.getAttribute(name);
			
			printObj(out,outStream,name,obj,cnt,tab);
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
		

		if (!isSerializable(outStream,obj)){
			out.write(Integer.toString(cnt));
			out.write(") Object Name:'"+name+"'");
			out.write("<br>");
			
			out.write("Class:'"+(clazz.getName())+"'");
			out.write(" <b>SERIALIZATION FAILED!!!</b> <br>");
			out.write("\n");
		}

		out.write("<br>");
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
	
}
