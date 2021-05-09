package melnica.server.host.context.deployer.model;

import java.io.File;

public class DeployerContext {

	private File appFile;
	private String webAppName;
	private String webAppFileName;
	private String webAppBaseFilePath;
	
	public DeployerContext(File file, String webAppFileName, String webAppBaseFilePath) {
		this.appFile = file;
		this.webAppFileName = webAppFileName;
		this.webAppName = this.webAppFileName.replace(".war", "");
		this.webAppBaseFilePath = webAppBaseFilePath;
	}
	
	public File getAppFile() {
		return this.appFile;
	}
	
	public String getWebAppName() {
		return this.webAppName;
	}
	
	public String getWebAppBaseFilePath() {
		return this.webAppBaseFilePath;
	}
	
	public String getWebAppFileName() {
		return this.webAppFileName;
	}
}
