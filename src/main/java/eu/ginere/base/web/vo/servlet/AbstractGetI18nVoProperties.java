package eu.ginere.base.web.vo.servlet;

import java.beans.IntrospectionException;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.web.servlet.SimpleDataServlet;
import eu.ginere.base.web.vo.AbstractI18nClassWriter;

@SuppressWarnings("serial")
public abstract class AbstractGetI18nVoProperties extends SimpleDataServlet {


	private AbstractI18nClassWriter i18nAbstractDTOFields;
	private long lastModified=-1;
	

	public void init(ServletConfig config) throws ServletException {
		try {
			this.i18nAbstractDTOFields=getI18nDTOFields();
		}catch (IntrospectionException e){
			throw new ServletException(this.getClass().getName(),e);
		}
		
		lastModified=System.currentTimeMillis();
		super.init(config);
	}

    protected long getLastModifiedException(HttpServletRequest req) {
    	return lastModified;
    }
	
	abstract protected AbstractI18nClassWriter getI18nDTOFields() throws IntrospectionException;


	@Override
	protected String doSimpleDataService(HttpServletRequest request,
										 HttpServletResponse response) throws ServletException,IOException,DaoManagerException{
		
		Language langId=getLanguage();
		return i18nAbstractDTOFields.print(langId);
	}

}
