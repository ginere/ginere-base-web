package eu.ginere.base.web.tld.common;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.log4j.Logger;

public abstract class SimpleTag extends TagSupport implements TagInterface{

	public static final Logger log = Logger.getLogger(SimpleTag.class);

    protected long time=0;

    protected String tagName=this.getClass().getName();
    public String getTagName(){
		return tagName;
    }

    
	/**
	 * If this function return null no output will be done
	 */
    public abstract String getCode(HttpServletRequest request,
                                       HttpServletResponse response)
        throws JspException;
    
    /**
     * This is the function that initializes the tag. After calling this 
     * function tag must be ready to work and all variables must be 
     * initialized.
     */
    // abstract protected void boolean initTag(HttpServletRequest request,HttpServletResponse response);

    /**
     * Render the beginning of the hyperlink.
     *
     * @exception JspException if a JSP exception has occurred
     */
    public int doStartTag() throws JspException {
		time=System.currentTimeMillis();
		if (log.isDebugEnabled()){
			log.debug("TAG doStartTag Tag class:'"+getClass().getName()+
					"' name:'"+tagName+
				  	"' ");
		}
        try {
            String html=getCode(getRequest(),getResponse());
			if (html!=null){
				// Print this element to our output writer
				JspWriter writer = pageContext.getOut();
				writer.print(html);
			}
       } catch (IOException e) {
            throw new JspException(SimpleTag.class.getName(),e);
        }
        
        // Evaluate the body of this tag
        return SKIP_BODY;
    }
    
    
    /**
     */
    public int doEndTag() throws JspException {
		// Releases the tag
		releaseTag();

		if (log.isDebugEnabled()){
			log.debug("TAG doEndTag Tag class:'"+getClass().getName()+
					  "' name:'"+tagName+
					  "' in time:"+(System.currentTimeMillis()-time));
		}
        return super.doEndTag();
    }

    /**
	 * This function must realease tag.
	 * All the information must be free for tag reutilisability
     */
    protected void releaseTag(){
		tagName=this.getClass().getName();
	}

	/**
	 * Called on a Tag handler to release state. The page compiler guarantees 
	 * that JSP page implementation objects will invoke this method on all tag 
	 * handlers, but there may be multiple invocations on doStartTag and 
	 * doEndTag in between
	 */
	public void release(){
	}

	public HttpServletRequest getRequest(){
		return (HttpServletRequest) pageContext.getRequest();
	}

	public HttpServletResponse getResponse(){
        return (HttpServletResponse) pageContext.getResponse();
	}


        
}
