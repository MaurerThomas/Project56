package com.resist.websocket.example;

import java.io.IOException;

import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class MessageEchoer implements MessageHandler {

	@Override
	public void handleMessage(Message message) {
		System.out.println("Spam received: "+message);
		try {
			message.getConnection().sendMessage(message.toString());
		} catch (IOException e) {
			System.out.println("Failed to echo message:");
			e.printStackTrace();
		}
	}

}
