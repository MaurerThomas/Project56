package com.resist.websocket.example;

import com.resist.websocket.ConnectionServer;

public class Server {

	public static void main(String[] args) {
		new ConnectionServer("145.24.222.119",8080,"/search")
			.setMessageHandler(new MessageEchoer())
			.setControlFrameHandler(new ControlFrameEchoer())
			.manageConnections();
	}

}
