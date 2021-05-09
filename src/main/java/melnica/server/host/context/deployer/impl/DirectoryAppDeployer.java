package melnica.server.host.context.deployer.impl;

import java.io.File;
import java.util.Locale;

import melnica.server.host.context.deployer.WebAppDeployer;
import melnica.server.host.context.deployer.model.DeployedWebAppResult;
import melnica.server.host.context.deployer.model.DeployerContext;
import melnica.server.host.context.deployer.util.JarFileProcessor;

public class DirectoryAppDeployer extends WebAppDeployer {

	@Override
	public boolean canExecute(File file) {
		return !file.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war") && 
				file.isDirectory();
	}

	@Override
	public DeployedWebAppResult deploy(DeployerContext context) {
		
		String webAppFilePath = JarFileProcessor.expandDirectory(new File(context.getWebAppBaseFilePath()), context.getWebAppName());
		return new DeployedWebAppResult(context.getWebAppName(), webAppFilePath, !webAppFilePath.isBlank());
	}
}
