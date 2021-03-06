package it.bori.netlib.engine;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class Server {
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private int maxClientsCount;
	private ClientThread[] threads;
	private int port;

	public void setPort(int p) {
		this.port = p;
	}

	public int getPort() {
		return this.port;
	}

	public void setMaxClient(int mcc) {
		this.maxClientsCount = mcc;
	}

	public int getMaxClient() {
		return this.maxClientsCount;
	}

	public Server(int port, int maxClient) {
		setPort(port);
		setMaxClient(maxClient);
		threads = new ClientThread[getMaxClient()];
		try {
			serverSocket = new ServerSocket(getPort());
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					while (true) {
						try {
							clientSocket = serverSocket.accept();
							int i = 0;
							for (i = 0; i < maxClientsCount; i++) {
								if (threads[i] == null) {
									(threads[i] = new ClientThread(
											clientSocket, threads)).start();
									System.out.println("new user");
									break;
								}
							}
							if (i >= maxClientsCount) {
								PrintStream os = new PrintStream(clientSocket
										.getOutputStream());
								os.println("Server too busy. Try later.");
								os.close();
								clientSocket.close();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
