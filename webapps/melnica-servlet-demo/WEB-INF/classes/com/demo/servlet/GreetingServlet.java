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
		response.getWriter().println("<htm><body><h1>Hello Melnica Server!</h1><img src=\"/melnica-servlet-demo/melnica_server_logo.png\" width=\"200px\" height=\"100px\" /></body></html>");
		System.out.println(">>> GreetingServlet is executed! (HTTP GET)");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.addHeader("Melnica", "Batuhan Düzgün");
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().println("<htm><body><h1>Hello Melnica Server!</h1><img src=\"/melnica-servlet-demo/melnica_server_logo.png\" width=\"200px\" height=\"100px\" /></body></html>");
		System.out.println(">>> GreetingServlet is executed! (HTTP POST)");
	}
}
