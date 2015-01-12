package com.resist.websocket;

public class DefaultControlFrameHandler implements MessageHandler {
	/**
	 * Handles default opcodes.
	 * 
	 * @param message The received message
	 */
	@Override
	public void handleMessage(Message message) {
		if(message.getType() == Connection.OPCODE_CONNECTION_CLOSE) {
			message.getConnection().close();
		} else if(message.getType() == Connection.OPCODE_PING) {
			message.getConnection().sendPong(message.toByteArray());
		}
	}
}
