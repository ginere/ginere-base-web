package eu.ginere.base.web.connectors.users;

import java.util.ArrayList;
import java.util.List;

import eu.ginere.base.util.i18n.Language;

public class DummyUsersConnector implements UsersConnectorInterface {

	
	public DummyUsersConnector(){
		
	}
	
	@Override
	public String getUserInfoLine(String userId) {
		return userId;
	}

	@Override
	public Language getUserLanguage(String userId,Language defaultvalue) {
		return defaultvalue;
	}

	@Override
	public void setUserLanguage(String userId, Language langId) {
		return ;
	}

	@Override
	public String getUserName(String userId) {
		return userId;
	}

	@Override
	public List<String> getUserGroupNames(String userId) {
		return new ArrayList<String>(0);
	}

	@Override
	public boolean exists(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean validateLoginCookie(String userId, String uuid,String cookieValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean login(String userId, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void logout(String userId,String uuid,boolean all) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnect(String userId) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public void connect(String userId) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public String generateUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateLoginCookie(String userId, String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean useCookiesForLogin() {
		return false;
	}

}
