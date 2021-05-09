package melnica.server.host.context.deployer;

import java.io.File;
import java.io.IOException;

import melnica.server.host.context.deployer.model.DeployedWebAppResult;
import melnica.server.host.context.deployer.model.DeployerContext;

public abstract class WebAppDeployer {

	public abstract boolean canExecute(File file);
	public abstract DeployedWebAppResult deploy(DeployerContext context) throws IOException;
}
