//package eu.ginere.base.web.servlet.info;
//
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.builder.ToStringBuilder;
//import org.apache.log4j.Logger;
//
//import eu.ginere.base.web.connectors.rights.RightInterface;
//import eu.ginere.base.web.servlet.MainServlet;
//import eu.ginere.base.web.servlet.ServletSecurityException;
//import eu.ginere.base.web.session.SessionManager;
//
//
//public class ServletInfo {
//	public static final Logger log = Logger.getLogger(ServletInfo.class);
//
//	private final Class<? extends MainServlet> clazz;
//	
//	public final String name;
//	
//	private long totalNumber=0;
//	private long totalTime=0;
//	private long maxTime=0;
//	
//	private long runningCall=0;
//	
//	private long exceptionNumber=0;
//	private Exception lastException=null;
//
//	private String description;
//
//	private String url;
//
//	private String rightDescription;
//
////	private String uri;
////
////	private String description;
////
////	private ServletArgs[] args;
//
////	public ServletInfo(Class<? extends MainServlet> clazz){
//	public ServletInfo(MainServlet servlet){
//		Class<? extends MainServlet> clazz=servlet.getClass();
//		this.clazz=clazz;
//		this.name=clazz.getName();
//		
//		WebServlet ws=(WebServlet)clazz.getAnnotation(WebServlet.class);
//
//		if (ws != null){
//			this.description="";
//			this.description+="Name: "+ws.name();
//			this.description+="Displayname: "+ws.displayName();
//			this.description+="Description: "+ws.description();
//			this.description+="AsyncSupported: "+ws.asyncSupported();
//
//			this.url=StringUtils.join(ws.value());
//		} else {
//			this.url=servlet.getUri();
//			this.description=servlet.getDescription();			
////			protected abstract ServletArgs[] getArgs(); 
//
//		}	
//
//
//		RightInterface[] rights=servlet.getServletRights();
//
//		if (rights == null){
//			this.rightDescription="Public access";
//		} else if (rights.length == 0){
//			this.rightDescription="User logged";
//		} else {
//			this.rightDescription=null;
//			for (RightInterface right:rights){
//				if (this.rightDescription == null){
//					this.rightDescription+=right.getName();
//				} else {
//					this.rightDescription+=","+right.getName();
//				}
//			}
//		}
//
//	}
//
//	public synchronized void startCall() {
//		this.runningCall++;	
//	}
//	
//	public synchronized void clear(){
//		this.totalNumber=0;
//		this.totalTime=0;
//		this.maxTime=0;
//		this.lastException=null;
//		this.exceptionNumber=0;
//		this.runningCall=0;
//	}
//	
//	public synchronized void addLaps(long laps){
//		this.totalNumber++;
//		this.totalTime+=laps;
//		this.runningCall--;	
//		if(laps>maxTime){
//			this.maxTime=laps;
//		}
//	}
//
//	public synchronized void addException(HttpServletRequest request,Exception e){
//		this.lastException=e;
//		this.exceptionNumber++;
//
//		SessionManager.MANAGER.notifyError(request,e);
//	}
//	
//	public synchronized void addSecurityError(HttpServletRequest request,ServletSecurityException e){
//		this.lastException=e;
//		this.exceptionNumber++;
//
//		SessionManager.MANAGER.notifyError(request,e);
//	}
//	public Class<? extends MainServlet> getServletClass() {
//		return clazz;
//	}
//
//	public long getTotalNumber() {
//		return totalNumber;
//	}
//
//	public long getTotalTime() {
//		return totalTime;
//	}
//
//	public long getMaxTime() {
//		return maxTime;
//	}
//
//	public long getExceptionNumber() {
//		return exceptionNumber;
//	}
//
//	public long getAverageTime() {
//		if (totalNumber>0){
//			return totalTime/totalNumber;
//		} else {
//			return 0;
//		}
//	}
//	
//	public Exception getLastException() {
//		return lastException;
//	}
//
//
//	public String toString() {
//		return ToStringBuilder.reflectionToString(this);
//	}
//
//	public long getRunningCall() {
//		return runningCall;
//	}
//
//	public void startSpetialCall(HttpServletRequest request,String uri) {
//		SessionManager.MANAGER.notifySpetialCall(request,uri);
//	}	
//}
