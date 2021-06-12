package melnica.server.application;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import melnica.server.LifeCycle;
import melnica.server.application.configuration.ServerConfiguration;
import melnica.server.application.configuration.ServiceConfiguration;
import melnica.server.application.util.ApplicationStandardFilePathDefinition;
import melnica.server.configuration.xml.assembler.ServerConfigurationAssembler;
import melnica.server.configuration.xml.parser.ServerConfigurationParser;
import melnica.server.service.Service;

public class MelnicaServer implements LifeCycle {

	private ServerConfiguration configuration;
	private ServerConfigurationParser configurationParser;
	private Map<String, Service> services;
	
	public MelnicaServer() {
		this.services = new HashMap<String, Service>();
		this.configurationParser = new ServerConfigurationParser(new ServerConfigurationAssembler());
	}
	
	public void init() {
		
		try {
			this.configuration = this.configurationParser.parse(generateServerConfigFilePath());
			initServices();
		}
		catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public void start() {
		
		for(String key : this.services.keySet()) {
			Service service = this.services.get(key);
			if(service == null) {
				continue;
			}
			service.start();
		}
	}
	
	public ServerConfiguration getConfiguration() {
		return this.configuration;
	}
	
	private void initServices() {
		
		Map<String, ServiceConfiguration> servicesConfMap = this.configuration.getServices();
		for(String name : servicesConfMap.keySet()) {
			Service service = new Service(name, this);
			service.init();
			this.services.put(name, service);
		}
	}
	
	private String generateServerConfigFilePath() {
		return System.getProperty("user.dir") + File.separator + ApplicationStandardFilePathDefinition.CONFIGURATION_FILE_PATH;
	}
}
