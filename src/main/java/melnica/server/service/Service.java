package melnica.server.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import melnica.server.LifeCycle;
import melnica.server.application.MelnicaServer;
import melnica.server.application.configuration.BosphorusConfiguration;
import melnica.server.application.configuration.HostConfiguration;
import melnica.server.application.configuration.ServiceConfiguration;
import melnica.server.connector.http.Bosphorus;
import melnica.server.connector.http.request.HttpRequest;
import melnica.server.connector.http.request.HttpRequestParser;
import melnica.server.connector.http.response.HttpResponse;
import melnica.server.connector.socket.SocketContext;
import melnica.server.connector.socket.SocketInputStream;
import melnica.server.host.Host;
import melnica.server.web.context.MelnicaFilterChain;
import melnica.server.web.context.MelnicaServletContext;
import melnica.server.web.context.WebAppContext;

public class Service implements LifeCycle {

	private String name;
	private MelnicaServer server;
	private ServiceConfiguration configuration;
	
	private Map<String, Host> hosts;
	private Map<String, Bosphorus> connectors;
	
	public Service(String name, MelnicaServer server) {
		this.name = name;
		this.server = server;
		this.configuration = this.server.getConfiguration().getServices().get(this.name);
		this.hosts = new ConcurrentHashMap<String, Host>(this.configuration.getHosts().size());
		this.connectors = new ConcurrentHashMap<String, Bosphorus>(this.configuration.getConnectors().size());
	}
	
	public void init() {
		
		try {
			initHosts(this.configuration.getHosts());
			initConnectors(this.configuration.getConnectors());
			System.out.println("Service - " + name + " is initialized.");
		}
		catch(Exception e) {
			System.out.println("Service init exception" + e.getMessage());
		}
	}

	public void start() {
		
		for(String hostName : hosts.keySet()) {
			Host host = hosts.get(hostName);
			host.start();
		}
		
		for(final String connectorName : connectors.keySet()) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					Bosphorus connector = connectors.get(connectorName);
					connector.start();
				}
			});
			thread.start();
		}
		System.out.println("Service - " + name + " is started.");
	}
	
	public boolean execute(SocketContext socketContext) {
		
		try {
			SocketInputStream input = new SocketInputStream(socketContext.getSocket().getInputStream(), 2048);
			OutputStream output = socketContext.getSocket().getOutputStream();

			HttpRequest request = new HttpRequest(input);
			HttpResponse response = new HttpResponse(output);
			response.setRequest(request);

			HttpRequestParser.parseBody(input, request, output);
			HttpRequestParser.parseHeaders(input, request);
			
			if(request.getRequestURI().contains("favicon.ico")) {
				response.sendFavicon();
				return true;
			}

			WebAppContext context = findWebApplication(request.getRequestURI());
			if(context != null) {
				
				if(!request.getRequestURI().contains("/servlet")) {
					response.sendStaticResource2();
					return true;
				}
				
				int index1 = request.getRequestURI().indexOf("/");
				int index2 = request.getRequestURI().indexOf("/", index1 + 1);
				if(index1 < 0 || index2 < 0) {
					return true;
				}
				
				MelnicaServletContext servletContext = context.findServletContext(request.getRequestURI().substring(index2));
				if(servletContext != null) {
					MelnicaFilterChain headFilterChain = servletContext.getHeadFilterChain();
					if(headFilterChain != null) {
						headFilterChain.doFilter(request, response);
					}
				}
			}
			response.finishResponse();
		} 
		catch (Exception e) {
			System.out.println("Servlet execution exception: " + e.getMessage());
		}
		finally {
			try {
				socketContext.getSocket().close();
			} 
			catch (IOException e) {
				System.out.println("Socket close exception: " + e.getMessage());
			}
		}
		return true;
	}
	
	public MelnicaServer getServer() {
		return this.server;
	}
	
	public List<Host> getHosts() {
		return new ArrayList<Host>(this.hosts.values());
	}
	
	public ServiceConfiguration getConfiguration() {
		return this.configuration;
	}
	
	private void initHosts(List<HostConfiguration> hostConfigurationList) {
		
		for(HostConfiguration configuration : hostConfigurationList) {
			Host host = new Host(this, configuration);
			host.init();
			this.hosts.put(host.getName(), host);
		}
	}
	
	private void initConnectors(List<BosphorusConfiguration> connectorConfigurationList) {
		
		for(BosphorusConfiguration configuration : connectorConfigurationList) {
			Bosphorus connector = new Bosphorus(this, configuration);
			connector.init();
			this.connectors.put(connector.getName(), connector);
		}
	}
	
	private synchronized WebAppContext findWebApplication(String requestUri) {
		
		int index1 = requestUri.indexOf("/");
		int index2 = requestUri.indexOf("/", index1 + 1);
		if(index1 < 0 || index2 < 0) {
			return null;
		}
		
		for(Host host : this.hosts.values()) {
			WebAppContext selectedWebApplication = host.findWebAppContext(requestUri.substring(index1 + 1, index2));
			if(selectedWebApplication != null) {
				return selectedWebApplication;
			}
		}
		return null;
	}
}
