package melnica.server.host.context.deployer.model;

import java.io.File;

public class DeployerContext {

	private File webApplicationFile;
	private String webApplicationName;
	private String webApplicationFilePath;
	private String webApplicationsFolderPath;
	
	public DeployerContext(File webApplicationFile, String webApplicationFilePath, String webApplicationsFolderPath) {
		this.webApplicationFile = webApplicationFile;
		this.webApplicationFilePath = webApplicationFilePath;
		this.webApplicationName = this.webApplicationFilePath.replace(".war", "");
		this.webApplicationsFolderPath = webApplicationsFolderPath;
	}
	
	public File getWebApplicationFile() {
		return this.webApplicationFile;
	}
	
	public String getWebApplicationName() {
		return this.webApplicationName;
	}
	
	public String getWebApplicationsFolderPath() {
		return this.webApplicationsFolderPath;
	}
	
	public String getWebApplicationFilePath() {
		return this.webApplicationFilePath;
	}
}
