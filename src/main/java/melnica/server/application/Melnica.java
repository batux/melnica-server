package melnica.server.application;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import melnica.server.LifeCycle;
import melnica.server.application.configuration.ServerConfiguration;
import melnica.server.application.configuration.ServiceConfiguration;
import melnica.server.application.util.ApplicationFilePathDefinition;
import melnica.server.service.Service;
import melnica.server.xml.assembler.ServerConfigurationAssembler;
import melnica.server.xml.parser.ServerConfigurationParser;

public class Melnica implements LifeCycle {

	private ServerConfiguration configuration;
	private ServerConfigurationParser confParser;
	private Map<String, Service> services;
	
	public Melnica() {
		this.services = new HashMap<String, Service>();
		this.confParser = new ServerConfigurationParser(new ServerConfigurationAssembler());
	}
	
	public void init() {
		try {
			this.configuration = this.confParser.parse(System.getProperty("user.dir") + File.separator + ApplicationFilePathDefinition.CONF_FILE_PATH);
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
}
