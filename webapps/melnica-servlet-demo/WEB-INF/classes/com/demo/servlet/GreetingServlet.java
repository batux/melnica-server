package com.demo.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class GreetingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GreetingServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.addHeader("Melnica", "Batuhan Düzgün");
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().println("<htm><body><h1>Hello Melnica Server!</h1></body></html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.addHeader("Melnica", "Batuhan Düzgün");
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().println("<htm><body>Hello Melnica Server!</body></html>");
	}
}
