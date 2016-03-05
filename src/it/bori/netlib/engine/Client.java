package it.bori.netlib.engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.SwingUtilities;

public class Client {

	private Socket clientSocket = null;
	private PrintStream os = null;
	private BufferedReader is = null;
	private BufferedReader inputLine = null;
	private boolean closed = false;
	
	public Client (String host, int port) {

		try {
			clientSocket = new Socket(host, port);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			os = new PrintStream(clientSocket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to the host "
							+ host);
		}

		if (clientSocket != null && os != null && is != null) {
			try {

				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						String responseLine;
						try {
							while ((responseLine = is.readLine()) != null) {
								System.out.println(responseLine);
								if (responseLine.indexOf("*** Bye") != -1)
									break;
							}
							closed = true;
						} catch (IOException e) {
							System.err.println("IOException:  " + e);
						}
					}
				});
				while (!closed) {
					os.println(inputLine.readLine().trim());
				}
				
				os.close();
				is.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}
	
}
