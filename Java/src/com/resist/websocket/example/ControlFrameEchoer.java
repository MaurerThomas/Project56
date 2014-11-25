package com.resist.websocket.example;

import com.resist.websocket.Message;
import com.resist.websocket.MessageHandler;

public class ControlFrameEchoer implements MessageHandler {

	@Override
	public void handleMessage(Message message) {
		System.out.println("Control frame: "+message.getType()+"	Message: "+message);
	}

}
