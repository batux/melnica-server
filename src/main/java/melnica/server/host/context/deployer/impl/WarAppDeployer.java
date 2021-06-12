package melnica.server.host.context.deployer.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import melnica.server.host.context.deployer.JarFileProcessor;
import melnica.server.host.context.deployer.WebAppDeployer;
import melnica.server.host.context.deployer.model.DeployedWebAppResult;
import melnica.server.host.context.deployer.model.DeployerContext;

public class WarAppDeployer extends WebAppDeployer {

	@Override
	public boolean canExecute(File file) {
		return file.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war") && file.isFile();
	}

	@Override
	public DeployedWebAppResult deploy(DeployerContext context) throws IOException {
		
		URL warUrl = JarFileProcessor.buildJarUrl(context.getWebApplicationFile());
		String webApplicationFilePath = JarFileProcessor.expandWar(warUrl, new File(context.getWebApplicationsFolderPath()), context.getWebApplicationName());
		return new DeployedWebAppResult(context.getWebApplicationName(), webApplicationFilePath, !webApplicationFilePath.isBlank());
	}
}
