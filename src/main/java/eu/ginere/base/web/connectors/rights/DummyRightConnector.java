package eu.ginere.base.web.connectors.rights;

public class DummyRightConnector implements RightConnectorInterface{

	public DummyRightConnector(){
		
	}
	
	@Override
	public boolean hasRight(String userId, String rightId) {
		// TODO Auto-generated method stub
		return true;
	}

	public void subscriveApplicationRights(
			RightInterface[] applicationPermision) {
		
	}
}
