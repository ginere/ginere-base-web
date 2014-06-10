package eu.ginere.base.web.tld.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.Tag;


/**
 * @author Angel-Ventura Mendo
 * @version $Revision: 1.4 $
 */
public interface TagInterface extends Tag{
    public String getTagName();

	public HttpServletRequest getRequest();
	public HttpServletResponse getResponse();
}

