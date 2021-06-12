package melnica.server.web.context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import melnica.server.LifeCycle;
import melnica.server.classloader.MelnicaClassLoader;
import melnica.server.host.Host;
import melnica.server.host.context.deployer.model.DeployedWebAppResult;
import melnica.server.web.context.model.FilterContext;
import melnica.server.web.context.model.ServletContext;
import melnica.server.web.context.model.WebXml;
import melnica.server.web.context.xml.parser.WebXmlAssembler;
import melnica.server.web.context.xml.parser.WebXmlParser;

public class WebAppContext implements LifeCycle {
	
	private static final String webXmlPath = "WEB-INF/web.xml";
	
	private Host host;
	private WebXml webXml;
	private WebXmlParser parser;
	private DeployedWebAppResult result;
	private Map<String, MelnicaServletContext> servletInstanceMap;
	private Map<String, MelnicaFilterContext> filterInstanceMap;
	
	public WebAppContext(Host host, DeployedWebAppResult result) {
		this.host = host;
		this.result = result;
		this.parser = new WebXmlParser(new WebXmlAssembler());
	}
	
	public void init() {
		
		String fullPath = this.result.getWebApplicationFilePath() + File.separator + webXmlPath;
		try {
			this.webXml = this.parser.parse(fullPath);
		} 
		catch (Exception e) {
			System.out.println("Web xml parse exception: " + fullPath);
		}
	}

	public void start() {
		
		MelnicaClassLoader classLoader = new MelnicaClassLoader(this.result.getWebApplicationName());
		prepareFilterInstances(classLoader);
		prepareServletInstances(classLoader);		
	}
	
	public synchronized MelnicaServletContext findServletContext(String urlPattern) {
		return servletInstanceMap.get(urlPattern);
	}
	
	private void prepareFilterInstances(MelnicaClassLoader classLoader) {
		
		Map<String, FilterContext> filterContexts = this.webXml.getFilterContexts();
		if(filterInstanceMap == null) {
			filterInstanceMap = new ConcurrentHashMap<String, MelnicaFilterContext>(filterContexts.size());
		}

		for(String name : filterContexts.keySet()) {
			
			FilterContext filterContext = filterContexts.get(name);
			Filter filter = createFilterInstance(filterContext.getClazzAbsolutePath(), classLoader);
			if(filter == null) {
				continue;
			}
			try {
				filter.init(null);
				filterInstanceMap.put(filterContext.getName(), new MelnicaFilterContext(filter, filterContext));
			}
			catch (ServletException e) {
				System.out.println("Web application start exception: " + e.getMessage());
			}
		}
	}
	
	private void prepareServletInstances(MelnicaClassLoader classLoader) {
		
		Map<String, ServletContext> servletContexts = this.webXml.getServletContexts();
		if(servletInstanceMap == null) {
			servletInstanceMap = new ConcurrentHashMap<String, MelnicaServletContext>(servletContexts.size());
		}
		
		for(String name : servletContexts.keySet()) {
		
			ServletContext servletContext = servletContexts.get(name);
			Servlet servlet = createServletInstance(servletContext.getClazzAbsolutePath(), classLoader);
			if(servlet == null) {
				continue;
			}
			try {
				servlet.init(null);
				
				MelnicaServletContext context = new MelnicaServletContext(servlet, servletContext);
				context.createFilterChains(selectWebFilterContextsForServlet(servletContext));
				servletInstanceMap.put(servletContext.getUrlPattern(), context);
			} 
			catch (ServletException e) {
				System.out.println("Web application start exception: " + e.getMessage());
			}
		}
	}
	
	private List<MelnicaFilterContext> selectWebFilterContextsForServlet(ServletContext servletContext) {
		
		List<MelnicaFilterContext> webFilterContexts = new ArrayList<MelnicaFilterContext>();
		List<String> chainedWebFilterNames = servletContext.getChainedWebFilterNames();
		for(String webFilterName : chainedWebFilterNames) {
			MelnicaFilterContext filterContext = filterInstanceMap.get(webFilterName);
			webFilterContexts.add(filterContext);
		}
		return webFilterContexts;
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
	
	@SuppressWarnings("deprecation")
	private Filter createFilterInstance(String classFullPackagePath, MelnicaClassLoader classLoader) {
		
		Filter filter = null;
		try {
			Class<?> clazz = Class.forName(classFullPackagePath, true, classLoader);
			filter = (Filter) clazz.newInstance();
		} 
		catch (Exception e) {
			System.out.println(e.toString());
		} 
		catch (Throwable e) {
			System.out.println(e.toString());
		}
		return filter;
	}
	
	public WebXml getWebXml() {
		return this.webXml;
	}
	
	public Host getHost() {
		return this.host;
	}
}
