package melnica.server.web.context;

import java.util.List;

import javax.servlet.Servlet;

import melnica.server.web.context.model.ServletContext;

public class MelnicaServletContext {

	private Servlet servlet;
	private ServletContext context;
	private MelnicaFilterChain head;
	
	public MelnicaServletContext(Servlet servlet, ServletContext context) {
		this.servlet = servlet;
		this.context = context;
	}
	
	public Servlet getServlet() {
		return this.servlet;
	}
	
	public ServletContext getServletContext() {
		return this.context;
	}
	
	public MelnicaFilterChain getHeadFilterChain() {
		return this.head;
	}
	
	public void createFilterChains(List<MelnicaFilterContext> webFilterContexts) {
		
		if(webFilterContexts == null || webFilterContexts.size() == 0) {
			head = new MelnicaFilterChain(this, null);
			return;
		}
		
		if(webFilterContexts.size() == 1) {
			head = new MelnicaFilterChain(this, webFilterContexts.get(0));
			return;
		}
		
		// TODO: filtrenin son next chain'ini eklemen lazım yoksa servlet execute edilemiyor!
		
		MelnicaFilterContext currentWebFilterContext = webFilterContexts.get(0);
		MelnicaFilterChain currentFilterChain = new MelnicaFilterChain(this, currentWebFilterContext);
		head = currentFilterChain;
		
		int itemSize = webFilterContexts.size();
		for(int i = 0; i < itemSize; i++) {
			
			MelnicaFilterChain nextFilterChain = null;
			if((i + 1) < itemSize) {
				MelnicaFilterContext nextWebFilterContext = webFilterContexts.get(i + 1);
				nextFilterChain = new MelnicaFilterChain(this, nextWebFilterContext);
			}
			else {
				nextFilterChain = new MelnicaFilterChain(this, null);
			}
			currentFilterChain.setNext(nextFilterChain);
			currentFilterChain = nextFilterChain;
		}
	}
}
