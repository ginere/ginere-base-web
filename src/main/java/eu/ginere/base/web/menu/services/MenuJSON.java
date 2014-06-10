package eu.ginere.base.web.menu.services;

import java.beans.IntrospectionException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.json.DescriptionExclusionStrategy;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.menu.MenuItem;
import eu.ginere.base.web.menu.MenuManager;
import eu.ginere.base.web.servlet.JSONServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;


public class MenuJSON extends JSONServlet {

	private static final String URI = "NoDEf";
	private static final String DESCRIPTION = "Returns the Menu associated to this Id. The menu has to be configurated into the configuration file";

	@Override
	protected ServletArgs[] getArgs() {
		return new ServletArgs[]{
				ServletArgs.getMandatoryStringParameter("The menu Id","id"),
		};	
	}
	
	@Override
	protected String configureGsonBuilder(GsonBuilder gsonBuilder) throws IntrospectionException {
		// Registrando las enumeraciones Pero no funciona por que no se pueden registrar interfaces.
		gsonBuilder.registerTypeAdapter(MenuItem.class, new I18NMenuSerializer());
		
		return new DescriptionExclusionStrategy(MenuItem.class).getDescription();
	}

	@Override
	protected Object doTaskJSONService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String menuId=getMandatoryStringParameter(request, "id");
		
		return MenuManager.getMenuItem(menuId, getUserId(request))	;
	}

	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return PUBLIC_ACCESS;
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
