package eu.ginere.base.web.services;


import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.listener.AbstractWebContextListener;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.JSONServlet;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;
import eu.ginere.base.web.servlet.info.ServletInfo;


public class ShowInfoJSONServlet extends JSONServlet{

	private static final long serialVersionUID = 1L;
	
	private static final String URI = "/servlet/util/ShowInfoServlet";
	private static final String DESCRIPTION = "Returns the information of all the servlet loaded into the context. The servlet are loaded the when the first call is done";

	@Override
	protected String configureGsonBuilder(GsonBuilder gsonBuilder) throws IntrospectionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object doTaskJSONService(HttpServletRequest request,
									   HttpServletResponse response) throws ServletException, IOException,DaoManagerException {

		String id=getStringParameter(request, "id",null);
		String accion=getStringParameter(request, "action",null);

		if ("exception".equals(accion)){
			MainServlet servlet=AbstractWebContextListener.getServlet(id);
			Exception e=servlet.getInfo().getLastException();
			return e;
		} else if ("clear".equals(accion)){
			MainServlet servlet=AbstractWebContextListener.getServlet(id);
			servlet.getInfo().clear();
			return null;
		} else {
			if (id==null){
				List<MainServlet> list=AbstractWebContextListener.getServletList();
				List<ServletInfo> ret=new ArrayList<ServletInfo>(list.size());
				//				List<String> ret=new ArrayList<String>(list.size());
				
				for (MainServlet servlet:list){
					if (this.getClass() != servlet.getClass()){
					
						ServletInfo info=servlet.getInfo();
						ret.add(info);
					}
				}
				return ret;
			} else {
				MainServlet servlet=AbstractWebContextListener.getServlet(id);
				if (servlet==null){
					return id;
				} else {
					ServletInfo info=servlet.getInfo();
					return info;
				}
			}
		}
		
	}
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return AbstractWebContextListener.SUPER_ADMIN_TECH;
	}
	
	@Override
	protected String getUri() {
		return URI;
	}

	@Override
	protected String getDescription() {
		return DESCRIPTION;
	}

	@Override
	protected ServletArgs[] getArgs() {
		return new ServletArgs[]{
				ServletArgs.getStringParameter("The servlet ID to clear or to print exception","id"),
				ServletArgs.getStringParameter("The action to do: exception,clear","action"),
		};	
	}


}
