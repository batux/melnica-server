package melnica.server.connector.socket;

import java.net.Socket;

public class SocketContext {

	private Socket socket;
	private String domain;
	
	public SocketContext(Socket socket, String domain) {
		this.socket = socket;
		this.domain = domain;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	public String getDomain() {
		return this.domain;
	}
}
