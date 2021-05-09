package melnica.server.host.context.deployer.decider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import melnica.server.host.context.deployer.WebAppDeployer;
import melnica.server.host.context.deployer.impl.DirectoryAppDeployer;
import melnica.server.host.context.deployer.impl.WarAppDeployer;

public class DeployerDecider {

	private List<WebAppDeployer> deployers;
	
	public DeployerDecider() {
		this.deployers = new ArrayList<WebAppDeployer>(2);
		this.deployers.add(new WarAppDeployer());
		this.deployers.add(new DirectoryAppDeployer());
	}
	
	public WebAppDeployer decide(final File file) {

		WebAppDeployer selectedDeployer = null;
		for(WebAppDeployer deployer : this.deployers) {
			if(deployer.canExecute(file)) {
				selectedDeployer = deployer;
				break;
			}
		}
		return selectedDeployer;
	}
}
