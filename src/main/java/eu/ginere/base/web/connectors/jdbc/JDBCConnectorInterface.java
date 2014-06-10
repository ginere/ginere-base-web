package eu.ginere.base.web.connectors.jdbc;

public interface JDBCConnectorInterface {

	public void setJniDataSource(String jniName) throws Exception ;

	public boolean testConnection() throws Exception;

}