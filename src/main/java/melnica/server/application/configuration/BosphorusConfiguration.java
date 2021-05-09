package melnica.server.application.configuration;

public class BosphorusConfiguration {

	private int port;
	private String protocol;
	private int timeout;
	
	public BosphorusConfiguration() {}
	
	public BosphorusConfiguration(int port, String protocol, int timeout) {
		this.port = port;
		this.protocol = protocol;
		this.timeout = timeout;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getProtocol() {
		return this.protocol;
	}
	
	public int getTimeout() {
		return this.timeout;
	}
}
