package melnica.server.bootstrap;

import melnica.server.application.Melnica;

public class Bootstrap {

	private static Melnica server;
	
	public static void main(String[] args) {
		
		if(server == null) {
			server = new Melnica();
			server.init();
		}
		server.start();
	}
}
