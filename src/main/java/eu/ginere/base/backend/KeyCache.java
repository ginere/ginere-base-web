package eu.ginere.base.backend;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.dao.cache.KeyCacheManager;
import eu.ginere.base.util.dao.cache.impl.AbstractKeyCacheManager;
import eu.ginere.base.util.lang.AvemStringUtils;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.AbstractWebContextListener;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;


public class KeyCache extends MainServlet {
	private static final long serialVersionUID = 1L;
	
	public static final String URI = "/servlet/util/KeyCache";
	public static final String DESCRIPTION = "Lists the key caches of this server";
	

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}


	@Override
	protected void doService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			DaoManagerException {
		PrintWriter writer=response.getWriter();
		writer.print("<html><body><table>");
		
		writer.print("<tr><td>");
		writer.print("Name");
		writer.print("</td><td>");
		writer.print("Elements");
		writer.print("</td><td>");
		writer.print("Max Aged");
		writer.print("</td></tr>");
		
		for (int i=0;i<KeyCacheManager.MANAGER.list.size();i++){
			AbstractKeyCacheManager cache=KeyCacheManager.MANAGER.list.elementAt(i);
			
			writer.print("<tr><td>");
			writer.print(cache.getName());
			writer.print("</td><td>");
			writer.print(cache.getBackendElementNumber());
			writer.print("</td><td>");
			writer.print(cache.getMaxAged());
			writer.print("</td><td>");
			
			writer.print("</td></tr>");
		}
		
		writer.print("</table></body></html>");
		
		writer.print("<pre>");
		writer.print("KeyCacheManager.DEFAULT_UNACTIVE_TIME (sec)=");
		writer.println(AvemStringUtils.fromLapInMillis(KeyCacheManager.DEFAULT_UNACTIVE_TIME));
		writer.print("KeyCacheManager.DEFAULT_TIME_TO_SLEEP (sec)=");
		writer.println(AvemStringUtils.fromLapInMillis(KeyCacheManager.DEFAULT_TIME_TO_SLEEP));
		writer.print("</pre>");
		
		writer.close();
		
	}
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return new RightInterface[]{AbstractWebContextListener.ADMIN_TECH_RIGHT};
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
