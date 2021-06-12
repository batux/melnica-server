package melnica.server.web.context;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import melnica.server.web.context.model.FilterContext;
import melnica.server.web.context.model.ServletContext;

public class MelnicaFilterChain implements FilterChain {

	private MelnicaServletContext servletContext;
	private MelnicaFilterContext filterContext;
	private FilterChain nextChain;
	
	public MelnicaFilterChain(MelnicaServletContext servletContext, MelnicaFilterContext filterContext) {
		this.servletContext = servletContext;
		this.filterContext = filterContext;
	}
	
	public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
	{
		if(servletContext == null || 
				servletContext.getServlet() == null || 
				servletContext.getServletContext() == null) {
			return;
		}
		
		if(canExecuteNextFilterChain()) {
			if(canCallFilter(this.filterContext, this.servletContext)) {
				Filter filter = this.filterContext.getFilter();
				filter.doFilter(request, response, this.nextChain);
			}
			else {
				if(this.nextChain == null) {
					servletContext.getServlet().service(request, response);
				}
				else {
					this.nextChain.doFilter(request, response);
				}
			}
		}
		else {
			servletContext.getServlet().service(request, response);
		}
	}
	
	private boolean canCallFilter(MelnicaFilterContext filterContext, MelnicaServletContext servletContext) {
		
		Filter filter = this.filterContext.getFilter();
		FilterContext filterConfig = filterContext.getFilterContext();
		
		if(filter == null || filterConfig == null) {
			return false;
		}
		
		ServletContext servletConfig = servletContext.getServletContext();

		String servletUrlPattern = servletConfig.getUrlPattern();
		String filterUrlPattern = filterConfig.getUrlPattern();
		
		return (servletUrlPattern != null && filterUrlPattern != null) &&
				(servletUrlPattern.equals(filterUrlPattern) || "/*".equals(filterUrlPattern));
	}
	
	private boolean canExecuteNextFilterChain() {
		return this.nextChain != null && this.filterContext != null || 
				(this.nextChain == null && this.filterContext != null);
	}
	
	public void setNext(FilterChain nextChain) {
		this.nextChain = nextChain;
	}
}
