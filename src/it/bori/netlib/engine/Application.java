package it.bori.netlib.engine;

import it.bori.netlib.engine.server.Server;

public abstract class Application {

	public static void main(String[] args) {
		Server s = new Server(2222);
		s.run();
	}

}
