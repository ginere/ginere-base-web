package eu.ginere.base.web.tld.i18n;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringEscapeUtils;

import eu.ginere.base.web.servlet.MainServlet;
import eu.ginere.base.web.tld.common.SimpleTag;


public class I18nJSTag extends SimpleTag{

	private static final long serialVersionUID = 1L;

	@Override
	public String getCode(HttpServletRequest request,
			HttpServletResponse response) throws JspException {
		String html=MainServlet.i18nLabel(request,id);
		
		return StringEscapeUtils.escapeJavaScript(html);	
	}

	private String id;

	/**
	 * @return the label
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param label the label to set
	 */
	public void setId(String label) {
		this.id = label;
	}
	
}
