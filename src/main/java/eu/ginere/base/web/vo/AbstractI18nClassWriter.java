package eu.ginere.base.web.vo;

import java.beans.IntrospectionException;

import eu.ginere.base.util.descriptor.AbstractClassDescriptor;
import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.web.connectors.i18n.I18NConnector;

public abstract class AbstractI18nClassWriter extends AbstractClassWriter {

//	private final Map <Language,String>cache=new Hashtable<Language,String>();

	public AbstractI18nClassWriter(AbstractClassDescriptor descriptor) throws IntrospectionException{
		super(descriptor);
	}

	public String print(){
		Language language=I18NConnector.getThreadLocalLanguage();
		
		return print(language);
	}
	
	public String print(Language language){
//		if (cache.containsKey(language)){
//			return cache.get(language) ;
//		} else {
//			String value=innerPrinter(language);
//			cache.put(language,value);
//
//			return value;
//		}
		
		return innerPrinter(language);
	}
	
	protected String innerPrinter(){
		return innerPrinter(I18NConnector.getThreadLocalLanguage());
	}
	
	protected abstract String innerPrinter(Language language);
}
