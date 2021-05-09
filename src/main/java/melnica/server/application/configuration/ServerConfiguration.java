package melnica.server.application.configuration;

import java.util.Map;

public class ServerConfiguration {

	private Map<String, ServiceConfiguration> services;
	private String shutdownCommand;

	public ServerConfiguration() {}
	
	public ServerConfiguration(Map<String, ServiceConfiguration> services, String shutdownCommand) {
		this.services = services;
		this.shutdownCommand = shutdownCommand;
	}
	
	public Map<String, ServiceConfiguration> getServices() {
		return this.services;
	}
	
	public String getShutdownCommand() {
		return this.shutdownCommand;
	}
}
