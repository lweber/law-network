/*
 * Created on Mar 31, 2012
 */
package com.law.network;

/**
 * SocketShutdownListener is called when a socket handler is closing it's
 * connection.
 * 
 * @author Lloyd
 */
public interface SocketShutdownListener {
	
	/**
	 * This method is called immediately prior to a running socket handler
	 * (e.g. one that has been started) closing its socket connection.
	 * 
	 * <p> This will happen whether the socket handler is closed
	 * intentionally, or closes due to an exception.
	 * 
	 * @param socketHandler - The socket handler that is closing.
	 */
	void socketClosing(SocketHandler socketHandler);
	
}
