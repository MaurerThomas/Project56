package com.resist.websocket;

import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class Connection implements Runnable {
    public static final int OPCODE_CONTINUATION_FRAME = 0;
    public static final int OPCODE_TEXT_FRAME = 1;
    public static final int OPCODE_BINARY_FRAME = 2;
    public static final int OPCODE_NON_CONTROL_FRAME0 = 3;
    public static final int OPCODE_NON_CONTROL_FRAME1 = 4;
    public static final int OPCODE_NON_CONTROL_FRAME2 = 5;
    public static final int OPCODE_NON_CONTROL_FRAME3 = 6;
    public static final int OPCODE_NON_CONTROL_FRAME4 = 7;
    public static final int OPCODE_CONNECTION_CLOSE = 8;
    public static final int OPCODE_PING = 9;
    public static final int OPCODE_PONG = 10;
    public static final int OPCODE_CONTROL_FRAME0 = 11;
    public static final int OPCODE_CONTROL_FRAME1 = 12;
    public static final int OPCODE_CONTROL_FRAME2 = 13;
    public static final int OPCODE_CONTROL_FRAME3 = 14;
    public static final int OPCODE_CONTROL_FRAME4 = 15;

    public static final String GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

    private ConnectionServer server;
    private Socket socket;
    private OutputStream output;
    private InputStream input;
    private String key;
    private Message currentMessage = null;
    private boolean closed = false;

    /**
     * Creates a new connection.
     *
     * @param server The connection server
     * @param socket The connection socket
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public Connection(ConnectionServer server, Socket socket) throws IOException, NoSuchAlgorithmException {
        this.server = server;
        this.socket = socket;
        output = socket.getOutputStream();
        input = socket.getInputStream();
        handShake();
    }

    /**
     * Returns the server that spawned this connection.
     *
     * @return The server that spawned this connection
     */
    public ConnectionServer getServer() {
        return server;
    }

    /**
     * Checks if the connection has been closed.
     *
     * @return True if the connection has been closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Runs the thread, reading messages.
     */
    @Override
    public void run() {
        try {
            int data;
            while (!closed && (data = input.read()) != -1) {
                readAndHandleMessage(data);
            }
        } catch (SocketTimeoutException e) {
            server.getLogger().log(Level.INFO, "Closing connection.");
        } catch (SocketException e) {
            server.getLogger().log(Level.INFO, "Connection closed unexpectedly.");
        } catch (IOException e) {
            server.getLogger().log(Level.WARNING, "Connection closed unexpectedly.", e);
        } finally {
            close();
        }
    }

    /**
     * Closes the connection.
     */
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        sendClose();
        try {
            output.close();
            input.close();
            socket.close();
        } catch (IOException e) {
            server.getLogger().log(Level.WARNING, "Failed to close connection.", e);
        }
    }

    /**
     * Tries to establish a WebSocket connection.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private void handShake() throws IOException, NoSuchAlgorithmException {
        if (isWebSocketRequest()) {
            sendResponseHeader();
        } else {
            close();
        }
    }

    /**
     * Parses an HTTP header.
     *
     * @param input The input stream to read from
     * @return A map of request headers
     * @throws IOException
     */
    public static Map<String, List<String>> parseHTTP(BufferedReader input) throws IOException {
        Map<String, List<String>> out = new HashMap<String, List<String>>();
        String line;
        while ((line = input.readLine()) != null && !line.isEmpty()) {
            String[] args = line.split("\\s*:\\s*", 2);
            if (args.length >= 2) {
                List<String> values = Arrays.asList(args[1].split("\\s*,\\s*"));
                out.put(args[0].toLowerCase(), values);
            }
        }
        return out;
    }

    /**
     * Validates a handshake.
     *
     * @return True if the request is a valid WebSocket request
     * @throws IOException
     */
    private boolean isWebSocketRequest() throws IOException {
        BufferedReader input = new BufferedReader(new InputStreamReader(this.input));
        String line = input.readLine();
        if (line == null || !line.equals("GET " + server.getPath() + " HTTP/1.1")) {
            return false;
        }
        Map<String, List<String>> headers = parseHTTP(input);
        if (
                headers.containsKey("host") &&
                        headers.containsKey("upgrade") &&
                        headers.containsKey("sec-websocket-version") &&
                        headers.containsKey("connection") &&
                        headers.containsKey("origin") &&
                        headers.get("host").contains(server.getAddress() + ":" + server.getPort()) &&
                        (headers.get("upgrade").contains("websocket") || headers.get("upgrade").contains("Websocket") || headers.get("upgrade").contains("WebSocket")) &&
                        headers.get("sec-websocket-version").contains("13") &&
                        headers.get("connection").contains("Upgrade") &&
                        headers.get("origin").get(0).contains("://" + server.getAddress())
                ) {
            key = headers.get("sec-websocket-key").get(0);
            return true;
        }
        return false;
    }

    /**
     * Sends a handshake confirmation to the client.
     *
     * @throws NoSuchAlgorithmException
     */
    private void sendResponseHeader() throws NoSuchAlgorithmException {
        PrintWriter writer = new PrintWriter(output, true);
        writer.println(
                "HTTP/1.1 101 Switching Protocols\r\n" +
                        "Upgrade: websocket\r\n" +
                        "Connection: Upgrade\r\n" +
                        "Sec-WebSocket-Accept: " + getResponseKey() + "\r\n");
    }

    /**
     * Calculates the response key to send to the client.
     *
     * @return The Base64 encoded hash of the key and GUID
     * @throws NoSuchAlgorithmException
     */
    private String getResponseKey() throws NoSuchAlgorithmException {
        String response = key + GUID;
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] hash = md.digest(response.getBytes());
        return new String(Base64.encodeBase64(hash));
    }

    /**
     * Reads a message byte for byte.
     *
     * @param nextByte First byte of the message
     * @throws IOException
     */
    private void readAndHandleMessage(int nextByte) throws IOException {
        //First bit
        int fin = nextByte >> 7;
        if (checkRSV(nextByte)) {
            close();
            return;
        }
        //Bit 5-8
        int opcode = nextByte & ((1 << 4) - 1);
        if ((currentMessage == null || opcode != OPCODE_CONTINUATION_FRAME) && opcode < OPCODE_CONNECTION_CLOSE) {
            currentMessage = new Message(this, opcode);
        }
        nextByte = input.read();
        //First bit
        int mask = nextByte >> 7;
        long payloadLen = getPayloadLength(nextByte);
        int[] maskingKey = getMaskingKey(mask == 1);
        handleMessage(opcode, fin == 1, payloadLen, maskingKey);
    }

    /**
     * Checks if the reserved bytes RSV1-3 are 0.
     *
     * @param nextByte The byte to read bits from
     * @return False if the three RSV bytes are 0
     */
    private boolean checkRSV(int nextByte) {
        //Second bit
        int rsv1 = (nextByte >> 6) & 1;
        //Third bit
        int rsv2 = (nextByte >> 5) & 1;
        //Fourth bit
        int rsv3 = (nextByte >> 4) & 1;
        return (rsv1 | rsv2 | rsv3) != 0;
    }

    /**
     * Returns the length of the message.
     *
     * @param nextByte The byte to start reading from
     * @return The length of the message
     * @throws IOException
     */
    private long getPayloadLength(int nextByte) throws IOException {
        //Bit 2-8
        long payloadLen = nextByte & ((1 << 7) - 1);
        if (payloadLen == 126) {
            //Next two bytes
            payloadLen = (input.read() << 8) + input.read();
        } else if (payloadLen == 127) {
            //Next 8 bytes as unsigned 64bit int
            byte[] length = new byte[8];
            input.read(length);
            payloadLen = new BigInteger(length).longValue();
        }
        return payloadLen;
    }

    /**
     * Returns the message's masking key.
     *
     * @param mask The bit indicating if the message is masked
     * @return The masking key
     * @throws IOException
     */
    private int[] getMaskingKey(boolean mask) throws IOException {
        int[] maskingKey = new int[4];
        if (mask) {
            for (int n = 0; n < 4; n++) {
                //Next four bytes
                maskingKey[n] = input.read();
            }
        }
        return maskingKey;
    }

    /**
     * Handles a received message.
     *
     * @param opcode     The opcode of the message
     * @param fin        The finished bit of the message
     * @param payloadLen The length of the message
     * @param maskingKey The masking key of the message
     * @throws IOException
     */
    private void handleMessage(int opcode, boolean fin, long payloadLen, int[] maskingKey) throws IOException {
        if (opcode < OPCODE_CONNECTION_CLOSE) {
            handleContinuedFrame(fin, payloadLen, maskingKey);
        } else {
            handleControlFrame(opcode, payloadLen, maskingKey);
        }
    }

    /**
     * Handles the continuation of an incoming message.
     *
     * @param fin        The finished bit of the message
     * @param payloadLen The length of the message
     * @param maskingKey The masking key of the message
     * @throws IOException
     */
    private void handleContinuedFrame(boolean fin, long payloadLen, int[] maskingKey) throws IOException {
        currentMessage.add(input, payloadLen, maskingKey);
        if (fin) {
            currentMessage.complete();
            server.handleMessage(currentMessage);
            currentMessage = null;
        }
    }

    /**
     * Handles a received control frame.
     *
     * @param opcode     The opcode of the message
     * @param payloadLen The length of the message
     * @param maskingKey The masking key of the message
     * @throws IOException
     */
    private void handleControlFrame(int opcode, long payloadLen, int[] maskingKey) throws IOException {
        Message controlMessage = new Message(this, opcode);
        controlMessage.add(input, payloadLen, maskingKey);
        controlMessage.complete();
        server.handleControlFrame(controlMessage);
    }

    /**
     * Sends a pong response to the client.
     *
     * @param message The message to return
     * @return True if no IOException was thrown
     */
    public boolean sendPong(byte[] message) {
        return sendMessage(true, OPCODE_PONG, null, message);
    }

    /**
     * Tells the client to close the connection.
     */
    private void sendClose() {
        if (!sendMessage(true, OPCODE_CONNECTION_CLOSE, null, new byte[0])) {
            server.getLogger().log(Level.WARNING, "Failed to send close: client gone.");
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message
     * @return True if no IOException was thrown
     */
    public boolean sendMessage(String message) {
        return sendMessage(true, OPCODE_TEXT_FRAME, null, message.getBytes());
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message
     * @return True if no IOException was thrown
     */
    public boolean sendMessage(byte[] message) {
        return sendMessage(true, OPCODE_BINARY_FRAME, null, message);
    }

    /**
     * Sends a message to the client.
     *
     * @param fin     True if this is a complete message
     * @param opcode  One of Connection.OPCODE_*
     * @param mask    The mask that has been applied to the message
     * @param message The message to send
     * @return True if no IOException was thrown
     */
    public boolean sendMessage(boolean fin, int opcode, int[] mask, byte[] message) {
        try {
            byte[] frame = encapsulateMessage(fin, opcode, mask, message);
            output.write(frame);
            output.flush();
            return true;
        } catch (IOException e) {
            close();
            return false;
        }
    }

    /**
     * Encapsulates a message.
     *
     * @param fin     True if this is a complete message
     * @param opcode  One of Connection.OPCODE_*
     * @param mask    The mask that has been applied to the message
     * @param message The message to send
     * @return The data frame
     * @throws IOException
     */
    private byte[] encapsulateMessage(boolean fin, int opcode, int[] mask, byte[] message) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.write(getMessageFirstByte(fin, opcode));
        buffer.write(getMessageLengthBytes(mask != null, message.length));
        if (mask != null) {
            for (int n = 0; n < 4; n++) {
                buffer.write(mask[n]);
            }
        }
        buffer.write(message);
        return buffer.toByteArray();
    }

    /**
     * Returns the first byte of the data frame.
     *
     * @param fin    True if this is a complete message
     * @param opcode One of Connection.OPCODE_*
     * @return The first byte of the data frame
     */
    private int getMessageFirstByte(boolean fin, int opcode) {
        int header = 0;
        if (fin) {
            header = 1;
        }
        return (header << 7) | opcode;
    }

    /**
     * Returns the payload length portion of the data frame.
     *
     * @param masked True if the message has a mask
     * @param length Length of the message
     * @return The payload length of the data frame
     */
    private byte[] getMessageLengthBytes(boolean masked, long length) {
        int mask = 0;
        if (masked) {
            mask = 1;
        }
        mask = mask << 7;
        if (length < 126) {
            return getMessageSmallLengthBytes(mask, length);
        } else if (length < 65536) {
            return getMessageMediumLengthBytes(mask, length);
        } else {
            return getMessageLargeLengthBytes(mask, length);
        }
    }

    /**
     * Returns the payload length portion of the data frame for small payloads.
     *
     * @param mask   The first bit of the output
     * @param length Length of the message
     * @return The payload length of the data frame
     */
    private byte[] getMessageSmallLengthBytes(int mask, long length) {
        return new byte[]{
                (byte) (mask | length)
        };
    }

    /**
     * Returns the payload length portion of the data frame for medium payloads.
     *
     * @param mask   The first bit of the output
     * @param length Length of the message
     * @return The payload length of the data frame
     */
    private byte[] getMessageMediumLengthBytes(int mask, long length) {
        byte[] out = new byte[3];
        out[0] = (byte) (mask | 126);
        out[1] = (byte) (length >> 8);
        out[2] = (byte) (length & 0xff);
        return out;
    }

    /**
     * Returns the payload length portion of the data frame for large payloads.
     *
     * @param mask   The first bit of the output
     * @param length Length of the message
     * @return The payload length of the data frame
     */
    private byte[] getMessageLargeLengthBytes(int mask, long length) {
        byte[] out = new byte[9];
        out[0] = (byte) (mask | 127);
        byte[] len = BigInteger.valueOf(length).toByteArray();
        for (int n = len.length - 1, i = 8; i >= 0 && n >= 0; n--, i--) {
            out[i] = len[n];
        }
        return out;
    }
}
