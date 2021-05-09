package melnica.server.web.context.model;

public class ServletContext {

	private String name;
	private String clazzAbsolutePath;
	private String urlPattern;
	private int loadOnStartup;
	
	public ServletContext(String name, String clazzAbsolutePath, int loadOnStartup) {
		this.name = name;
		this.clazzAbsolutePath = clazzAbsolutePath;
		this.loadOnStartup = loadOnStartup;
	}
	
	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
	
	public String getUrlPattern() {
		return this.urlPattern;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getClazzAbsolutePath() {
		return this.clazzAbsolutePath;
	}
	
	public int getLoadOnStartup() {
		return this.loadOnStartup;
	}
}