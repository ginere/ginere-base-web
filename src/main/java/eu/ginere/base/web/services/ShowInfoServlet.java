//package eu.ginere.base.web.services;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.List;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import eu.ginere.base.web.connectors.rights.RightInterface;
//import eu.ginere.base.web.listener.AbstractWebContextListener;
//import eu.ginere.base.web.listener.ContextInitializedException;
//import eu.ginere.base.web.servlet.JSONServlet;
//import eu.ginere.base.web.servlet.MainServlet;
//import eu.ginere.base.web.servlet.info.ServletArgs;
//
//public class ShowInfoServlet extends MainServlet{
//
//	private static final String URI = "/servlet/util/ShowInfoServlet";
//	private static final String DESCRIPTION = "Returns the information of all the servlet loaded into the context. The servlet are loaded the when the first call is done";
//
//	@Override
//	protected ServletArgs[] getArgs() {
//		return new ServletArgs[]{
//				ServletArgs.getStringParameter("The servlet ID to clear or to print exception","id"),
//				ServletArgs.getStringParameter("The action to do: exception,clear","action"),
//		};	
//	}
//	
//	@Override
//	protected void doService(HttpServletRequest request,
//			HttpServletResponse response) throws ServletException, IOException {
//		String id=getStringParameter(request, "id",null);
//		String accion=getStringParameter(request, "action",null);
//		
//		response.setCharacterEncoding(JSONServlet.CHARSET);
//		response.setContentType("text/html");
//		
//		PrintWriter writer=response.getWriter();
//		
//		
//		if ("exception".equals(accion)){
//			writer.println("<html><body>");
//			MainServlet servlet=AbstractWebContextListener.getServlet(id);
//			Exception e=servlet.getInfo().getLastException();
//			if (e!=null){
//				printException(writer,e);
//			} else {
//				writer.println("No hay excepcion");
//			}
//			writer.println("</body></html>");
//			return ;
//		} else if ("clear".equals(accion)){
//			MainServlet servlet=AbstractWebContextListener.getServlet(id);
//			servlet.getInfo().clear();
//			return;
//		} else {
//			writer.println("<html><body>");
//			
//			if (id==null){
//				List<MainServlet> list=AbstractWebContextListener.getServletList();
//				for (MainServlet servlet:list){
//					printServlet(request,writer,servlet);
//					writer.println("</p>");
//				}
//			} else {
//				MainServlet servlet=AbstractWebContextListener.getServlet(id);
//				if (servlet==null){
//					writer.println("<b>Not Found:</p>");	
//					writer.println(id);	
//					writer.println("</b></p>");	
//					
//				} else {
//					printServlet(request,writer,servlet);
//					writer.println("</p>");
//				}
//			}
//			writer.println("</body></html>");
//			
//		}
//		
//	}
//
//	private static void printServlet(HttpServletRequest request,PrintWriter writer,MainServlet servlet){
//		ServletInfo info=servlet.getInfo();
//		ServletArgs args[]=servlet.getServletArgs();
//
//		writer.println("<table width='90%' border=\"1\" align='center' >");
//
//		writer.print("<tr><td colspan=\"2\"><b>");
//		writer.print(servlet.getServletUri());
//		writer.print("</b> [");
//		
//		writer.print(info.getServletClass().getName());
//		writer.print("]</p>");
//		writer.print(servlet.getServletDescription());
//		writer.print("</p>");
//		
//		
//		writer.print("<b>Arguments:</b></p>");
//		// Ther argumens
//		if (args==null){
//			writer.print(" - no Args</p>");
//		} else {
//			for (ServletArgs arg:args){
//				writer.print(arg);
//				writer.print("</p>");		
//			}
//		}
//		
//		writer.print("<b>Servlet Return:</b></p>");
//		String servletReturn=servlet.getServletReturn();
//		// Ther argumens
//		if (servletReturn==null){
//			writer.print(" - No Def</p>");
//		} else {
//			writer.print("<pre>");
//			writer.print(servletReturn);
//			writer.print("</pre></p>");
//		}
//		
//		printRights(request,writer,servlet);
//		
//		writer.print("</td></tr>");
//
//
//		printInfo(request,writer,servlet);
//
//		
//		writer.println("</table>");
//		
//	}
//
//	private static void printRights(HttpServletRequest request,PrintWriter writer,MainServlet servlet){
//		RightInterface[] rights=servlet.get();
//		writer.print("<b>Access:</b></p>");
//		
//		if (rights==null){
//			writer.print(" - Public Access</p>");
//		} else if ( rights.length == 0){
//			writer.print(" - For users logged only</p>");
//		} else {
//			for (RightInterface right:rights){
//				writer.print(" - <b>");
//				writer.print(right.getId());
//				writer.print("</b> - ");	
//				writer.print(right.getName());
//				writer.print(" - ");	
//				writer.print(right.getDescription());
//							
//			}
//			writer.println("</tr>");				
//		}
//
//	}
//	private static void printInfo(HttpServletRequest request,PrintWriter writer,MainServlet servlet){
//		ServletInfo info=servlet.getInfo();
//		
//		writer.println("<tr><td align='right'>");
//		writer.println("RunningCall:");
//		writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//		writer.println(info.getRunningCall());
//		writer.println("</td></tr>");
//
//		writer.println("<tr><td align='right'>");
//		writer.println("AverageTime:");
//		writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//		writer.println(info.getAverageTime());
//		writer.println("</td></tr>");
//
//		writer.println("<tr><td align='right'>");
//		writer.println("ExceptionNumber:");
//		writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//		writer.println(info.getExceptionNumber());
//		writer.println("</td></tr>");
//		
//		writer.println("<tr><td align='right'>");
//		writer.println("Call Number:");
//		writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//		writer.println(info.getTotalNumber());
//		writer.println("</td></tr>");
//		
//		writer.println("<tr><td align='right'>");
//		writer.println("Max Call Time:");
//		writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//		writer.println(info.getMaxTime());
//		writer.println("</td></tr>");
//		
//		writer.println("<tr><td align='right'>");
//		writer.println("Total Consumed Time:");
//		writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//		writer.println(info.getTotalTime());
//		writer.println("</td></tr>");
//		
//		if (info.getLastException()==null){
//			writer.println("<tr><td align='right'>");
//			writer.println(" No Exceptions ");
//			writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//			writer.println("</td></tr>");
//		} else {
//			writer.println("<tr><td align='right'>");
//			writer.println(" Exception ");
//			writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//			
//			writer.print("<a href=\"");
//			writer.print(request.getContextPath());
//			writer.print("/servlet/util/ShowInfoServlet?id=");
//			writer.print(info.getServletClass().getName());
//			writer.print("&action=exception\">");
//			writer.print(info.getLastException().getMessage());
//			writer.print("</a>");
//			writer.println("</td></tr>");
//		}
//
//
//		writer.println("<tr><td align='right'>");
//		writer.println("Clear");
//		writer.println("&nbsp;</td><td width='90%'>&nbsp;");
//		
//		writer.print("<a href=\"");
//		writer.print(request.getContextPath());
//		writer.print("/servlet/util/ShowInfoServlet?id=");
//		writer.print(info.getServletClass().getName());
//		writer.print("&action=clear\">clear</a>");
//
//		writer.println("</td></tr>");
//
//
//	}
//
//	private static void printException(PrintWriter writer,Throwable e){
//		
//		writer.println("Message:");
//		writer.println(e.getMessage());
//		writer.println("</p>");
//
//		writer.println("Trace:</p>");
//		writer.println("<pre>");
//		e.printStackTrace(writer);
//		writer.println("</pre></p>");
//		
//		Throwable cause=e.getCause();
//		if (cause!=null){
//			writer.println("Causa:");
//			printException(writer,cause);
//			writer.println("</p>");
//		}
//	}
//	
//	@Override
//	protected RightInterface[] getRights() throws ContextInitializedException {
////		return new RightInterface[]{AbstractWebContextListener.ADMIN_TECH_RIGHT};
//		return AbstractWebContextListener.SUPER_ADMIN_TECH;
//	}
//	
//	@Override
//	protected String getUri() {
//		return URI;
//	}
//
//	@Override
//	protected String getDescription() {
//		return DESCRIPTION;
//	}
//
//}
