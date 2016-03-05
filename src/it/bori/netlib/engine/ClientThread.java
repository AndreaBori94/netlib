package it.bori.netlib.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {

	private String clientName = null;
	private BufferedReader is = null;
	private PrintStream os = null;
	private Socket clientSocket = null;
	private final ClientThread[] threads;
	private int maxClientsCount;

	public ClientThread(Socket clientSocket, ClientThread[] threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		maxClientsCount = threads.length;
	}

	public void run() {
		int maxClientsCount = this.maxClientsCount;
		ClientThread[] threads = this.threads;

		try {
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			os = new PrintStream(clientSocket.getOutputStream());
			String name;
			while (true) {
				os.println("Enter your name.");
				name = is.readLine().trim();
				if (name.indexOf('@') == -1) {
					break;
				} else {
					os.println("The name should not contain '@' character.");
				}
			}

			os.println("Welcome " + name
					+ " to our chat room.\nTo leave enter /quit in a new line.");
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] == this) {
						clientName = "@" + name;
						break;
					}
				}
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this) {
						threads[i].os.println("*** A new user " + name
								+ " entered the chat room !!! ***");
					}
				}
			}
			while (true) {
				String line = is.readLine();
				if (line.startsWith("/quit")) {
					break;
				}
				if (line.startsWith("@")) {
					String[] words = line.split("\\s", 2);
					if (words.length > 1 && words[1] != null) {
						words[1] = words[1].trim();
						if (!words[1].isEmpty()) {
							synchronized (this) {
								for (int i = 0; i < maxClientsCount; i++) {
									if (threads[i] != null
											&& threads[i] != this
											&& threads[i].clientName != null
											&& threads[i].clientName
													.equals(words[0])) {
										threads[i].os.println("<" + name + "> "
												+ words[1]);
										
										this.os.println(">" + name + "> "
												+ words[1]);
										break;
									}
								}
							}
						}
					}
				} else {
					synchronized (this) {
						for (int i = 0; i < maxClientsCount; i++) {
							if (threads[i] != null
									&& threads[i].clientName != null) {
								threads[i].os.println("<" + name + "> " + line);
							}
						}
					}
				}
			}
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] != null && threads[i] != this
							&& threads[i].clientName != null) {
						threads[i].os.println("*** The user " + name
								+ " is leaving the chat room !!! ***");
					}
				}
			}
			os.println("*** Bye " + name + " ***");

			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}
			is.close();
			os.close();
			clientSocket.close();
		} catch (IOException e) {
		}
	}
}
