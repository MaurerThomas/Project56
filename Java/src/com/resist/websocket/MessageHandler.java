package com.resist.websocket;

public interface MessageHandler {
    /**
     * Implement this method to handle messages.
     *
     * @param message The message from the client
     */
    public void handleMessage(Message message);
}
