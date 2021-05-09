package melnica.server.application.configuration;

import java.util.List;

public class ServiceConfiguration {

	private String name;
	private List<HostConfiguration> hosts;
	private List<BosphorusConfiguration> connectors;
	private List<String> webPlatforms;
	
	public ServiceConfiguration() {}
	
	public ServiceConfiguration(
			String name, 
			List<HostConfiguration> hosts, 
			List<BosphorusConfiguration> connectors, 
			List<String> webPlatforms) {
		this.name = name;
		this.hosts = hosts;
		this.connectors = connectors;
		this.webPlatforms = webPlatforms;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<HostConfiguration> getHosts() {
		return this.hosts;
	}
	
	public List<BosphorusConfiguration> getConnectors() {
		return this.connectors;
	}
	
	public List<String> getWebplatforms() {
		return this.webPlatforms;
	}
}
