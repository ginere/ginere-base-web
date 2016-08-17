//package eu.ginere.base.web.menu.services;
//
//import java.beans.IntrospectionException;
//import java.io.IOException;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import com.google.gson.GsonBuilder;
//
//import eu.ginere.base.util.i18n.Language;
//import eu.ginere.base.web.connectors.rights.RightInterface;
//import eu.ginere.base.web.json.DescriptionExclusionStrategy;
//import eu.ginere.base.web.listener.ContextInitializedException;
//import eu.ginere.base.web.menu.MenuItem;
//import eu.ginere.base.web.menu.MenuManager;
//import eu.ginere.base.web.servlet.JSONServlet;
//import eu.ginere.base.web.servlet.info.ServletArgs;
//
//
//public class I18NMenuJSON extends JSONServlet {
//
//	private static final long serialVersionUID = 1L;
//
//	private static final String URI = "NoDEf";
//	private static final String DESCRIPTION = "Returns the I18N Menu associated to this Id. The menu has to be configurated into the configuration file";
//
//	@Override
//	public ServletArgs[] getArgs() {
//		return new ServletArgs[]{
//				ServletArgs.getMandatoryStringParameter("The menu Id","id"),
//		};	
//	}
//	
//	
//	@Override
//	protected String configureGsonBuilder(GsonBuilder gsonBuilder) throws IntrospectionException {
//		// Registrando las enumeraciones Pero no funciona por que no se pueden registrar interfaces.
//		gsonBuilder.registerTypeAdapter(MenuItem.class, new I18NMenuSerializer());
//		
//		return new DescriptionExclusionStrategy(MenuItem.class).getDescription();
//	}
//
//	@Override
//	protected Object doTaskJSONService(HttpServletRequest request,
//			HttpServletResponse response) throws ServletException, IOException {
//		String menuId=getMandatoryStringParameter(request, "id");
//		
//		Language langId=getLanguage();
//
//		return MenuManager.getI18nMenuItem(menuId, getUserId(request),langId)	;
//	}
//
//	@Override
//	protected RightInterface[] getRights() throws ContextInitializedException {
//		return PUBLIC_ACCESS;
//	}
//
//	@Override
//	public String getUri() {
//		return URI;
//	}
//
//	@Override
//	public String getDescription() {
//		return DESCRIPTION;
//	}
//
//}