package it.bori.netlib.engine;

import it.bori.netlib.engine.client.Client;
import it.bori.netlib.engine.server.Server;

public class Application_client {

	public static void main(String[] args) {
			
		Client c = new Client("localhost", 2222);
	}

}
