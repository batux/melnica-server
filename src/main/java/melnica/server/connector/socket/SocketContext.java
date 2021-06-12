package melnica.server.connector.socket;

import java.net.Socket;

public class SocketContext {

	private Socket socket;
	private String domain;
	private String hostName;
	
	public SocketContext(Socket socket, String domain, String hostName) {
		this.socket = socket;
		this.domain = domain;
		this.hostName = hostName;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public String getDomain() {
		return this.domain;
	}
	
	public String getHostName() {
		return this.hostName;
	}
}
