package eu.ginere.base.web.services;

import java.beans.IntrospectionException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.web.connectors.i18n.I18NConnector;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.json.DescriptionExclusionStrategy;
import eu.ginere.base.web.json.JSONSerializarDescriptor;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.JSONServlet;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.info.ServletArgs;

@SuppressWarnings("serial")
public class GetLanguageList extends JSONServlet{

	public static final String URI = "/servlet/util/GetLanguageList";
	public static final String DESCRIPTION = "Returns a JSON list of languages";

	@Override
	protected ServletArgs[] getArgs() {
		return ServletArgs.NO_ARGS;
	}
	
	@Override
	protected String configureGsonBuilder(GsonBuilder gsonBuilder) throws IntrospectionException {
//		gsonBuilder.setPrettyPrinting();
		JSONSerializarDescriptor descriptor=new DescriptionExclusionStrategy(Language.class);
		gsonBuilder.setExclusionStrategies(descriptor);
		
		return descriptor.getDescription();
	}

	protected long getLastModifiedException(HttpServletRequest request) {
		return getSessionLastModified(request);
	}
	
	@Override
	protected Object doTaskJSONService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			DaoManagerException {
		Language array[]=I18NConnector.getAvailablesLanguageList();
		
		return array;
	}
	
	
	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return MainServlet.PUBLIC_ACCESS;
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
