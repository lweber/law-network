/*
 * (c) lweber 2008-2012
 * Created on Apr 1, 2008
 */
package com.law.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class to handle communication to and/or from a socket.
 * 
 * <p> The socket handler begins following the rules of its mode only
 * after its start() method has been called. Prior to starting, the socket
 * handler's writeLine() and readLine() methods can be used for synchronous
 * communication, such as initial handshakes.
 * 
 * @author Lloyd
 */
public class SocketHandler extends Thread {
	
	/**
	 * Modes that a SocketHandler can operate in.
	 */
	static public enum Mode {
		/** Read from the socket, and for each line read call the handler's
		 *  read processor (if non-null) with the data that was read. This mode
		 *  is essentially "listener" mode, where the handler waits for data
		 *  and then processes it. */
		MODE_READ,
		
		/** Write to the socket when the handler's send() method is called.
		 *  This mode is essentially "write and forget" mode, where the handler just
		 *  sends data. */
		MODE_WRITE,
		
		/** Read from the socket, obtain a response from the socket read processor
		 *  and write the response back to the socket. If the socket read processor
		 *  is null then send "" as the response. This mode is essentially "server"
		 *  mode, where the handler waits for a query and then supplies a response. */
		MODE_READ_WRITE,
		
		/** Write to the socket when the handler's send() method is called, and
		 *  then read a response back from the socket, and send the response to
		 *  the socket read processor if the processor is non-null. This mode is
		 *  essentially "client" mode, where the handler sends a query and then
		 *  waits for a response to process. */
		MODE_WRITE_READ
	}
	
	private final Socket socket;
	private final Mode mode;
	private final SocketReadProcessor readProcessor;
	private final SocketShutdownListener shutdownListener;
	private final BufferedWriter out;
	private final BufferedReader in;
	private boolean isStopped = false;
	private List<String> outBuffer = new ArrayList<String>();
	
	/**
	 * Construct a socket handler thread.
	 * 
	 * @param socket - the socket to handle.
	 * @param mode - the mode in which the socket will be handled while the
	 *  handler thread is running:
	 *  <ul>
	 *  <li> MODE_READ - Read from the socket, and for each line read call the
	 *          read processor (if non-null) with the data that was read. This mode
	 *          is essentially "listener" mode, where the handler waits for data
	 *          and then processes it.
	 *  <li> MODE_WRITE - Write to the socket when send() is called. This mode is
	 *          essentially "write and forget" mode, where the handler just sends data.
	 *  <li> MODE_READ_WRITE - Read from the socket, obtain a response from the
	 *          socket read processor and write the response back to the socket.
	 *          If the socket read processor is null then send "" as the response.
	 *          This mode is essentially "server" mode, where the handler waits
	 *          for a query and then supplies a response.
	 *  <li> MODE_WRITE_READ - Write to the socket when send() is called, and
	 *          then read a response back from the socket, and send the response
	 *          to the socket read processor if the processor is non-null. This
	 *          mode is essentially "client" mode, where the handler sends a
	 *          query and then waits for a response to process.
	 *  </ul>
	 * @param readProcessor - Socket read processor to call when data is read
	 *  from the socket; can be null. Not used when mode is MODE_WRITE.
	 * @param shutdownListener - Listener for socket close event, or null for
	 *  none.
	 * 
	 * @throws IOException if an I/O error occurs when creating the output
	 *  stream, or if an I/O error occurs when creating the input stream,
	 *  or the socket is closed, or the socket is not connected, or the socket
	 *  input has been shut down using shutdownInput().
	 */
	public SocketHandler(Socket socket, Mode mode, SocketReadProcessor readProcessor,
			SocketShutdownListener shutdownListener)
	throws IOException {
		this.socket = socket;
		this.mode = mode;
		this.readProcessor = readProcessor;
		this.shutdownListener = shutdownListener;
		this.out = getOutputStreamWriter();
		this.in = getInputStreamReader();
	}
	
	@Override
	public void run() {
		try {
			infiniteLoop:
			do {
				switch (mode) {
				case MODE_READ:
					String inData = readLine();
					if (inData == null) {
						break infiniteLoop;
					}
					processDataFromSocket(inData);
					break;
					
				case MODE_WRITE:
					String outData = waitForDataToWrite();
					writeLine(outData);
					break;
					
				case MODE_READ_WRITE:
					String inQuery = readLine();
					if (inQuery == null) {
						break infiniteLoop;
					}
					String outResponse = processDataFromSocket(inQuery);
					writeLine(outResponse);
					break;
					
				case MODE_WRITE_READ:
					String outQuery = waitForDataToWrite();
					writeLine(outQuery);
					String response = readLine();
					if (response == null) {
						break infiniteLoop;
					}
					processDataFromSocket(response);
					break;
				}
			}
			while (!isStopped);
		}
		catch (IOException e) {
			// TODO Perhaps don't want to return from run after IOException without retrying.
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			System.out.println("Socket handler " + Thread.currentThread().getName() + " interrupt stop. shutdownListener = " + shutdownListener);
		}
		finally {
			if (shutdownListener != null) {
				try { shutdownListener.socketClosing(this); }
				catch (Exception e) {
					// Eat any exception so that the close always happens.
				}
			}
			close();
		}
	}
	
	/**
	 * Send data to the socket read processor and obtain a response.
	 * 
	 * <p> The data will not be null.
	 * <p> If the read processor is null then "" is returned.
	 * 
	 * @param dataReadFromSocket - The data that was read from the socket,
	 *  and which will now be sent to the socket read processor.
	 * 
	 * @return Response from the socket read processor, or "" if the socket
	 *  read processor is null.
	 */
	private String processDataFromSocket(String dataReadFromSocket) {
		return readProcessor != null
				? readProcessor.processDataFromSocket(dataReadFromSocket, getId())
				: "";
	}
	
	/**
	 * Wait for the out buffer to contian something.
	 * 
	 * @return The oldest item in the out buffer.
	 * 
	 * @throws InterruptedException If the wait is interrupted.
	 */
	synchronized private String waitForDataToWrite() throws InterruptedException {
		while (out != null && outBuffer.isEmpty()) {
			wait();
		}
		if (out != null && !outBuffer.isEmpty()) {
			return outBuffer.remove(0);
		}
		return out == null ? null : "";
	}
	
	/**
	 * Send data to the socket.
	 * 
	 * <p> The data will only be sent after this socket handler thread has been
	 * started, and as long as it continues to run.
	 * 
	 * <p> This is asynchronous so the caller doesn't have to wait.
	 * The buffer ensures that the socket receives messages in the same
	 * order they were sent.
	 * 
	 * <p> Another purpose of the buffer is to transfer control away from
	 * the caller thread, to this socket handler thread.
	 * 
	 * @param data - The data to send.
	 */
	synchronized public void send(String data) {
		if (out != null) {
			outBuffer.add(data);
		}
		notifyAll();
	}
	
	/**
	 * Write a line of data to the socket.
	 * 
	 * <p> The data is sent directly to the socket's out stream writer,
	 * followed by a newline and a flush.
	 * 
	 * @param data - The data to write.
	 * 
	 * @throws IOException If an I/O error occurs.
	 */
	synchronized public void writeLine(String data) throws IOException {
		out.write(data);
		out.newLine();
		out.flush();
	}
	
	/**
	 * Read a line of data from the socket.
	 * 
	 * <p> This method blocks until the data is ready.
	 * <p> The result will not include any line-termination characters.
	 * <p> Null will be returned if the end of the stream has been reached.
	 * 
	 * @return A String containing the contents of the line, not including any
	 *  line-termination characters, or null if the end of the stream has been
	 *  reached.
	 * 
	 * @throws IOException If an I/O error occurs
	 */
	public String readLine() throws IOException {
		try {
			return in.readLine();
		}
		catch (IOException e) {
			if (isStopped) {
				return null;
			}
			else {
				throw e;
			}
		}
	}
	
	/**
	 * Stop this socket handler from any further processing and close the I/O
	 * buffers and socket.
	 */
	synchronized public void stopHandler() {
		isStopped = true;
		interrupt();
	}
	
	private BufferedWriter getOutputStreamWriter() throws IOException {
		return new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
	}
	
	private BufferedReader getInputStreamReader() throws IOException {
		return new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	/**
	 * Close the I/O streams and the socket.
	 */
	public void close() {
		if (out != null) {
			try { out.close(); }
			catch (IOException e) {}
		}
		if (in != null) {
			try { in.close(); }
			catch (IOException e) {}
		}
		if (socket != null) {
			try { socket.close(); }
			catch (IOException e) {}
		}
	}
	
}
