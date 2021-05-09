package melnica.server.application.configuration;

public class HostConfiguration {

	private String domain;
	private String rootFolderName;
	private boolean unpackWar;
	
	public HostConfiguration() {}
	
	public HostConfiguration(String domain, String rootFolderName, boolean unpackWar) {
		this.domain = domain;
		this.rootFolderName = rootFolderName;
		this.unpackWar = unpackWar;
	}
	
	public String getDomain() {
		return this.domain;
	}
	
	public String getRootFolderName() {
		return this.rootFolderName;
	}
	
	public boolean isUnpackWar() {
		return this.unpackWar;
	}
}
