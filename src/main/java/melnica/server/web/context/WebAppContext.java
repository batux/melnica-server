package melnica.server.web.context;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import melnica.server.LifeCycle;
import melnica.server.classloader.MelnicaClassLoader;
import melnica.server.host.Host;
import melnica.server.host.context.deployer.model.DeployedWebAppResult;
import melnica.server.web.context.model.ServletContext;
import melnica.server.web.context.model.WebXml;
import melnica.server.web.context.xml.assembler.WebXmlAssembler;
import melnica.server.web.context.xml.parser.WebXmlParser;

public class WebAppContext implements LifeCycle {
	
	private static final String webXmlPath = "WEB-INF/web.xml";
	
	private Host host;
	private WebXml webXml;
	private WebXmlParser parser;
	private DeployedWebAppResult result;
	private Map<String, Servlet> servletInstanceMap;
	
	public WebAppContext(Host host, DeployedWebAppResult result) {
		this.host = host;
		this.result = result;
		this.parser = new WebXmlParser(new WebXmlAssembler());
	}
	
	public void init() {
		
		String fullPath = this.result.getAbsoluteWebAppFilePath() + File.separator + webXmlPath;
		try {
			this.webXml = this.parser.parse(fullPath);
		} 
		catch (Exception e) {
			throw new RuntimeException("Web xml parse exception for " + fullPath);
		}
	}

	public void start() {
		
		Map<String, ServletContext> servletContexts = this.webXml.getServletContexts();
		if(servletInstanceMap == null) {
			servletInstanceMap = new ConcurrentHashMap<String, Servlet>(servletContexts.size());
		}
		
		MelnicaClassLoader classLoader = new MelnicaClassLoader(this.result.getApplicationName());
		for(String key : servletContexts.keySet()) {
		
			ServletContext servletContext = servletContexts.get(key);
			Servlet servlet = createServletInstance(servletContext.getClazzAbsolutePath(), classLoader);
			if(servlet == null) {
				continue;
			}
			try {
				servlet.init(null);
			} 
			catch (ServletException e) {
				e.printStackTrace();
			}
			servletInstanceMap.put(servletContext.getUrlPattern(), servlet);
		}
	}
	
	public synchronized Servlet findServlet(String urlPattern) {
		return servletInstanceMap.get(urlPattern);
	}
	
	@SuppressWarnings("deprecation")
	private Servlet createServletInstance(String classFullPackagePath, MelnicaClassLoader classLoader) {
		
		Servlet servlet = null;
		try {
			Class<?> clazz = Class.forName(classFullPackagePath, true, classLoader);
			servlet = (Servlet) clazz.newInstance();
		} 
		catch (Exception e) {
			System.out.println(e.toString());
		} 
		catch (Throwable e) {
			System.out.println(e.toString());
		}
		return servlet;
	}
	
	public WebXml getWebXml() {
		return this.webXml;
	}
	
	public Host getHost() {
		return this.host;
	}
}
