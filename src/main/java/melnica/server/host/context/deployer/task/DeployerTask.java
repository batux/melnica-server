package melnica.server.host.context.deployer.task;

import java.io.IOException;

import melnica.server.host.Host;
import melnica.server.host.context.deployer.WebAppDeployer;
import melnica.server.host.context.deployer.model.DeployedWebAppResult;
import melnica.server.host.context.deployer.model.DeployerContext;

public class DeployerTask implements Runnable {

	private volatile Host host;
	private volatile WebAppDeployer deployer;
	private DeployerContext contex;
	
	public DeployerTask(Host host, WebAppDeployer deployer, DeployerContext contex) {
		this.host = host;
		this.deployer = deployer;
		this.contex = contex;
	}
	
	public void run() {
		
		if(this.deployer == null || this.contex == null) {
			throw new RuntimeException("Web app deployer or context can not be null.");
		}
		
		if(this.host.isWepAppExistInMap(this.contex.getWebAppName())) {
			return;
		}
		
		DeployedWebAppResult result = null;
		try {
			result = this.deployer.deploy(this.contex);
		} 
		catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		if(result != null && result.isSuccess()) {
			this.host.addWebAppContext(result);
		}
		this.clear();
	}
	
	private void clear() {
		
		this.deployer = null;
		this.contex = null;
	}
}
