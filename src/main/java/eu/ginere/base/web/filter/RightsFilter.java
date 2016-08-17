package eu.ginere.base.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import eu.ginere.base.util.properties.GlobalFileProperties;
import eu.ginere.base.web.servlet.MainServlet;

/**
 * @author ventura
 *
 * This fiultr can be included into the web.xml to set rights to pages into de GlobalPropertiesFile
 *
 */
public class RightsFilter implements Filter {
	static final Logger log = Logger.getLogger(RightsFilter.class);
	
	private static final String RIGHT_PROPERTY_PREFIX="RIGHT.";
	
	public void destroy() {
		// Do nothing
	}

	public void doFilter(ServletRequest req, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request=(HttpServletRequest)req;
		
		String uri=request.getRequestURI();
		
		log.debug(request.getRequestURI());
		log.debug(request.getRequestURL());
		
		String permisions[]=GlobalFileProperties.getPropertyList(RightsFilter.class, RIGHT_PROPERTY_PREFIX+uri,"Right properties to use in the filter of the url, prefix:"+RIGHT_PROPERTY_PREFIX);
		String userId=MainServlet.getUserId(request);
		boolean hasPermision=false;

		hasPermision=MainServlet.hasRights(userId,uri,permisions);
		
		if (hasPermision){
			filterChain.doFilter(req, response);
		} else {		
			if (userId == null) {
				((HttpServletResponse)response).sendError(MainServlet.HTTP_CODE_FORBIDDEN,
						"You must be logged to acces page");
			} else {
				((HttpServletResponse)response).sendError(MainServlet.HTTP_CODE_FORBIDDEN,
						"The user:'" + userId
								+ "' do not has rights to acces page");
			}
//			MainServlet.redirectNoUserRight(request, (HttpServletResponse)response, userId);
		}
	}
	
	public void init(FilterConfig arg0) throws ServletException {
		log.info("Inicializando el filtro de permisos");
	}

}
