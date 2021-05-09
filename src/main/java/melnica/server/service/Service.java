package melnica.server.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import melnica.server.LifeCycle;
import melnica.server.application.Melnica;
import melnica.server.application.configuration.BosphorusConfiguration;
import melnica.server.application.configuration.HostConfiguration;
import melnica.server.application.configuration.ServiceConfiguration;
import melnica.server.connector.Bosphorus;
import melnica.server.connector.model.HttpHeader;
import melnica.server.connector.model.HttpRequest;
import melnica.server.connector.model.HttpRequestLine;
import melnica.server.connector.model.HttpResponse;
import melnica.server.connector.model.RequestUtil;
import melnica.server.connector.socket.SocketContext;
import melnica.server.connector.socket.SocketInputStream;
import melnica.server.host.Host;
import melnica.server.web.context.WebAppContext;

public class Service implements LifeCycle {

	private String name;
	private Melnica server;
	private ServiceConfiguration configuration;
	
	private Map<String, Host> hosts;
	private Map<String, Bosphorus> connectors;
	
	public Service(String name, Melnica server) {
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
		}
		catch(Exception e) {
			throw new RuntimeException("Service init exception" + e.getMessage());
		}
	}

	public void start() {
		
		for(String key : hosts.keySet()) {
			Host host = hosts.get(key);
			host.start();
		}
		
		for(String key : connectors.keySet()) {
			Bosphorus connector = connectors.get(key);
			connector.start();
		}
	}
	
	public boolean process(SocketContext socketContext) {
		
		String domain = socketContext.getDomain();
		Host host = this.hosts.get(domain);
		if(host == null) {
			return true;
		}
		
		try {
			SocketInputStream input = new SocketInputStream(socketContext.getSocket().getInputStream(), 2048);
			OutputStream output = socketContext.getSocket().getOutputStream();

			HttpRequest request = new HttpRequest(input);
			HttpResponse response = new HttpResponse(output);
			response.setRequest(request);

			parseHttpRequest(input, request, output);
			parseHttpHeaders(input, request);

			WebAppContext app = findWebApp(request.getRequestURI());
			if(app != null) {
				int index1 = request.getRequestURI().indexOf("/");
				int index2 = request.getRequestURI().indexOf("/", index1 + 1);
				if(index1 < 0 || index2 < 0) {
					return true;
				}
				Servlet servlet = app.findServlet(request.getRequestURI().substring(index2));
				if(servlet != null) {
					servlet.service(request, response);
				}
			}
			((HttpResponse) response).finishResponse();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				socketContext.getSocket().close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	private void initHosts(List<HostConfiguration> hostConfList) {
		
		for(HostConfiguration configuration : hostConfList) {
			Host host = new Host(this, configuration);
			host.init();
			this.hosts.put(host.getName(), host);
		}
	}
	
	private void initConnectors(List<BosphorusConfiguration> connectorConfList) {
		
		for(BosphorusConfiguration configuration : connectorConfList) {
			Bosphorus connector = new Bosphorus(this, configuration);
			connector.init();
			this.connectors.put(connector.getName(), connector);
		}
	}
	
	public synchronized WebAppContext findWebApp(String requestUri) {
		
		int index1 = requestUri.indexOf("/");
		int index2 = requestUri.indexOf("/", index1 + 1);
		if(index1 < 0 || index2 < 0) {
			return null;
		}
		
		for(Host host : this.hosts.values()) {
			WebAppContext app = host.findWebAppContext(requestUri.substring(index1 + 1, index2));
			if(app != null) {
				return app;
			}
		}
		return null;
	}
	
	public Melnica getServer() {
		return this.server;
	}
	
	public List<Host> getHosts() {
		return new ArrayList<Host>(this.hosts.values());
	}
	
	public ServiceConfiguration getConfiguration() {
		return this.configuration;
	}
	
	private void parseHttpHeaders(SocketInputStream input, HttpRequest request) throws IOException, ServletException {
		while (true) {
			HttpHeader header = new HttpHeader();

			input.readHeader(header);
			if (header.nameEnd == 0) {
				if (header.valueEnd == 0) {
					return;
				} else {
					throw new ServletException("httpProcessor.parseHeaders.colon");
				}
			}

			String name = new String(header.name, 0, header.nameEnd);
			String value = new String(header.value, 0, header.valueEnd);
			request.addHeader(name, value);
			if (name.equals("cookie")) {
				Cookie cookies[] = RequestUtil.parseCookieHeader(value);
				for (int i = 0; i < cookies.length; i++) {
					if (cookies[i].getName().equals("jsessionid")) {
						if (!request.isRequestedSessionIdFromCookie()) {
							request.setRequestedSessionId(cookies[i].getValue());
							request.setRequestedSessionCookie(true);
							request.setRequestedSessionURL(false);
						}
					}
					request.addCookie(cookies[i]);
				}
			} else if (name.equals("content-length")) {
				int n = -1;
				try {
					n = Integer.parseInt(value);
				} catch (Exception e) {
					throw new ServletException("httpProcessor.parseHeaders.contentLength");
				}
				request.setContentLength(n);
			} else if (name.equals("content-type")) {
				request.setContentType(value);
			}
		}
	}

	private void parseHttpRequest(SocketInputStream input, HttpRequest request, OutputStream output) throws IOException, ServletException {

		HttpRequestLine requestLine = new HttpRequestLine();
		input.readRequestLine(requestLine);
		
		String method = new String(requestLine.method, 0, requestLine.methodEnd);
		String uri = null;
		String protocol = new String(requestLine.protocol, 0, requestLine.protocolEnd);

		if (method.length() < 1) {
			throw new ServletException("Missing HTTP request method");
		} else if (requestLine.uriEnd < 1) {
			throw new ServletException("Missing HTTP request URI");
		}
		int question = requestLine.indexOf("?");
		if (question >= 0) {
			request.setQueryString(new String(requestLine.uri, question + 1, requestLine.uriEnd - question - 1));
			uri = new String(requestLine.uri, 0, question);
		} else {
			request.setQueryString(null);
			uri = new String(requestLine.uri, 0, requestLine.uriEnd);
		}

		if (!uri.startsWith("/")) {
			int pos = uri.indexOf("://");
			if (pos != -1) {
				pos = uri.indexOf('/', pos + 3);
				if (pos == -1) {
					uri = "";
				} else {
					uri = uri.substring(pos);
				}
			}
		}

		String match = ";jsessionid=";
		int semicolon = uri.indexOf(match);
		if (semicolon >= 0) {
			String rest = uri.substring(semicolon + match.length());
			int semicolon2 = rest.indexOf(';');
			if (semicolon2 >= 0) {
				request.setRequestedSessionId(rest.substring(0, semicolon2));
				rest = rest.substring(semicolon2);
			} else {
				request.setRequestedSessionId(rest);
				rest = "";
			}
			request.setRequestedSessionURL(true);
			uri = uri.substring(0, semicolon) + rest;
		} else {
			request.setRequestedSessionId(null);
			request.setRequestedSessionURL(false);
		}

		String normalizedUri = normalize(uri);
		((HttpRequest) request).setMethod(method);
		request.setProtocol(protocol);
		if (normalizedUri != null) {
			((HttpRequest) request).setRequestURI(normalizedUri);
		} else {
			((HttpRequest) request).setRequestURI(uri);
		}

		if (normalizedUri == null) {
			throw new ServletException("Invalid URI: " + uri + "'");
		}
	}

	protected String normalize(String path) {
		if (path == null)
			return null;
		// Create a place for the normalized path
		String normalized = path;

		// Normalize "/%7E" and "/%7e" at the beginning to "/~"
		if (normalized.startsWith("/%7E") || normalized.startsWith("/%7e"))
			normalized = "/~" + normalized.substring(4);

		// Prevent encoding '%', '/', '.' and '\', which are special reserved
		// characters
		if ((normalized.indexOf("%25") >= 0) || (normalized.indexOf("%2F") >= 0) || (normalized.indexOf("%2E") >= 0)
				|| (normalized.indexOf("%5C") >= 0) || (normalized.indexOf("%2f") >= 0)
				|| (normalized.indexOf("%2e") >= 0) || (normalized.indexOf("%5c") >= 0)) {
			return null;
		}

		if (normalized.equals("/."))
			return "/";

		// Normalize the slashes and add leading slash if necessary
		if (normalized.indexOf('\\') >= 0)
			normalized = normalized.replace('\\', '/');
		if (!normalized.startsWith("/"))
			normalized = "/" + normalized;

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) + normalized.substring(index + 1);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) + normalized.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0)
				break;
			if (index == 0)
				return (null); // Trying to go outside our context
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
		}

		// Declare occurrences of "/..." (three or more dots) to be invalid
		// (on some Windows platforms this walks the directory tree!!!)
		if (normalized.indexOf("/...") >= 0)
			return (null);

		// Return the normalized path that we have completed
		return (normalized);

	}
}
