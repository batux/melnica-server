package melnica.server.web.context;

import javax.servlet.Filter;

import melnica.server.web.context.model.FilterContext;

public class MelnicaFilterContext {

	private Filter filter;
	private FilterContext context;
	
	public MelnicaFilterContext(Filter filter, FilterContext context) {
		this.filter = filter;
		this.context = context;
	}
	
	public Filter getFilter() {
		return this.filter;
	}
	
	public FilterContext getFilterContext() {
		return this.context;
	}
}
