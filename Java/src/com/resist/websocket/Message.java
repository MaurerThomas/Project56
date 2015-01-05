package com.resist.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Message {
	private Connection connection;
	private int type;
	private ByteArrayOutputStream buffer;
	private StringBuilder stringBuffer;
	private boolean complete = false;
	private String message = null;

	/**
	 * Creates a new message.
	 * 
	 * @param connection The connection that created the message.
	 * @param type The type of the message. One of Connection.OPCODE_*
	 */
	public Message(Connection connection, int type) {
		this.connection = connection;
		this.type = type;
		if(type == Connection.OPCODE_TEXT_FRAME) {
			stringBuffer = new StringBuilder();
		} else {
			buffer = new ByteArrayOutputStream();
		}
	}

	/**
	 * Checks whether this is a complete message.
	 * 
	 * @return True if this is a complete message.
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Sets the message to complete.
	 */
	public void complete() {
		complete = true;
		if(type == Connection.OPCODE_TEXT_FRAME) {
			message = stringBuffer.toString();
		}
	}

	/**
	 * Returns the type of the message.
	 * 
	 * @return One of Connection.OPCODE_*
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns the connection that created this message.
	 * 
	 * @return The connection this message come from.
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Adds data to this message.
	 * 
	 * @param input The input stream to read from.
	 * @param length The number of bytes to read.
	 * @param mask The byte mask to apply to the message.
	 * @throws IOException
	 */
	public void add(InputStream input, long length, int[] mask) throws IOException {
		if(complete) {
			return;
		}
		for(long n=0;n < length;n++) {
			int currentByte = input.read()^mask[(int)(n%4)];
			if(type == Connection.OPCODE_TEXT_FRAME) {
				stringBuffer.appendCodePoint(currentByte);
			} else {
				buffer.write(currentByte);
			}
		}
	}

	/**
	 * Converts this message to a byte array.
	 * 
	 * @return The value of this message as a byte array.
	 */
	public byte[] toByteArray() {
		if(type == Connection.OPCODE_TEXT_FRAME) {
			if(message != null) {
				return message.getBytes();
			}
			return stringBuffer.toString().getBytes();
		}
		return buffer.toByteArray();
	}

	public String toString() {
		if(type == Connection.OPCODE_TEXT_FRAME) {
			if(message != null) {
				return message;
			}
			return stringBuffer.toString();
		}
		return buffer.toString();
	}
}
