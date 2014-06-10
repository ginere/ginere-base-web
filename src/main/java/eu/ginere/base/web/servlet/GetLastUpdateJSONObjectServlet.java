package eu.ginere.base.web.servlet;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;

import eu.ginere.base.util.dao.DaoManagerException;
import eu.ginere.base.util.dao.LastUpdateDTO;
import eu.ginere.base.web.connectors.rights.RightInterface;
import eu.ginere.base.web.json.DescriptionExclusionStrategy;
import eu.ginere.base.web.json.JSONSerializarDescriptor;
import eu.ginere.base.web.listener.ContextInitializedException;

/**
 * @author ventura
 *
 * This help to do servlet that asupport the LastUpdateDTO interface and uses the DescriptionExclusionStrategy
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public abstract class GetLastUpdateJSONObjectServlet<T extends LastUpdateDTO> extends JSONServlet {

	private static final String OBJECT_ATTR_NAME = "GetLastUpdateJSONObjectServlet";

	protected abstract T getObject(HttpServletRequest request) throws ServletException , DaoManagerException ;
	
	
	private Class <T> getTClass(){
        Type type = getClass().getGenericSuperclass();
         ParameterizedType paramType = (ParameterizedType)type;
         return (Class<T>) paramType.getActualTypeArguments()[0];
	}
	@Override
	protected String configureGsonBuilder(GsonBuilder gsonBuilder) throws IntrospectionException {
		Class <T>clazz=getTClass();
		JSONSerializarDescriptor descriptor=new DescriptionExclusionStrategy(clazz);
		gsonBuilder.setExclusionStrategies(descriptor);
		
		return descriptor.getDescription();
	}
	
	protected long getObjectlastModified(HttpServletRequest request) throws ServletException , DaoManagerException {
		T obj=getObject(request);
		
		request.setAttribute(OBJECT_ATTR_NAME, obj);
		
		if (obj==null){
			return -1;
		} else {
			return obj.lastUpdate();
		}

	}
	
	@Override
	protected Object doTaskJSONService(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,
			DaoManagerException {
//		return getObject(request);
		T obj = (T)request.getAttribute(OBJECT_ATTR_NAME);
		
		if (obj==null){
			obj=getObject(request);
		}
		
		return obj;
	}

	@Override
	protected RightInterface[] getRights() throws ContextInitializedException {
		return MainServlet.PUBLIC_ACCESS;
	}

}
