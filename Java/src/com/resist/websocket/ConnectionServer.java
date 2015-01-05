package com.resist.websocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public final class ConnectionServer {
	private String address;
	private int port;
	private String path;
	private boolean running = true;
	private MessageHandler controlFrameHandler = null;
	private MessageHandler messageHandler = null;
	private int timeout = 1000*60*60;
	private ServerSocket serverSocket;

	/**
	 * Creates a new WebSocket server.
	 * 
	 * @param address The address of the server
	 * @param port The port to listen on
	 * @param path The path to accept clients on
	 */
	public ConnectionServer(String address, int port, String path) {
		this.address = address;
		this.port = port;
		this.path = path;
	}

	/**
	 * Sets a handler to call when the client sends a control frame.
	 * 
	 * @param controlFrameHandler The handler to use
	 * @return The server, for chaining
	 */
	public ConnectionServer setControlFrameHandler(MessageHandler controlFrameHandler) {
		this.controlFrameHandler = controlFrameHandler;
		return this;
	}

	/**
	 * Sets a handler to call when the client sends a completed message
	 * 
	 * @param messageHandler The handler to use
	 * @return The server, for chaining
	 */
	public ConnectionServer setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
		return this;
	}

	/**
	 * Sets the read timeout to be used for connections.
	 * 
	 * @param timeout The timeout in milliseconds (0 is no timeout)
	 * @return The server, for chaining
	 */
	public ConnectionServer setTimeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	/**
	 * Returns the address of the server.
	 * 
	 * @return The address of the server
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * Returns the port of the server.
	 * 
	 * @return The port of the server
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns the path of the server.
	 * 
	 * @return The path of the server
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Handles a control frame, running it in a new thread.
	 * 
	 * @param message The message sent with the frame
	 */
	protected void handleControlFrame(final Message message) {
		if(controlFrameHandler != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					controlFrameHandler.handleMessage(message);
				}
			}).start();
		}
	}

	/**
	 * Handles a completed message, running it in a new thread.
	 * 
	 * @param message The completed message.
	 */
	protected void handleMessage(final Message message) {
		if(messageHandler != null) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					messageHandler.handleMessage(message);
				}
			}).start();
		}
	}

	/**
	 * Stops the server from accepting new connections.
	 */
	public void stop() {
		running = false;
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Listens for new connections and starts new threads.
	 */
	public void manageConnections() {
		try {
			createSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a socket and checks for connections.
	 * 
	 * @throws IOException
	 */
	private void createSocket() throws IOException {
		serverSocket = new ServerSocket(port);
		while(running) {
			createConnections();
		}
		serverSocket.close();
	}

	/**
	 * Listens for connections on a socket.
	 * 
	 * @param socket The socket to accept connections on
	 * @throws IOException
	 */
	private void createConnections() throws IOException {
		Connection conn = null;
		Socket client = accept();
		if(client != null) {
			client.setSoTimeout(timeout);
			try {
				conn = new Connection(this,client);
				new Thread(conn).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Accepts connections to read from.
	 * 
	 * @return A connection to read from
	 * @throws IOException
	 */
	private Socket accept() throws IOException {
		try {
			return serverSocket.accept();
		} catch(SocketException e) {
			return null;
		}
	}
}