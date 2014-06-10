package eu.ginere.base.web.vo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import eu.ginere.base.util.enumeration.SQLEnum;
import eu.ginere.base.web.servlet.MainServlet;

public abstract class AbstractVO{

	private transient static final String DATE_PATTERN="dd-MMM-yyyy HH:mm:ss";
	
	private transient final HttpServletRequest request;
	private transient SimpleDateFormat sdf=null;
	private transient Locale locale=null;
	
	protected AbstractVO(HttpServletRequest request){
		this.request=request;
	}

	protected String intToString(int i) {
		return Integer.toString(i);
	}
	
	protected String sqlEnumToString(SQLEnum tipoInstalacion) {
		if (tipoInstalacion==null){
			return "";
		} else {
			return tipoInstalacion.getName();
		}
	}

	protected String dateToString(Date date) {
		if (date==null){
			return "";
		} else {
			return getDateFormat().format(date);
		}
	}
	
	private SimpleDateFormat getDateFormat(){
		if (this.sdf==null){
			this.sdf=new SimpleDateFormat(DATE_PATTERN,getLocale());
		}

		return sdf;
	}

	private Locale getLocale(){
		if (this.locale==null){
			this.locale=MainServlet.getLocale();;
		}

		return locale;
	}
}