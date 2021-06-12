package melnica.server.bootstrap;

import melnica.server.application.MelnicaServer;

public class Bootstrap {

	private static MelnicaServer server;
	
	public static void main(String[] args) {
		
		if(server == null) {
			server = new MelnicaServer();
			server.init();
		}
		server.start();
		System.out.println("Melnica web server is started.");
	}
}
