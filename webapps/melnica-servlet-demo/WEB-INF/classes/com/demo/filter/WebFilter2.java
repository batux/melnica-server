package com.demo.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebFilter2 implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		String authValue = req.getHeader("auth");
		if(!"batux".equals(authValue)) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.setContentType("text/html; charset=UTF-8");
			resp.getWriter().println("<htm><body><h1>You need to pass 'auth' value in header!</h1></body></html>");
			return;
		}
		
		resp.addHeader("WebFilter2", "I'm web filter2!");
		System.out.println(">>> WebFilter2 is executed!");
		chain.doFilter(request, response);
	}
}
