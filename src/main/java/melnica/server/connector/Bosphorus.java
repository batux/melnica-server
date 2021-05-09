package melnica.server.connector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import melnica.server.LifeCycle;
import melnica.server.application.configuration.BosphorusConfiguration;
import melnica.server.connector.socket.SocketProcess;
import melnica.server.host.Host;
import melnica.server.service.Service;

public class Bosphorus implements LifeCycle {

	private String name;
	private Service service;
	private ExecutorService executor;
	private BosphorusConfiguration configuration;
	private boolean shutdown = false;
	private String shutdownCommand;

	public Bosphorus(Service service, BosphorusConfiguration configuration) {
		this.service = service;
		this.configuration = configuration;
		this.name = produceName();
		this.shutdownCommand = this.service.getServer().getConfiguration().getShutdownCommand();
	}

	public void init() {
		this.executor = Executors.newFixedThreadPool(20);

	}

	public void start() {
		List<Host> hosts = this.service.getHosts();
		for (Host host : hosts) {
			listen(host.getDomain());
		}
	}

	public void listen(final String domain) {

		int port = this.configuration.getPort();

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName(domain));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		while (!shutdown) {
			try {
				Socket socket = serverSocket.accept();
				this.executor.execute(new SocketProcess(this.service, this, socket, domain));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.executor.shutdown();
	}

	public synchronized void stop() {
		this.shutdown = true;
	}

	public String getName() {
		return this.name;
	}

	public String getShutdownCommand() {
		return this.shutdownCommand;
	}

	private String produceName() {

		StringBuilder builder = new StringBuilder();
		builder.append(this.configuration.getPort());
		builder.append("_");
		builder.append(this.configuration.getProtocol());
		return builder.toString();
	}
}
