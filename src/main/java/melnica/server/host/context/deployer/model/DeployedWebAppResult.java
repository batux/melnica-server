package melnica.server.host.context.deployer.model;

public class DeployedWebAppResult {

	private boolean success;
	private String webApplicationName;
	private String webApplicationFilePath;
	
	public DeployedWebAppResult(String webApplicationName, String webApplicationFilePath, boolean success) {
		this.webApplicationName = webApplicationName;
		this.webApplicationFilePath = webApplicationFilePath;
		this.success = success;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public String getWebApplicationName() {
		return this.webApplicationName;
	}
	
	public String getWebApplicationFilePath() {
		return this.webApplicationFilePath;
	}
}
