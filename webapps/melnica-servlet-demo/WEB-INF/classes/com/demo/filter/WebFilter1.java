package com.demo.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class WebFilter1 implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse resp = (HttpServletResponse) response;
		resp.addHeader("WebFilter1", "I'm web filter1!");
		System.out.println(">>> WebFilter1 is executed!");
		chain.doFilter(request, response);
	}
}
