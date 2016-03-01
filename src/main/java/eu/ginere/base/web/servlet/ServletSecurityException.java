package eu.ginere.base.web.servlet;

import javax.servlet.http.HttpServletRequest;

public class ServletSecurityException extends Exception {

	private static final long serialVersionUID = 1L;

	public static ServletSecurityException create(int code,
			String message, 
			HttpServletRequest request,
			MainServlet servlet) {
		StringBuilder buffer=new StringBuilder();

		buffer.append("Http Code:");
		buffer.append(code);
		buffer.append(" Message:");
		buffer.append(message);
		buffer.append(" RemoteAddress:");
		buffer.append(request.getRemoteAddr());
		buffer.append(" Referer:");
		buffer.append(request.getHeader("referer"));
		buffer.append(" Servlet Class:");
		buffer.append(servlet.getClass());
		buffer.append(" Servlet Uri:");
		buffer.append(MainServlet.getURI(request));
		
		buffer.append(" Remote user Id:");
		buffer.append(request.getRemoteUser());
		buffer.append(" Session user Id:");
		buffer.append(MainServlet.getSessionUserId(request));
		
		return new ServletSecurityException(buffer.toString());
	}
	public static ServletSecurityException create(String message, 
									HttpServletRequest request,
									MainServlet servlet) {
		StringBuilder buffer=new StringBuilder();

		buffer.append("Message:");
		buffer.append(message);
		buffer.append(" RemoteAddress:");
		buffer.append(request.getRemoteAddr());
		buffer.append(" Referer:");
		buffer.append(request.getHeader("referer"));
		buffer.append(" Servlet Class:");
		buffer.append(servlet.getClass());
		buffer.append(" Servlet Uri:");
		buffer.append(MainServlet.getURI(request));
		buffer.append(" Remote user Id:");
		buffer.append(request.getRemoteUser());
		buffer.append(" Session user Id:");
		buffer.append(MainServlet.getSessionUserId(request));
		
		return new ServletSecurityException(buffer.toString());
	}
	

	private ServletSecurityException(String message, Throwable cause){
		super(message,cause);
	}
	
	private ServletSecurityException(String message){
		super(message);
	}

}
