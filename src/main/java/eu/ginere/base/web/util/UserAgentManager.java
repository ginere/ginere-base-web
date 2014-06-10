/**
 * Copyright: Angel-Ventura Mendo Gomez
 *	      ventura@free.fr
 *
 * $Id: UserAgentUtils.java,v 1.1 2006/11/25 07:21:36 ventura Exp $
 */
package eu.ginere.base.web.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.ginere.base.util.manager.AbstractManager;
import eu.ginere.base.util.properties.FileProperties;


/**
 * UserAgentUtilies
 *
 * @author Angel Mendo
 * @version $Revision: 1.1 $
 */
public class UserAgentManager extends AbstractManager {
	
	public static final Logger log = Logger.getLogger(UserAgentManager.class);

	private static FileProperties fileManagerProperties = null;
	private static final String PROPERTIES_FILE_NAME = "UserAgentManager.properties";
	
	static FileProperties getFileProperties() {
		if (fileManagerProperties == null) {
			fileManagerProperties = AbstractManager.getFileProperties(PROPERTIES_FILE_NAME);
		}
		return fileManagerProperties;
	}

	static final String DEFAULT_USER_AGENT="chrome";

	public static boolean isSupported(String agent){
		String familly=getUserAgentFamilly(agent);
		
		return isFamillySupported(familly);
	}
	
	private static boolean isFamillySupported(String familly) {
		String array[]=getFileProperties().getPropertyList(UserAgentManager.class,"SupportedFamilies");
		
		for (String value:array){
			if (StringUtils.equals(value, familly)){
				return true;
			}
		}
		
		return false;
	}

	public static boolean isRobot(String agent){
		return isFamilly("robot",agent);
	}

	/**
	 * Return true if the user agent bellong to one failly
	 */
	public static boolean isFamilly(String familly,String agent){
		String array[]=getFileProperties().getPropertyList(UserAgentManager.class,familly);
		
		agent=agent.toLowerCase();
		for (int i=0;i<array.length;i++){
			if (agent.indexOf(array[i])>=0){
				return true;
			}
		}
		return false;
		
	}

	public static String[] getFamillyList(){
		return getFileProperties().getPropertyList(UserAgentManager.class,"FAMILLY_LIST");
	}

	/**
	 * Returns the borser familly, ie ,mozilla, etc ...
	 */
	public static String getUserAgentFamilly(String agent){
		String famillyList[]= getFamillyList();
		
		for (int i=0;i<famillyList.length;i++){
			if (isFamilly(famillyList[i],agent)){
				return famillyList[i];
			}
		}
		
		log.warn("The agent:'"+agent+"' has not be found into the database");
		return DEFAULT_USER_AGENT;
	}
}
