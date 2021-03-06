package melnica.server.connector.socket;

import java.net.Socket;

import melnica.server.connector.http.Bosphorus;
import melnica.server.service.Service;

public class SocketContextProcessor implements Runnable {

	private volatile Service service;
	private volatile Bosphorus connector;
	private volatile Socket socket;
	private volatile String domain;
	
	public SocketContextProcessor(Service service, Bosphorus connector, Socket socket, String domain) {
		this.service = service;
		this.connector = connector;
		this.socket = socket;
		this.domain = domain;
	}
	
	public void run() {
		if(!this.service.execute(new SocketContext(socket, domain))) {
			this.connector.stop();
		}
	}
}
