package melnica.server.host;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import melnica.server.LifeCycle;
import melnica.server.application.configuration.HostConfiguration;
import melnica.server.host.context.deployer.model.DeployedWebAppResult;
import melnica.server.host.context.manager.WebAppManager;
import melnica.server.service.Service;
import melnica.server.web.context.WebAppContext;

public class Host implements LifeCycle {

	private Service service;
	private WebAppManager appManager;
	private HostConfiguration configuration;
	
	private Map<String, WebAppContext> contexts;
	private Map<String, WebAppContext> contextsWithDisplayName;

	
	public Host(Service service, HostConfiguration configuration) {
		this.service = service;
		this.configuration = configuration;
		this.appManager = new WebAppManager(this);
		this.contexts = new ConcurrentHashMap<String, WebAppContext>();
	}
	
	public void init() {
		
		this.appManager.process();
		initWebAppContexts();
	}

	public void start() {
		startWebApps();
	}
	
	private void startWebApps() {
		
		if(this.contextsWithDisplayName == null) {
			this.contextsWithDisplayName = new HashMap<String, WebAppContext>(this.contexts.size());
		}
		
		for(String key : this.contexts.keySet()) {
			WebAppContext context = this.contexts.get(key);
			if(context == null) {
				continue;
			}
			context.start();
			String displayName = context.getWebXml().getWebAppDisplayName();
			this.contextsWithDisplayName.put(displayName, context);
		}
	}
	
	public synchronized WebAppContext findWebAppContext(String displayName) {
		return this.contextsWithDisplayName.get(displayName);
	}
	
	public String getName() {
		return this.configuration.getDomain();
	}
	
	public String getDomain() {
		return this.configuration.getDomain();
	}
	
	public Service getService() {
		return this.service;
	}
	
	public Map<String, WebAppContext> getWebAppMap() {
		return this.contextsWithDisplayName;
	}

	public synchronized void addWebAppContext(DeployedWebAppResult result) {

		this.contexts.put(result.getApplicationName(), new WebAppContext(this, result));
	}
	
	public synchronized boolean isWepAppExistInMap(String webAppName) {
		
		return this.contexts.containsKey(webAppName);
	}
	
	private void initWebAppContexts() {
		for(String name : this.contexts.keySet()) {
			WebAppContext context = this.contexts.get(name);
			if(context == null) {
				continue;
			}
			context.init();
		}
	}
}
