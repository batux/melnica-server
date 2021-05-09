package melnica.server.web.context.model;

import java.util.Map;

public class WebXml {

	private String webAppDisplayName;
	private Map<String, ServletContext> servletContexts;

	public WebXml(String webAppDisplayName, Map<String, ServletContext> servletContextMap) {
		this.webAppDisplayName = webAppDisplayName;
		this.servletContexts = servletContextMap;
	}
	
	public String getWebAppDisplayName() {
		return this.webAppDisplayName;
	}
	
	public Map<String, ServletContext> getServletContexts() {
		return this.servletContexts;
	}
}





