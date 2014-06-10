package eu.ginere.base.web.servlet.info;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.servlet.ServletSecurityException;
import eu.ginere.base.web.session.SessionManager;


public class ServletInfo {
	public static final Logger log = Logger.getLogger(ServletInfo.class);

	private final Class<? extends MainServlet> clazz;
	
	private long totalNumber=0;
	private long totalTime=0;
	private long maxTime=0;
	
	private long runningCall=0;
	
	private long exceptionNumber=0;
	private Exception lastException=null;

//	private String uri;
//
//	private String description;
//
//	private ServletArgs[] args;

	public ServletInfo(Class<? extends MainServlet> clazz){
		this.clazz=clazz;
	}

	public synchronized void startCall() {
		this.runningCall++;	
	}
	
	public synchronized void clear(){
		this.totalNumber=0;
		this.totalTime=0;
		this.maxTime=0;
		this.lastException=null;
		this.exceptionNumber=0;
		this.runningCall=0;
	}
	
	public synchronized void addLaps(long laps){
		this.totalNumber++;
		this.totalTime+=laps;
		this.runningCall--;	
		if(laps>maxTime){
			this.maxTime=laps;
		}
	}

	public synchronized void addException(HttpServletRequest request,Exception e){
		this.lastException=e;
		this.exceptionNumber++;

		SessionManager.MANAGER.notifyError(request,e);
	}
	
	public synchronized void addSecurityError(HttpServletRequest request,ServletSecurityException e){
		this.lastException=e;
		this.exceptionNumber++;

		SessionManager.MANAGER.notifyError(request,e);
	}
	public Class<? extends MainServlet> getServletClass() {
		return clazz;
	}

	public long getTotalNumber() {
		return totalNumber;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public long getExceptionNumber() {
		return exceptionNumber;
	}

	public long getAverageTime() {
		if (totalNumber>0){
			return totalTime/totalNumber;
		} else {
			return 0;
		}
	}
	
	public Exception getLastException() {
		return lastException;
	}


	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public long getRunningCall() {
		return runningCall;
	}

	public void startSpetialCall(HttpServletRequest request,String uri) {
		SessionManager.MANAGER.notifySpetialCall(request,uri);
	}

//	public void setUri(String uri) {
//		this.uri=uri;
//	}
//
//	public void setDescription(String description) {
//		this.description=description;
//	}
//
//	/**
//	 * @return the uri
//	 */
//	public String getUri() {
//		return uri;
//	}
//
//	/**
//	 * @return the description
//	 */
//	public String getDescription() {
//		return description;
//	}
//
//	public void setArgs(ServletArgs[] args) {
//		this.args=args;
//	}
//
//	/**
//	 * @return the args
//	 */
//	public ServletArgs[] getArgs() {
//		return args;
//	}
	
	
}
