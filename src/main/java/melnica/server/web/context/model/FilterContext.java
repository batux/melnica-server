package melnica.server.web.context.model;

public class FilterContext {

	private String name;
	private String clazzAbsolutePath;
	private String urlPattern;
	
	public FilterContext(String name, String clazzAbsolutePath) {
		this.name = name;
		this.clazzAbsolutePath = clazzAbsolutePath;
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
}
