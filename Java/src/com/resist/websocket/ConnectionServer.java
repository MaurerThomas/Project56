package com.resist.websocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public final class ConnectionServer {
	private String address;
	private int port;
	private String path;
	private boolean running = true;
	private MessageHandler controlFrameHandler = null;
	private MessageHandler messageHandler = null;

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
			}).run();
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
			}).run();
		}
	}

	/**
	 * Stops the server from accepting new connections.
	 */
	public void stop() {
		running = false;
	}

	/**
	 * Listens for new connections and starts new threads.
	 */
	public void manageConnections() {
		try {
			ServerSocket socket = new ServerSocket(port);
			while(running) {
				Socket client = socket.accept();
				try {
					Connection conn = new Connection(this,client);
					new Thread(conn).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}