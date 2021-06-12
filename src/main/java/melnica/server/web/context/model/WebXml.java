package melnica.server.web.context.model;

import java.util.Map;

public class WebXml {

	private String webAppDisplayName;
	private Map<String, ServletContext> servletContexts;
	private Map<String, FilterContext> filterContexts;

	public WebXml(String webAppDisplayName, Map<String, ServletContext> servletContextMap, Map<String, FilterContext> filterContextMap) {
		this.webAppDisplayName = webAppDisplayName;
		this.servletContexts = servletContextMap;
		this.filterContexts = filterContextMap;
	}
	
	public String getWebAppDisplayName() {
		return this.webAppDisplayName;
	}
	
	public Map<String, ServletContext> getServletContexts() {
		return this.servletContexts;
	}
	
	public Map<String, FilterContext> getFilterContexts() {
		return this.filterContexts;
	}
}





