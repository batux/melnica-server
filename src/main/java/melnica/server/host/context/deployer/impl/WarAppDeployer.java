package melnica.server.host.context.deployer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import melnica.server.host.context.deployer.WebAppDeployer;
import melnica.server.host.context.deployer.model.DeployedWebAppResult;
import melnica.server.host.context.deployer.model.DeployerContext;
import melnica.server.host.context.deployer.util.JarFileProcessor;

public class WarAppDeployer extends WebAppDeployer {

	@Override
	public boolean canExecute(File file) {
		return file.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war") && file.isFile();
	}

	@Override
	public DeployedWebAppResult deploy(DeployerContext context) throws IOException {
		
		URL warUrl = JarFileProcessor.buildJarUrl(context.getAppFile());
		String webAppFilePath = JarFileProcessor.expandWar(warUrl, new File(context.getWebAppBaseFilePath()), context.getWebAppName());
		return new DeployedWebAppResult(context.getWebAppName(), webAppFilePath, !webAppFilePath.isBlank());
	}
}
