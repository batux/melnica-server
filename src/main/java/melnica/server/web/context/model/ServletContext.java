package melnica.server.web.context.model;

import java.util.LinkedList;
import java.util.List;

public class ServletContext {

	private String name;
	private String clazzAbsolutePath;
	private String urlPattern;
	private int loadOnStartup;
	private List<String> chainedWebFilterNames;
	
	public ServletContext(String name, String clazzAbsolutePath, int loadOnStartup) {
		this.name = name;
		this.clazzAbsolutePath = clazzAbsolutePath;
		this.loadOnStartup = loadOnStartup;
		this.chainedWebFilterNames = new LinkedList<String>();
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
	
	public void add(String webFilterName) {
		this.chainedWebFilterNames.add(webFilterName);
	}
	
	public List<String> getChainedWebFilterNames() {
		return this.chainedWebFilterNames;
	}
}