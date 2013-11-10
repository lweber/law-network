/*
 * Created on Apr 1, 2008
 */
package com.law.network;

/**
 * SocketReadProcessor processes data from a socket and returns a response.
 * 
 * @author Lloyd
 */
public interface SocketReadProcessor {
	
	/**
	 * Process the data and return a response.
	 * 
	 * <p> This method is called by a socket handler when data is read from it's
	 * socket. Since the handler read the data the handler is using one of
	 * MODE_READ, MODE_READ_WRITE or MODE_WRITE_READ. If the handler is using
	 * MODE_READ_WRITE then the handler will write this method's return value
	 * back to the socket.
	 * 
	 * <p> The data will not be null.
	 * 
	 * <p> The implementation of this method may need to be synchronized if
	 * multiple socket handler threads will be calling it.
	 * 
	 * @param data - The data that was read from the socket.
	 * @param fromId - The unique id of the socket handler thread.
	 * 
	 * @return A response which will be written back to the socket if the socket
	 *  handler is using MODE_READ_WRITE, otherwise ignored.
	 */
	String processDataFromSocket(String data, long fromId);
	
}
