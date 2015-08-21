package eu.ginere.base.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.enumeration.SQLEnum;
import eu.ginere.base.util.file.FileUtils;
import eu.ginere.base.util.i18n.Language;
//import eu.ginere.base.util.image.ImageSize;
import eu.ginere.base.util.notification.Notify;
import eu.ginere.base.util.properties.GlobalFileProperties;
import eu.ginere.base.web.connectors.i18n.I18NConnector;
import eu.ginere.base.web.connectors.rights.RightConnector;
import eu.ginere.base.web.connectors.rights.RightImpl;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.connectors.users.UsersConnector;
import eu.ginere.base.web.listener.AbstractWebContextListener;
import eu.ginere.base.web.listener.ContextInitializedException;
import eu.ginere.base.web.servlet.info.ServletArgs;
import eu.ginere.base.web.servlet.info.ServletInfo;
import eu.ginere.base.web.session.SessionAccesor;
import eu.ginere.base.web.session.SessionManager;

/**
 * @author ventura
 *
 * Garantiza que siempre habra un una lengua en el thread que trata la peticion
 *
 */
@SuppressWarnings("serial")
public abstract class MainServlet extends HttpServlet {

	public static final Logger log = Logger.getLogger(MainServlet.class);

//	private static final String LANG_ID_SESSION = "Sesion.LANG_ID";
//	private static final String LANG_SESSION = "Sesion.LANG";

	// Utilzar esto como derechos de acceso publico
	protected static final RightInterface[] PUBLIC_ACCESS = null;
	protected static final RightInterface[] USER_LOGGED = new RightInterface[] {};

	private static final String LOCAL_HOST="127.0.0.1";
	
	public static final RightInterface LOCAL_ACCESS_RIGHT=new RightImpl("LOCAL_ACCESS_RIGHT","Local acess allowed","Locas access allowed, by exemple launching preccess","WEB_COMMON");

	public static final String NOT_REMOTE_ADDR_FOUND="NO_REMOTE_ADDR";
	public static final String NOT_USER_AGENT_FOUND = "NO_USER_AGENT";

	private static final String NOT_UUID_FOUND = "NO_UUID_FOUND";
	
	/*
	 * 10.4.4 403 Forbidden The server understood the request, but is refusing
	 * to fulfill it. Authorization will not help and the request SHOULD NOT be
	 * repeated. If the request method was not HEAD and the server wishes to
	 * make public why the request has not been fulfilled, it SHOULD describe
	 * the reason for the refusal in the entity. If the server does not wish to
	 * make this information available to the client, the status code 404 (Not
	 * Found) can be used instead.
	 */
	public static final int HTTP_CODE_REQUEST_TIMEOUT = 408;
	public static final int HTTP_SERVICE_UNAVAILABLE=503;
	
	public static final int HTTP_BAD_REQUEST=400;
	public static final int HTTP_CODE_FORBIDDEN = 403;
	public static final int HTTP_NOT_FOUND=404;
	
	/**
	 * Funcion que debe implementarse en los servlets para hacer el trabajo
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	abstract protected void doService(HttpServletRequest request,
									  HttpServletResponse response) throws ServletException, IOException,DaoManagerException;

	/**
	 * Use this method to set the header that had to be sended back. Use this for cross site.
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void setDefaultHeaders(HttpServletRequest request,
			  						 HttpServletResponse response) throws ServletException, IOException{
		
	}
	/**
	 * Esta funcion define los derechos de acceso al servlet
	 * @return
	 * @throws ContextInitializedException
	 */
	abstract protected RightInterface[] getRights()throws ContextInitializedException;

	public void destroy() {
		super.destroy();
	}

	public ServletConfig getServletConfig() {
		return super.getServletConfig();
	}

	public String getServletInfo() {
		return super.getServletInfo();
	}

	public ServletInfo getInfo() {
		return servletInfo;
	}
	
	/**
	 * This apply additional security to this Servlet.
	 * Like creating a new user or loggin. If one service modifies de data basse must
	 * enable the spetial security only if it is in public access.
	 * 
	 * @return
	 */
	protected boolean enableSpetialSecurity() {
		return false;
	}

	/**
	 * Los derechos de acceso al servlet
	 */
	private RightInterface[] SERVLET_RIGHTS=null;
	private final ServletInfo servletInfo=new ServletInfo(getClass());
	protected String servletReturn=null;

//	private HashSet<String> privilegedRemoteClients;
	
	
	// Inits the servlet security stuff
    // TODO That have to be done in the Initialization stuff
    //	static {
    //		ServletSecurity.init();
    //	}
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			AbstractWebContextListener.addServlet(this);
			
			servletInfo.clear();
			try {
				SERVLET_RIGHTS=getRights();				
			}catch (ContextInitializedException e) {
				Notify.fatal(log,"Error inicializando los permisos",e);
			}
			
//			// reads the privileged remote clients
//			privilegedRemoteClients=GlobalFileProperties.getPropertyMap(getClass(), "PrivilegedRemoteClients");

//			servletInfo.setUri(getUri());
//			servletInfo.setDescription(getDescription());
//			servletInfo.setArgs(getArgs());
			
		} catch (Exception e) {
			throw new ServletException("Iniciando el servlet", e);
		}
	}
	
	protected abstract String getUri();
	protected abstract String getDescription();
	protected abstract ServletArgs[] getArgs(); 


	public String getServletUri(){
		return getUri();
	}
	public String getServletDescription(){
		return getDescription();
	}
	public ServletArgs[] getServletArgs(){
		return getArgs();
	}
	public String getServletReturn(){
		return servletReturn;
	}
	public RightInterface[] getServletRights(){
		return SERVLET_RIGHTS;
	}

	protected static final String METHOD_GET = "GET";
	protected static final String METHOD_OPTIONS = "OPTIONS";
	protected static final String HEADER_LASTMOD = "Last-Modified";
	protected static final String HEADER_IFMODSINCE = "If-Modified-Since";
	
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	protected static final String REDIRECT_URL = "redir";


	 /*
	 * Sets the Last-Modified entity header field, if it has not already been
	 * set and if the value is meaningful. Called before doGet, to ensure that
	 * headers are set before response data is written. A subclass might have
	 * set this header already, so we check.
	 */
	private void maybeSetLastModified(HttpServletResponse resp/*,
			long lastModified*/) {
//		if (resp.containsHeader(HEADER_LASTMOD)){
//			return;
//		}
//		if (lastModified >= 0){
//			if (log.isDebugEnabled()){
//				SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss SSS");
//				log.debug("Setting last Modified:'"+sdf.format(new Date(lastModified))+"' last Modified:"+lastModified);
//			}
//			resp.setDateHeader(HEADER_LASTMOD, lastModified);
//		}
	
		
//		El problema viene de que esta cabecera solo acepta una resolucion de hasta un segundo.
//		Si un dato es regenerado en el mismo segundo varias veces no sabemos si se ha enviado.
//		En este header enviamos la fecha a la que el cliente hizo la paticion por lo que si conincide con el
//		last modified del los datos del servidor no sabemos si tiene la version correcta puesto que puede haber sido actualizado
//		en el mismos segun, por lo que en ese caso volvemos a enviar la informacion.
//		solo le devolvemos un 304 si la fecha del if-modify-since es mallor que la del last modified del servidor
		
		
		
		
		if (resp.containsHeader(HEADER_LASTMOD)){
			return;
		}
		long lastModified = System.currentTimeMillis();
		
		if (log.isDebugEnabled()){
			SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss SSS");
			log.debug("Setting last Modified:'"+sdf.format(new Date(lastModified))+"' last Modified:"+lastModified);
		}
		resp.setDateHeader(HEADER_LASTMOD, lastModified);
	}


	public static long getSystemLastModified(){
		return AbstractWebContextListener.getSystemLastModified();
	}

	public static long getSessionLastModified(HttpServletRequest request){
		HttpSession session = request.getSession();
		return getSessionLastModified(session);
	}

	public static long getSessionLastModified(HttpSession session){
		return SessionAccesor.getLastUpdated(session);
	}
	
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#getLastModified(javax.servlet.http.HttpServletRequest)
     */
    protected final long getLastModified(HttpServletRequest request) {
//    	try {
//			long lastModified=getObjectlastModified(request);
//			
//			// Si el lenguage de la session ha cambiao 
//			// enviamos el last modified de la sesion y no el del objeto.
//			if (lastModified>0){
//				long sessionLastModified=getSessionLastModified(request);
//				if (lastModified<sessionLastModified){
//					return sessionLastModified;
//				} else {
//					return lastModified;
//				}
//			} else {
//				return -1;
//			}
//		} catch (Exception e) {
//			if (log.isDebugEnabled()){
//				log.debug("",e);
//			}
//	    	return -1;
//		}
    	throw new IllegalAccessError("Not not use this method, use getLastModifiedException()");
    }
    
    protected long getLastModifiedException(HttpServletRequest request) throws ServletException , DaoManagerException{
		long lastModified=getObjectlastModified(request);
		
		// Si el lenguage de la session ha cambiao 
		// enviamos el last modified de la sesion y no el del objeto.
		if (lastModified>0){
			long sessionLastModified=getSessionLastModified(request);
			if (lastModified<sessionLastModified){
				return sessionLastModified;
			} else {
				return lastModified;
			}
		} else {
			return -1;
		}
    }
	  
    protected long getObjectlastModified(HttpServletRequest req) throws ServletException , DaoManagerException{
    	return -1;
    }
    
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// trazing time servlets
		// start call
		long time=System.currentTimeMillis();
		servletInfo.startCall();
		String uri=getURI(request);
		String method = request.getMethod();
		
		try {
			// First session
			SessionManager.MANAGER.initSession(request,response);

			// Setting return headers before call
			setDefaultHeaders(request,response);

			if (method.equals(METHOD_OPTIONS)){

				doOptions(request, response);

				if (log.isInfoEnabled()){
					log.info("OPTIONS: ha tratado el servlet :'" + getURI(request)+
							 "' en:"+(System.currentTimeMillis()-time)+
							 " milisgundos.");
				}

				// THE OPTIONS QUERY STOP HERE
				return ;
			}
			
			// SECURITY
			// IF it is the first cookie redirect to captha
            if (ServletSecurity.isInitialized()){
				long securityTestCheckTime=System.currentTimeMillis();
				try {
					
					// si el usuario no esta conectado es entonces cuando verificamos que existe session
					// y que hay un referer.
					// Por que quizas la session haya expirado pero aun tenemos un cookie activa
					// que nos valida la peticion y no podemos cortar al usuario si la cookie esta activa 
					// y lo primero que hace es un comet.
		//				if (!isUserConnected(request)){
		//					
		////					TODO en el contans la primera vez que un usuario se conecta no tiene Session
		////					Los constants tienen que ser publicos , que estos generen una JSESSIONID y asi todo iria bien
		////					
		////					// todos las llamadas a servlet tienen que tener una session
		////					ServletSecurity.isFirstSessionCall(request, this);
		//					
		////					TODO las versiones enbarcadas no tienen referer?
		////					// No se pueden llamar a los servlets desde sitios externos
		////					ServletSecurity.testReferer(request, this);
		//				}
					// Si hay demasidos errores en una misma session o desde un remote host,
					// directamente paramos las llamadas
					ServletSecurity.testToManyErrors(request, this);
					
					// Para servlet speciales
					if (enableSpetialSecurity()){
						// Aqui no aceptamos robots
						ServletSecurity.noRobots(request, this);
						
						// no se pueden hacer demasiadas llamadas 
						// TODO por el moment osolo lo ralentizamos
						ServletSecurity.testToManySpetialCalls(request, this);
						// aqui habria que banera los clientes y las sessiones ...
						
						servletInfo.startSpetialCall(request,uri);
					}
				}catch(ServletSecurityException e){
					servletInfo.addSecurityError(request,e);
		//				ServletSecurity.redirectToCapcha(request, response, this);
					response.sendError(HTTP_CODE_FORBIDDEN);
					Notify.warn(log,"Security exception for servlet:'"+uri+"'",e);
					return;
				}finally{
					if (log.isDebugEnabled()){
						log.debug("Security Checks done in:"+(System.currentTimeMillis()-securityTestCheckTime));
					}
				}
            } else {
            	if (log.isDebugEnabled()){
					log.debug("Servlet Security not initialized...");
				}
            }
				
			// Then request stuff
			String langId=getStringParameter(request,"LANG_ID",null);
			if (langId!=null){
				setLangId(request,langId); 
			} else {
				initializeLanguage(request);
				//				setThreadLocallanguage(request);
			}
			
			
	
		
			// Permisions and user stuff
			String userId = getUserId(request);
			
			boolean hasPermision = false;
			hasPermision=hasRights(request,userId,uri,SERVLET_RIGHTS);
			
			if (hasPermision) {
				try {
					// TRY THE LAST UPDATE FOR THE GET METHOD
					if (method.equals(METHOD_GET)) {
						long lastModified = getLastModifiedException(request);
							if (lastModified >0 ) {
								long ifModifiedSince = request.getDateHeader(HEADER_IFMODSINCE);
								if (log.isDebugEnabled()){
									SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss SSS");
									log.debug("Getting last Modified from request header:'"+sdf.format(new Date(ifModifiedSince))+"' last Modified:"+ifModifiedSince);
								}
//							long lastModifiedRounded=(lastModified / 1000 * 1000);
//							if (ifModifiedSince <  lastModifiedRounded) {
//								// If the servlet mod time is later, call doGet()
//								// Round down to the nearest second for a proper compare
//								// A ifModifiedSince of -1 will always be less
//								maybeSetLastModified(response, lastModified);
//								// Se sigue con la query normal
//							} else if (ifModifiedSince >  (time-1000)) {
//								// If they are the same we dont change de lasta modified but que resent the content.
//								// We have a precision of one second only ...
//								maybeSetLastModified(response, lastModified);
//							} else {
//								if (log.isInfoEnabled()){
//									SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss SSS");
//									log.info("AVOIDING!! query last modified:'"+sdf.format(new Date(ifModifiedSince))+" "+ifModifiedSince+
//											" ' last Modified:"+sdf.format(new Date( (lastModified / 1000 * 1000)))+" "+ (lastModified / 1000 * 1000)+
//											" ' Real last Modified:"+sdf.format(new Date(lastModified))+" "+lastModified+
//											"  time:"+(System.currentTimeMillis()-time));
//								}
//								response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
//								return ;
//							}
							
							
							
//							El problema viene de que esta cabecera solo acepta una resolucion de hasta un segundo.
//							Si un dato es regenerado en el mismo segundo varias veces no sabemos si se ha enviado.
//							En este header enviamos la fecha a la que el cliente hizo la paticion por lo que si conincide con el
//							last modified del los datos del servidor no sabemos si tiene la version correcta puesto que puede haber sido actualizado
//							en el mismo segundo, por lo que en ese caso volvemos a enviar la informacion.
//							solo le devolvemos un 304 si la fecha del if-modify-since es mayor que la del last modified del servidor con un margen de un segundo
							
							if (ifModifiedSince <=  (lastModified+1000)) {
								// If the servlet mod time is later, call doGet()
								// Round down to the nearest second for a proper compare
								// A ifModifiedSince of -1 will always be less
								maybeSetLastModified(response);
								// Se sigue con la query normal
							} else {
								if (log.isInfoEnabled()){
									SimpleDateFormat sdf=new SimpleDateFormat("yy-MM-dd HH:mm:ss SSS");
									log.info("AVOIDING!! query last modified:'"+sdf.format(new Date(ifModifiedSince))+" "+ifModifiedSince+
											" ' last Modified:"+sdf.format(new Date( (lastModified / 1000 * 1000)))+" "+ (lastModified / 1000 * 1000)+
											" ' Real last Modified:"+sdf.format(new Date(lastModified))+" "+lastModified+
											"  time:"+(System.currentTimeMillis()-time));
								}
								response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
								return ;
							}
						}
					}
					// END LAST UPDATE
					
					if (method.equals(METHOD_OPTIONS)) {
						doOptions(request, response);
					} else {
						if (this instanceof Jsp) {
							Jsp jsp = (Jsp) this;
							
							jsp._jspService(request, response);
						} else {					
							doService(request, response);
						}						
					}
				}catch (ServletException e){
					servletInfo.addException(request,e);
					if (log.isDebugEnabled()){
						log.debug("Main Servlet:",e);
					}
					throw e;
				}catch (IOException e){
					servletInfo.addException(request,e);
					if (log.isDebugEnabled()){
						log.debug("Main Servlet:",e);
					}
					throw e;
				}catch (Exception e){
					servletInfo.addException(request,e);
					Notify.error(log,"Main Servlet:",e);
					throw new ServletException("Main Servlet:",e);
				}
				
				if (log.isInfoEnabled()){
					log.info("El usuario:'"+userId+
							 "' ha tratado el servlet :'" + getURI(request)+
							 "' en:"+(System.currentTimeMillis()-time)+
							 " milisgundos.");
				}
			} else {
				String message="El usuario:'"+userId+
				 "' no tiene ninguno de los permisos:'"+StringUtils.join(SERVLET_RIGHTS, ',')+
				 "' necesarios para la pagina:" + getURI(request)+"'";
				
				if (log.isInfoEnabled()){
//					String permisosList=StringUtils.join(SERVLET_RIGHTS, ',');
					log.info(message);
				}
				servletInfo.addException(request,  ServletSecurityException.create(message, request, this));
				redirectNoUserRight(request, response, userId);
			}
		}finally{
			servletInfo.addLaps(System.currentTimeMillis()-time);
		}
	}

	static public boolean hasRights(String userId, String uri,String permisions[]) {
		if (permisions != null && permisions.length > 0) {
			// hay permisos definidos
			if (userId == null) { // si no hay usuario no se entra
				if (log.isInfoEnabled()){
					log.info("No hay usuario conectado, se deniega el acceso a la pagina:'"+uri+"'");
				}
				return false;
			} else {
				for (int i = 0; i < permisions.length; i++) {
					// si el usuario tiene alguno de los permisos, entra
					if (RightConnector.hasRight(userId, permisions[i])) {
						if (log.isInfoEnabled()){
							log.info("El usuario:'" + userId
									 + "' tiene el permiso':" + permisions[i]
									 + "' para la pagina:'" + uri + "'");
						}
						return true;
					}
				}
				if (log.isInfoEnabled()) {
					log.info("El El usuario:'" + userId
							+ "' no tiene ninguno de los permisos:'"
							+ StringUtils.join(permisions)
							+ "' para la pagina:'" + uri + "'");
				}
				return false;
			}
		} else {
			if (log.isInfoEnabled()) {
				log.info("La pagina:" + uri + " no tiene permisos asociados");
			}
			return true;
		}
	}

	static public boolean hasRights(HttpServletRequest request,
									String userId, 
									String uri,
									RightInterface permisions[]) {

		if (permisions == null){
			if (log.isInfoEnabled()) {
				log.info("La pagina:" + uri + " no tiene permisos asociados");
			}
			return true;
		} else if (permisions.length == 0){
			// user must be logged
			if (userId == null) { // si no hay usuario no se entra
				if (log.isInfoEnabled()){
					log.info("No hay usuario conectado, se deniega el acceso a la pagina:'"+uri+"'");
				}
				return false;
			} else {
				if (log.isInfoEnabled()){
					log.info("The user:'" + userId
							 + "' access the page for users logged only:'" + uri + "'");
				}
				return true;
			}
		} else {
			// hay permisos definidos
			if (userId == null) { // si no hay usuario no se entra
				if (log.isInfoEnabled()){
					log.info("No hay usuario conectado, se deniega el acceso a la pagina:'"+uri+"'");
				}
				return false;
			} else {
				for (int i = 0; i < permisions.length; i++) {
					
					RightInterface permision=permisions[i];
					if (LOCAL_ACCESS_RIGHT.getId().equals(permision.getId())){
						String remoteHost=getRemoteAddress(request);
						if (StringUtils.equalsIgnoreCase(LOCAL_HOST, remoteHost)){
							if (log.isInfoEnabled()){
								log.info("La pagina:" + uri + " accept localhost access");
							}
							return true;
						}
					} else if (RightConnector.hasRight(userId, permision)) {
						// si el usuario tiene alguno de los permisos, entra
						if (log.isInfoEnabled()){
							log.info("El usuario:'" + userId
									 + "' tiene el permiso':" + permision
									 + "' para la pagina:'" + uri + "'");
						}
						return true;
					} 
				}
				if (log.isInfoEnabled()) {
					log.info("El El usuario:'" + userId
							+ "' no tiene ninguno de los permisos:'"
							 + StringUtils.join(permisions)
							 + "' para la pagina:'" + uri + "'");
				}
				return false;
			}
		}
//		
//		if (permisions != null && permisions.length > 0) {
//			// hay permisos definidos
//			if (userId == null) { // si no hay usuario no se entra
//				if (log.isInfoEnabled()){
//					log.info("No hay usuario conectado, se deniega el acceso a la pagina:'"+uri+"'");
//				}
//				return false;
//			} else {
//				for (int i = 0; i < permisions.length; i++) {
//					// si el usuario tiene alguno de los permisos, entra
//					if (RightConnector.hasRight(userId, permisions[i])) {
//						if (log.isInfoEnabled()){
//							log.info("El usuario:'" + userId
//									 + "' tiene el permiso':" + permisions[i]
//									 + "' para la pagina:'" + uri + "'");
//						}
//						return true;
//					}
//				}
//				if (log.isInfoEnabled()) {
//					log.info("El El usuario:'" + userId
//							+ "' no tiene ninguno de los permisos:'"
//							+ StringUtils.join(permisions)
//							+ "' para la pagina:'" + uri + "'");
//				}
//				return false;
//			}
//		} else {
//			if (log.isInfoEnabled()) {
//				log.info("La pagina:" + uri + " no tiene permisos asociados");
//			}
//			return true;
//		}
	}

	public boolean hasOneRoleOf(HttpServletRequest request, String roleList) {
		String userId = getUserId(request);
		String uri=getURI(request);

		return hasRights(userId, uri, roleList);
	}


	/*
	 * Recibe la lista de permisos como un String con los permisos separados por el caracter
	 * ','
	 */
	static public boolean hasRights(String userId, String uri,String permisionList) {
		String array[] = StringUtils.split(permisionList, ',');
		
		return hasRights(userId, uri, array);
	}
	
	public void redirectNoUserRight(HttpServletRequest request,
										   HttpServletResponse response, 
										   String userId)
		throws ServletException, IOException {

		if (userId == null) {
			sendError(request,response,HTTP_CODE_FORBIDDEN,
					  "You must be logged to acces page");
		} else {
			sendError(request,response,HTTP_CODE_FORBIDDEN, "The user:'" + userId
					  + "' do not has rights to acces page");
		}
		return;
	}

	public Long getMandatoryLongParameter(HttpServletRequest request,
										  String parameterName) throws ServletException {
		String value = getMandatoryStringParameter(request, parameterName);

		try {
			// return new Long(value);
			return Long.parseLong(value);
		} catch (Exception e) {
			throw new ServletException("El parametro:'" + parameterName
					+ "' con valor :'" + value
					+ "' no se puede transformar en un long ", e);
		}
	}

	public boolean getMandatoryBooleanParameter(HttpServletRequest request,
			String parameterName) throws ServletException {
		String value = getMandatoryStringParameter(request, parameterName);

		try {
			return Boolean.parseBoolean(value);
		} catch (Exception e) {
			throw new ServletException("El parametro:'" + parameterName
					+ "' con valor :'" + value
					+ "' no se puede transformar en un boolean. ", e);
		}
	}

	public int getMandatoryIntParameter(HttpServletRequest request,
			String parameterName) throws ServletException {
		String value = getMandatoryStringParameter(request, parameterName);

		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			throw new ServletException("El parametro:'" + parameterName
					+ "' con valor :'" + value
					+ "' no se puede transformar en un int ", e);
		}
	}

	public static String getMandatoryStringParameter(HttpServletRequest request,
											  String parameterName) throws ServletException {
		String value = request.getParameter(parameterName);
		if (value == null) {
			throw new ServletException("The mandatory parameter:'" + parameterName
					+ "'  does not exist.");
		} else if ("".equals(value.trim())) {
			throw new ServletException("The mandatory parameter:'" + parameterName
					+ "' does not exist.");
		} else {
			return value.trim();
		}
	}

//	public Enum getMandatoryEnumParameter(HttpServletRequest request,
//			String parameterName, Class enumType) throws ServletException {
//		String value = getMandatoryStringParameter(request, parameterName);
//		try {
//			return Enum.valueOf(enumType, value);
//		} catch (Exception e) {
//			throw new ServletException("El parametro:'" + parameterName
//					+ "' con valor :'" + value
//					+ "' no se puede transformar en un enumerado ", e);
//		}
//	}
	
	public SQLEnum getMandatorySQLEnumParameter(HttpServletRequest request,
												String parameterName, 
												Class<? extends SQLEnum> enumType) throws ServletException {
		String value = getMandatoryStringParameter(request, parameterName);
		try {
			return SQLEnum.valueUseWithCare(enumType, value);
		} catch (Exception e) {
			throw new ServletException("El parametro:'" + parameterName
					+ "' con valor :'" + value
					+ "' no se puede transformar en un enumerado ", e);
		}
	}

	public SQLEnum getSQLEnumParameter(HttpServletRequest request,
									   String parameterName, 
									   Class<? extends SQLEnum> enumType,
									   SQLEnum defaultValue) throws ServletException {
		String value = getStringParameter(request, parameterName,null);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return SQLEnum.valueUseWithCare(enumType, value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un enumerado ", e);
				}
				return defaultValue;				
			}
		}
	}


	static public String getUserId(HttpServletRequest request) {
		HttpSession session = request.getSession();
		
		return getUserId(session);

//		String userId=request.getRemoteUser();
//		if (userId==null){
//			return null;
//		} else {
//			return userId.toLowerCase();
//		}
	}

	public static String getUserId(HttpSession session) {
		String userId=SessionAccesor.getUserId(session);
		if (userId!=null){
			return userId;
		} else {
			return null;
		}
	}

	public static void setUserId(HttpServletRequest request,
							   String userId) {
		String oldUserId=getUserId(request);
		HttpSession session = request.getSession();
		// session.setAttribute(LOGIN_SESSION_PARAMETER_NAME, user);
		
		if (!StringUtils.equals(userId, oldUserId)){
			SessionAccesor.setUserId(session,userId);
	
			Language language = UsersConnector.getLanguage(userId);
			if (language != null) {
				setLanguage(request, language);
			}
		}
	}

	private static void initializeLanguage(HttpServletRequest request) {
		// firt test if the language is stored in session..
		HttpSession session = request.getSession();
		Language language=SessionAccesor.getLanguage(session);

		if (language==null){
			setThreadLocallanguage(request);			
		} else {
			I18NConnector.setThreadLocalLanguage(language);
		}
	}

	public static void setLanguage(HttpServletRequest request, Language language) {
		HttpSession session = request.getSession();

		if (I18NConnector.isAvailableLanguage(language)) {
			SessionAccesor.setLanguage(session,language);

			I18NConnector.setThreadLocalLanguage(language);
		} else {
			Language newLanguaje=I18NConnector.getClosestLanguage(language);
			SessionAccesor.setLanguage(session,newLanguaje);	

			I18NConnector.setThreadLocalLanguage(newLanguaje);
		}
	}


	public static String getStringParameter(HttpServletRequest request,
											String parameterName, 
											String defaultValue) {
		String value = request.getParameter(parameterName);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			return value.trim();
		}
	}

	public String getStringParameter(HttpServletRequest request,
			String parameterName, String notDefinedValue,String emptyDefaultValue) {
		String value = request.getParameter(parameterName);
		if (value == null) {
			return notDefinedValue;
		} else if ("".equals(value.trim())) {
			return emptyDefaultValue;
		} else {
			return value.trim();
		}
	}
	
	public String[] getStringParameterArray(HttpServletRequest request,
											String parameterName, 
											String separator) {
		String value = getStringParameter(request, parameterName, null);
		
		if (value==null || "".equals(value)){
			return EMPTY_STRING_ARRAY;
		} else {
			return StringUtils.split(value,separator);	
		}
	}

	public String[] getStringParameterArray(HttpServletRequest request,
											String parameterName) {
		return getStringParameterArray(request, parameterName, ",");
	}

	public double[] getDoubleParameterArray(HttpServletRequest request,
											String parameterName, 
											String separator,
											double defaultValue) {
		String array[]=getStringParameterArray(request,parameterName,separator);
		double ret[]=new double[array.length];
		
		for (int i=0;i<array.length;i++){
			String value=array[i];
			
			if (value==null || "".equals(value.trim())){
				ret[i]=defaultValue;
			} else {
				try {
					ret[i]=new Double(value);
				} catch (Exception e) {
					if (log.isDebugEnabled()){
						log.debug("El parametro:'" + parameterName
								  + "' con valor :'" + value
								  + "' no se puede transformar en un Double ", e);
					}
					ret[i]=defaultValue;
				}				
			}
		}
		
		return ret;
	}

	public double[] getDoubleParameterArray(HttpServletRequest request,
											String parameterName,
											double defaultValue) {
		return getDoubleParameterArray(request, parameterName, ",",defaultValue);
	}


	public long[] getLongParameterArray(HttpServletRequest request,
										String parameterName, 
										String separator,
										long defaultValue) {
		String array[]=getStringParameterArray(request,parameterName,separator);
		long ret[]=new long[array.length];
		
		for (int i=0;i<array.length;i++){
			String value=array[i];
			
			if (value==null || "".equals(value.trim())){
				ret[i]=defaultValue;
			} else {
				try {
					ret[i]=Long.parseLong(value);
				} catch (Exception e) {
					if (log.isDebugEnabled()){
						log.debug("El parametro:'" + parameterName
								  + "' con valor :'" + value
								  + "' no se puede transformar en un Long ", e);
					}
					ret[i]=defaultValue;
				}				
			}
		}
		
		return ret;
	}

	public long[] getLongParameterArray(HttpServletRequest request,
										String parameterName,
										long defaultValue) {
		return getLongParameterArray(request, parameterName, ",",defaultValue);
	}
	
	public Long getLongParameter(HttpServletRequest request,
								 String parameterName, 
								 Long defaultValue) throws ServletException {
		String value = getStringParameter(request, parameterName, null);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				//return new Long(value);
				return Long.parseLong(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un long ", e);
				}
				return defaultValue;
			}
		}
	}


	public int[] getIntParameterArray(HttpServletRequest request,
										String parameterName, 
										String separator,
										int defaultValue) {
		String array[]=getStringParameterArray(request,parameterName,separator);
		int ret[]=new int[array.length];
		
		for (int i=0;i<array.length;i++){
			String value=array[i];
			
			if (value==null || "".equals(value.trim())){
				ret[i]=defaultValue;
			} else {
				try {
					ret[i]=Integer.parseInt(value);
				} catch (Exception e) {
					if (log.isDebugEnabled()){
						log.debug("El parametro:'" + parameterName
								  + "' con valor :'" + value
								  + "' no se puede transformar en un int ", e);
					}
					ret[i]=defaultValue;
				}				
			}
		}
		
		return ret;
	}

	public int[] getIntParameterArray(HttpServletRequest request,
									String parameterName,
									int defaultValue) {
		return getIntParameterArray(request, parameterName, ",",defaultValue);
	}
	
	public int getIntParameter(HttpServletRequest request,
			String parameterName, int defaultValue) throws ServletException {
		String value = getStringParameter(request, parameterName, null);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un intero ", e);
				}
				return defaultValue;
			}
		}
	}

	public boolean getBooleanParameter(HttpServletRequest request,
			String parameterName, boolean defaultValue) throws ServletException {
		String value = getStringParameter(request, parameterName, null);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return Boolean.parseBoolean(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un boolean ", e);
				}
				return defaultValue;
			}
		}
	}

	
//
// El ValueOf utiliza el nombre del enumerado en lugar del valor de toString para leer
//	
//	public Enum getEnumParameter(HttpServletRequest request,
//			String parameterName, Class enumType, Enum defaultValue)
//			throws ServletException {
//		String value = getStringParameter(request, parameterName, null);
//		if (value == null) {
//			return defaultValue;
//		} else if ("".equals(value.trim())) {
//			return defaultValue;
//		} else {
//			try {
//				return Enum.valueOf(enumType, value);
//			} catch (Exception e) {
//				throw new ServletException("El parametro:'" + parameterName
//						+ "' con valor :'" + value
//						+ "' no se puede transformar en un enumerado ", e);
//			}
//		}
//	}
	
	//
	// El ValueOf utiliza el nombre del enumerado en lugar del valor de toString para leer
	//	
	public SQLEnum getEnumParameter(HttpServletRequest request,
									String parameterName, 
									Class<? extends SQLEnum> enumType, 
									SQLEnum defaultValue) throws ServletException {

		String value = getStringParameter(request, parameterName, null);
		if (value == null) {
				return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return SQLEnum.valueUseWithCare(enumType, value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un enumerado ", e);
				}
				return defaultValue;
			}
		}
	}

	public Float getFloatParameter(HttpServletRequest request,
								   String parameterName, 
								   Float defaultValue) throws ServletException {
		String value = getStringParameter(request, parameterName, null);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return new Float(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un Float ", e);
				}
				return defaultValue;
			}
		}
	}

	public Double getDoubleParameter(HttpServletRequest request,
									 String parameterName, 
									 Double defaultValue) throws ServletException {
		String value = getStringParameter(request, parameterName, null);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return new Double(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un Double ", e);
				}
				return defaultValue;
			}
		}
	}

	public Double getMandatoryDoubleParameter(HttpServletRequest request,
											  String parameterName) throws ServletException {
		String value = getMandatoryStringParameter(request, parameterName);

		try {
			return new Double(value);
		} catch (Exception e) {
			throw new ServletException("El parametro obligatorio:'" + parameterName
									   + "' con valor :'" + value
									   + "' no se puede transformar en un Double ", e);
		}
	}
	public Date getDateParameter(HttpServletRequest request,
			String parameterName, SimpleDateFormat sdf, Date defaultValue)
			throws ServletException {
		String value = getStringParameter(request, parameterName, null);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return sdf.parse(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
						+ "' con valor :'" + value
						+ "' no se puede transformar en un Date ", e);
				}
				
				return defaultValue;
			}
		}
	}

	public Date getMandatoryDateParameter(HttpServletRequest request,
										  String parameterName, 
										  SimpleDateFormat sdf) throws ServletException {
		String value = getMandatoryStringParameter(request, parameterName);
		try {
			return sdf.parse(value);
		} catch (Exception e) {
			throw new ServletException("El parametro:'" + parameterName
									   + "' con valor :'" + value
									   + "' no se puede transformar en un Date ", e);
		}
	}

	public Integer getIntegerParameter(HttpServletRequest request,
			String parameterName, Integer defaultValue) throws ServletException {
		String value = getStringParameter(request, parameterName, null);
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un Integer ", e);
				}
				return defaultValue;
			}
		}
	}

	/**
	 * Hace un forward a una uri relativa al contexto de la aplicacion
	 * 
	 * @param request
	 * @param response
	 * @param uri
	 *            The uri relativa al contexto de la aplicacion
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void forward(HttpServletRequest request,
			HttpServletResponse response, String uri) throws ServletException,
			IOException {
		request.getRequestDispatcher(uri).forward(request, response);
	}

	public static void redirect(HttpServletRequest request,
						 HttpServletResponse response, String url) throws IOException {
		response.sendRedirect(url);
	}

	public static void redirectToContextUri(HttpServletRequest request,
						 HttpServletResponse response, String uriRelativeToContext) throws IOException {
		response.sendRedirect(request.getContextPath()+uriRelativeToContext);
	}

	public void sendError(HttpServletRequest request,
						  HttpServletResponse response, 
						  int code, 
						  String message)
		throws IOException {
		
		ServletSecurityException e = ServletSecurityException.create(code,
																	 message, request, this);
		servletInfo.addSecurityError(request, e);

		// Para evitar que se cachen los errores y esten perennes, por ejemplo
		// si un usuario no esta logado, se accede a la pagina de datos y salta un error
		// pero cuanso se loga la pagina de datos si esta cacheada, sigue devolviendo errores ...

		response.setHeader("Cache-Control","no-cache");
		response.setHeader("Pragma","no-cache");
		response.setDateHeader("Expires", 0);
		
		response.sendError(code, message);
	}

	static public String getSessionStringValue(HttpServletRequest request,
										String parameterName, 
										String defaultValue) {
		HttpSession session=request.getSession();
		Object value=session.getAttribute(parameterName);
		
		if (value == null) {
			return defaultValue;
		} else if (value instanceof String) {
			return (String)value;
		} else {
			return defaultValue;
		}
	}
	
	static public void setSessionStringValue(HttpServletRequest request,
											 String parameterName, 
											 String value) {
		setSessionValue(request,parameterName,value);
		//		HttpSession session = request.getSession();
		//		session.setAttribute(parameterName, value);
	}

	static public void setSessionValue(HttpServletRequest request,
									   String parameterName, 
									   Object value) {
		HttpSession session = request.getSession();
		session.setAttribute(parameterName, value);
	}



	/**
	 * Devuelve una forma de identificar a las paginas. Tiene que identificarlas de 
	 * forma unica incluyendo JSPs y servlet se utiliza para los permisos y para el i18n
	 * @param request
	 * @return
	 */
	static public String getURI(HttpServletRequest request){
		return request.getRequestURI();
	}

//	static public String getLangId(HttpServletRequest request) {
//		return getLanguage(request).getLanguageId();
//	}
	
	static public Language getLanguage() {
		return I18NConnector.getThreadLocalLanguage();
	}

	static public List <Language> getLanguageFromHeader(String acceptLanguage) {
		if (acceptLanguage==null){
			return Collections.emptyList();
		} else {
			List <Language>list=new ArrayList<Language>();
			
			String array[]=StringUtils.split(acceptLanguage,',');
			
			for (String langId:array){
				if (langId.indexOf(';')>=0){
					String array2[]=StringUtils.split(langId,';');
					langId=array2[0];					
				}				
			
				Language language=Language.createFromId(langId, '-');
				
				if (language!=null){
					list.add(language);
				}			
			}
			return list;
		}

	}
	
	static public List <Language> getLanguageFromHeader(HttpServletRequest request) {
		// en-US,en;q=0.8,es;q=0.6
		// da, en-gb;q=0.8, en;q=0.7
		String acceptLanguage=request.getHeader("accept-language");


		return getLanguageFromHeader(acceptLanguage);

//		HttpSession session = request.getSession();
//
//		return SessionAccesor.getLanguage(session);
//
////		String langId=getSessionStringValue(request,LANG_SESSION,null);
////
////		if (langId!=null){
////			return I18NConnector.getLanguage(langId);
////		} else {
////			langId=UsersConnector.getUserLangId(getUserId(request),null);
////
////			if (langId==null){
////				langId=I18NConnector.getDefaultLangId();
////			}
////
////			setSessionStringValue(request,LANG_SESSION,langId);
////
////			return I18NConnector.getLanguage(langId);
////		}
	}
	
//	static public String getLangId(HttpServletRequest request) {
//		...
//
//		String lang=getSessionStringValue(request,LANG_SESSION,null);
//
//		if (lang!=null){
//			return lang;
//		} else {
//			lang=UsersConnector.getUserLangId(getUserId(request),null);
//
//			if (lang==null){
//				lang=I18NConnector.getDefaultLangId();
//			}
//
//			setSessionStringValue(request,LANG_SESSION,lang);
//
//			return lang;
//		}
//	}

	private void setLangId(HttpServletRequest request,String langId) {
		Language language=I18NConnector.getLanguageFromLangId(langId,I18NConnector.getDefaultLanguage());
		setLanguage(request,language);
	}

	private static void setThreadLocallanguage(HttpServletRequest request) {
		// Lo guardamos en el thrad local del conector
		List <Language>list=getLanguageFromHeader(request);
		
		Language newLanguaje=I18NConnector.getClosestLanguage(list);

		HttpSession session=request.getSession();
		SessionAccesor.setLanguage(session,newLanguaje);

		I18NConnector.setThreadLocalLanguage(newLanguaje);
	}

	public String getUserInfoLine(HttpServletRequest request){
		String userId=getUserId(request);
		return UsersConnector.getUserInfoLine(userId);		
	}

	static public String i18nLabel(HttpServletRequest request,String label){
		String section=getURI(request);
		return i18nLabel(section,label);
	}

//	static public String i18nLabel(HttpServletRequest request,String section,String label){
//		Language language=getLanguage(request);
//
//		return i18nLabel(language,section,label);		
//	}

	static public String i18nLabel(String section,String label){
		return I18NConnector.getLabel(section,label);		
	}

	/**
	 * Escribe el contenido del input stream en el servlet. Usar para servlet que generen imagenes y ficherios.
	 * 
	 * @param request
	 * @param response
	 * @param input
	 * @param contentLength
	 * @param fileName
	 * @param contentType
	 * @throws IOException
	 */
	static protected void printStream(HttpServletRequest request, 
									  HttpServletResponse response, 
									  InputStream input, 
									  int contentLength,
									  String fileName,
									  String contentType) throws IOException {
		response.reset();
		response.setContentLength(contentLength);
		response.setContentType(contentType);
		response.setHeader("Content-Disposition",
				"inline; filename=" + fileName+ ";");

		ServletOutputStream output=response.getOutputStream();
		IOUtils.copy(input, output);
		
		output.flush();
		input.close();
		output.close();
	} 

	/**
	 * Escribe el contenido del fichero como salida del servlet. Verifica que exite y se puede leer entes de enviarlo
	 * @param request
	 * @param response
	 * @param file
	 * @param contentType
	 * @throws IOException
	 */
	static protected void printStream(HttpServletRequest request, 
									  HttpServletResponse response, 
									  File file,
									  String contentType) throws IOException {
		String error=FileUtils.verifyReadFile(file);
		if (error==null){
			printStream(request,response,new FileInputStream(file),(int)file.length(), file.getName(),contentType);
		} else {
			Notify.error(log,"While sendinf file:'"+file.getAbsolutePath()+"', error"+error);
			response.sendError(HTTP_SERVICE_UNAVAILABLE,
							   "No File");
		}
	}

	public static void printStream(HttpServletRequest request,
								   HttpServletResponse response, 
								   byte[] bytes,
								   String fileName, 
								   String contentType) throws IOException {

		if (bytes==null){
			response.sendError(HTTP_SERVICE_UNAVAILABLE,
							   "No bytes passed");
		} else {
			response.reset();
			response.setContentLength(bytes.length);
			response.setContentType(contentType);
			response.setHeader("Content-Disposition",
							   "inline; filename=" + fileName+ ";");
			
			ServletOutputStream output=response.getOutputStream();
			IOUtils.write(bytes, output);
			
			output.flush();
			output.close();	
		}
	}

	public static Locale getLocale() {
		Language lang=getLanguage();
		
		return lang.getLocale();
	}

	public static void removeUser(HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionAccesor.removeUserId(session);
	}

	public static boolean isUserConnected(HttpServletRequest request) {
		return getUserId(request)!=null;
	} 

	public static String getRemoteAddress(HttpServletRequest request) {
		String ret=request.getRemoteAddr();
		
		if (ret!=null){
			return ret;
		} else {
			return NOT_REMOTE_ADDR_FOUND;
		}
	}

	public static String getUserAgent(HttpServletRequest request) {
		String ret=request.getHeader("user-agent");
		
		if (ret!=null){
			return ret;
		} else {
			return NOT_USER_AGENT_FOUND;
		}
	}

	public static boolean containsParameter(HttpServletRequest request,String parameterName){
		String value=getStringParameter(request,parameterName,null);
		
		return (value!=null);
	}

	public static boolean containsParameter(HttpServletRequest request,
											Map<String, String> params,
											String parameterName){
		if (!params.containsKey(parameterName)){
			return containsParameter(request,parameterName);
		} else {
			return true;
		}
	}

	public static String getUUID(HttpServletRequest request) {
//		String ret=CookieUtils.getUUIDCookie(request);
		String ret=SessionAccesor.getUUID(request);
		if (ret!=null){
			return ret;
		} else {
			return NOT_UUID_FOUND;
		}
	}

	public FileItem parseRequest(HttpServletRequest request,ServletFileUpload uploadHandler,Map<String,String> retParams) throws FileUploadException{
		List <FileItem>items = uploadHandler.parseRequest(request);

		FileItem ret = null;

		for (FileItem item:items){
			if (item.isFormField()){					
				String fieldName = item.getFieldName();
				String fieldValue = item.getString();
				
				retParams.put(fieldName, fieldValue);
			}  else {
				ret = item;										
			}
		}
		return ret;
	}
	
	public static String getStringParameter(HttpServletRequest request,
											Map<String, String> params,
											String parameterName, 
											String defaultValue) {
		
		if (!params.containsKey(parameterName)) {
			return getStringParameter(request,parameterName,defaultValue);
		} else {
			String value = params.get(parameterName);

			if (value == null || "".equals(value.trim())) {
				return defaultValue;
			} else {
				return value.trim();
			}
		}
	}

	public static String getMandatoryStringParameter(HttpServletRequest request,
													 Map<String, String> params,
													 String parameterName) throws ServletException {

		if (!params.containsKey(parameterName)) {
			return getMandatoryStringParameter(request,parameterName);
			/*
			throw new ServletException("The mandatory parameter:'" + parameterName
									   + "'  does not exist.");
			*/
		} else {
			String value = params.get(parameterName);

			if (value == null || "".equals(value.trim())) {
				throw new ServletException("The mandatory parameter:'" + parameterName
										   + "'  does not exist.");
			} else {
				return value.trim();
			}
		}
	}

	
	public static boolean getBooleanParameter(HttpServletRequest request,
											  Map<String, String> params,
											  String parameterName, 
											  boolean defaultValue) {
		String value=getStringParameter(request,params, parameterName, null);
		
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return Boolean.parseBoolean(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un boolean ", e);
				}
				return defaultValue;
			}
		}
	}

	public static double getDoubleParameter(HttpServletRequest request,
											Map<String, String> params,
											String parameterName, 
											Double defaultValue) {
		String value=getStringParameter(request,params, parameterName, null);
		
		if (value == null) {
			return defaultValue;
		} else if ("".equals(value.trim())) {
			return defaultValue;
		} else {
			try {
				return new Double(value);
			} catch (Exception e) {
				if (log.isDebugEnabled()){
					log.debug("El parametro:'" + parameterName
							  + "' con valor :'" + value
							  + "' no se puede transformar en un Double ", e);
				}
				return defaultValue;
			}
		}
	}

	public static double getMandatoryDoubleParameter(HttpServletRequest request,
													 Map<String, String> params,
													 String parameterName) throws ServletException {
		String value=getStringParameter(request,params, parameterName, null);
		
		try {
			return new Double(value);
		} catch (Exception e) {
			throw new ServletException("El parametro obligatorio:'" + parameterName
									   + "' con valor :'" + value
									   + "' no se puede transformar en un Double ", e);
		}
	}
	

//	/**
//	 * Resizes the image from a file item and return the bytes
//	 * 
//	 * @param fileItem
//	 * @param userId
//	 * @param maxImageWidth
//	 * @param maxImageHeight
//	 * @return
//	 * @throws DaoManagerException
//	 * @throws IOException
//	 */
//	public static byte[] getBytes(FileItem fileItem,int maxImageWidth,int maxImageHeight) throws DaoManagerException, IOException {
//		String mimeType=fileItem.getContentType();
//		
//		if (fileItem == null || fileItem.isFormField() || !ImageSize.isImage(mimeType)){
//			return null;
//		} else {
//			byte[] bytes=fileItem.get();
//			
//			return ImageUtils.createImage(fileItem.getName(),mimeType, bytes, 
//										  maxImageWidth, 
//										  maxImageHeight);			
//		}
//	}
//
//	public FileId insertFile(FileItem fileItem,
//							  String userId,
//							  int maxImageWidth,
//							  int maxImageHeight) throws DaoManagerException, IOException {
//		String mimeType=fileItem.getContentType();
//		
//		if (fileItem == null || fileItem.isFormField() || !ImageSize.isImage(mimeType)){
//			return null;
//		} else {
//			byte[] bytes=fileItem.get();
//			
//			return ImageUtils.createImage(fileItem.getName(), 
//										  mimeType, 
//										  userId, 
//										  bytes, 										  
//										  maxImageWidth, 
//										  maxImageHeight);
//			
//		}
//	}
	
	protected void returnToRedirect(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String redirectURL=getStringParameter(request, REDIRECT_URL, null);
		
		if (redirectURL!=null){
			redirect(request, response, redirectURL);
		} 		
	}

    /**
     * Overwrite that function if you want spetial times for servlets
     */
    public int getTestToManySessionSpetialCallsLap(){
        int minErrorTime=GlobalFileProperties.getIntValue(ServletSecurity.class, "SessionMinSpetialCallLaps", 3000);

        return minErrorTime;        
    }

    /**
     * Overwrite that function if you want spetial times for servlets
     */
    public long getTestToManySessionSpetialCallsPunish(){
		long timeToSleep=GlobalFileProperties.getIntValue(ServletSecurity.class, "SessionMinSpetialCallTimeToSleep", 10000);

        return timeToSleep;        
    }


    /**
     * Overwrite that function if you want spetial times for servlets
     */
    public int getTestToManyRemoteHostSpetialCallsLap(){
		int minErrorTime=GlobalFileProperties.getIntValue(ServletSecurity.class, "RemoteHostMinSpetialCallLaps", 5000);

        return minErrorTime;
    }

    public long getTestToManyRemoteHostSpetialCallsPunish(){
		long timeToSleep=GlobalFileProperties.getIntValue(ServletSecurity.class, "RemoteHostMinSpetialCallTimeToSleep", 10000);

        return timeToSleep;
    }
}
