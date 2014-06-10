package eu.ginere.base.web.tld.rights;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;

import eu.ginere.base.web.connectors.rights.RightConnector;
import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.tld.common.AbstractBodyTag;


public class HasRightTag extends AbstractBodyTag{

	private static final long serialVersionUID = 1L;
	
	protected String id=null;
	
	public String getId(){
		return id;
    }

    public void setId(String value){
        this.id = value;
    }

	@Override
	protected int initTag(HttpServletRequest request,
			HttpServletResponse response) throws JspException {
		String userId=MainServlet.getURI(request);
		
		if (RightConnector.hasRight(userId, id)){
			return EVAL_BODY_BUFFERED;
		} else {
			return SKIP_BODY;
		}
	}

	@Override
	protected int doAfterBody(HttpServletRequest request,
			HttpServletResponse response, BodyContent body) throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public void doEndTag(PageContext pageContext, HttpServletRequest request,
			HttpServletResponse response) throws JspException {
	}


}
