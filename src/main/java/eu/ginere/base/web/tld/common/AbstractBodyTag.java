package eu.ginere.base.web.tld.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.log4j.Logger;

public abstract class AbstractBodyTag extends BodyTagSupport implements TagInterface{

	public static final Logger log = Logger.getLogger(AbstractBodyTag.class);

    protected long time=0;

    protected String tagName=this.getClass().getName();
    public String getTagName(){
		return tagName;
    }


    /**
     * This is the function that initializes the tag. After calling this 
     * function tag must be ready to work and all variables must be 
     * initialized.
     * @return EVAL_BODY_BUFFERED to evaluate the body on succes. and return 
     * SKIP_BODY and writes the error if initialization fails. The tag body will
     * not be initializated
     */
    abstract protected int initTag(HttpServletRequest request,HttpServletResponse response)throws JspException;
    

    /**
	 * This function is called after the body evaluation.
	 * If this function return EVAL_BODY_BUFFERED the body will be reevaluated 
	 * if it return SKIP_BODY the tag evaluation will end.
	 * To write use JspWriter jw = body.getEnclosingWriter(); jw.println("start");  body.clearBody(); </br>
	 * To simply write the content do :   body.writeOut(body.getEnclosingWriter()); body.clearBody(); return SKIP_BODY;</br>
	 * Attention if you do not nothig into this function the body will not be writen !!!!
     * @return EVAL_BODY_BUFFERED to re evaluate the body or 
     * SKIP_BODY to end evaluation
     */
    abstract protected int doAfterBody(HttpServletRequest request,HttpServletResponse response,BodyContent body)throws JspException;

	/**
	 * Us this tag to call the includes.
	 * to use to write to the output use: JspWriter writer = pageContext.getOut(); writer.print("doEndTag");
	 */
    abstract public void doEndTag(PageContext pageContext,HttpServletRequest request,HttpServletResponse response ) throws JspException ;

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


		int ret=initTag(getRequest(),getResponse());
		
		return ret;
    }
    
    public int doAfterBody() throws JspException{	
		try {
			BodyContent body = getBodyContent();
			return doAfterBody(getRequest(),getResponse(),body);
		}catch(JspException e){
			log.info("While doAfterBody for tab:"+getClass().getName()+"'",e);
			throw e;
		}
    }

    
    /**
     */
    public int doEndTag() throws JspException {
		doEndTag(pageContext,getRequest(),getResponse());

		if (log.isDebugEnabled()){
			log.debug("TAG doEndTag Tag class:'"+getClass().getName()+
					  "' name:'"+tagName+
					  "' in time:"+(System.currentTimeMillis()-time));
		}
        return super.doEndTag();
    }


	public HttpServletRequest getRequest(){
		return (HttpServletRequest) pageContext.getRequest();
	}

	public HttpServletResponse getResponse(){
        return (HttpServletResponse) pageContext.getResponse();
	}
}
