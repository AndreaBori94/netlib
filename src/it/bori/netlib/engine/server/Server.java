package it.bori.netlib.engine.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class Server {
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private final int maxClientsCount = 9999;
	private final clientThread[] threads = new clientThread[maxClientsCount];
	private int port;

	public Server(int port) {
		this.port = port;
		try {
			serverSocket = new ServerSocket(this.port);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public void run() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						clientSocket = serverSocket.accept();
						int i = 0;
						for (i = 0; i < maxClientsCount; i++) {
							if (threads[i] == null) {
								(threads[i] = new clientThread(clientSocket,
										threads)).start();
								break;
							}
						}
						if (i == maxClientsCount) {
							PrintStream os = new PrintStream(clientSocket
									.getOutputStream());
							os.println("Server too busy. Try later.");
							os.close();
							clientSocket.close();
						}
					} catch (IOException e) {
						System.out.println(e);
					}
				}
			}
		});

	}

}
