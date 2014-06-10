package ginere.base.web.servlet;

import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.ginere.base.util.i18n.Language;
import eu.ginere.base.web.servlet.MainServlet;

public class MainServletTest extends TestCase {

	static final Logger log = Logger.getLogger(MainServletTest.class);
	
//	private static void setDataSource() throws Exception {
//		DataSource dataSource = JdbcManager.createMySQLDataSourceFromPropertiesFile("/etc/cgps/jdbc.properties");
//		
//		JdbcManager.setDataSource(dataSource);
//	}


	@Test
	static public void testAcceptLanguage() throws Exception {	
		try {
			String accepLanguage="en-US,en;q=0.8,es;q=0.6";

			long time=System.currentTimeMillis();
			List <Language>list=MainServlet.getLanguageFromHeader(accepLanguage);
			
			log.info("Time:"+(System.currentTimeMillis()-time));
			for (Language lang:list){
				log.info("Language:"+lang);
			}
			
			accepLanguage=" da, en-gb;q=0.8, en;q=0.7";
			
			time=System.currentTimeMillis();
			list=MainServlet.getLanguageFromHeader(accepLanguage);
			log.info("Time:"+(System.currentTimeMillis()-time));
			
			for (Language lang:list){
				log.info("Language:"+lang);
			}
			
			
		} catch (Exception e) {
			log.error("", e);
			throw e;
		}
	}

}
