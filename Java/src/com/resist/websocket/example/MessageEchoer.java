package com.resist.websocket.example;

import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class MessageEchoer implements MessageHandler {

	@Override
	public void handleMessage(Message message) {
		System.out.println("Spam received: "+message);
		if(!message.getConnection().sendMessage(message.toString())) {
			System.out.println("Failed to echo message:");
		}
	}

}
