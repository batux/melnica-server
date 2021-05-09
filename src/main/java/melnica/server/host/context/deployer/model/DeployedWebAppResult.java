package melnica.server.host.context.deployer.model;

public class DeployedWebAppResult {

	private boolean success;
	private String applicationName;
	private String absoluteWebAppFilePath;
	
	public DeployedWebAppResult(String applicationName, String absoluteWebAppFilePath, boolean success) {
		this.applicationName = applicationName;
		this.absoluteWebAppFilePath = absoluteWebAppFilePath;
		this.success = success;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public String getApplicationName() {
		return this.applicationName;
	}
	
	public String getAbsoluteWebAppFilePath() {
		return this.absoluteWebAppFilePath;
	}
}
