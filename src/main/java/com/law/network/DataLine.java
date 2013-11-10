/*
 * (c) lweber 2007-2012
 * Created on Dec 15, 2007
 */
package com.law.network;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * High level representation of a line of data.
 * 
 * @author Lloyd
 */
public class DataLine {
	
	private DataLineParser parser;
	
	/**
	 * Construct a data line based on the name of the given enum value.
	 * 
	 * @param enumValue - The enum value.
	 */
	public <T extends Enum<T>> DataLine(T enumValue) {
		this(enumValue.name());
	}
	
	/**
	 * Construct a data line by reading from the given socket handler.
	 * 
	 * <p> Calls {@link SocketHandler#readLine()} and passes the result to the
	 * string constructor of this class.
	 * 
	 * @param socketHandler - The socket handler.
	 * 
	 * @throws IOException If the socket handler throws an IOException.
	 */
	public DataLine(SocketHandler socketHandler) throws IOException {
		this(socketHandler.readLine());
	}
	
	/**
	 * Construct a data line.
	 * 
	 * <p> Splits the given string as per {@link DataLineParser#DataLineParser(String)}.
	 * 
	 * @param s - The string to split into a data line.
	 */
	public DataLine(String s) {
		parser = new DataLineParser(s);
	}
	
	/**
	 * Construct a data line.
	 * 
	 * @param p - A data line parser.
	 * 
	 * @throws NullPointerException if the given data line parser is null.
	 */
	protected DataLine(DataLineParser p) {
		if (p == null) {
			throw new NullPointerException("Null data line parser.");
		}
		parser = p;
	}
	
	/**
	 * Determine whether the name of this data line matches the name of the
	 * given enum value.
	 * 
	 * @param enumVal - The enum value.
	 * 
	 * @return True if the name matches, or false if not.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> boolean isType(T enumVal) {
		return enumVal == toType(enumVal.getClass());
	}
	
	/**
	 * Get the enum value of the given enum type which corresponds the name
	 * of this data line.
	 * 
	 * @param enumType - The type of enum to return.
	 * 
	 * @return The enum value, or null if it does not exist.
	 */
	public <T extends Enum<T>> T toType(Class<T> enumType) {
		try { return Enum.valueOf(enumType, getName()); }
		catch (Exception e) { return null; }
	}
	
	/**
	 * Send the string value of this data to the given socket handler.
	 * 
	 * <p> Calls {@link SocketHandler#send(String)} to send the data to
	 * the socket.
	 * 
	 * <p> The data will only be sent after the socket handler thread has been
	 * started, and as long as it continues to run.
	 * 
	 * <p> This is asynchronous so the caller doesn't have to wait. Messages
	 * are sent to the socket in the same order they are received by the socket
	 * handler.
	 * 
	 * @see DataLine#toString()
	 * @see SocketHandler#writeLine(String)
	 * 
	 * @param socketHandler - The socket handler.
	 */
	public void sendTo(SocketHandler socketHandler) {
		socketHandler.send(toString());
	}
	
	/**
	 * Write the string value of this data to the given socket handler.
	 * 
	 * <p> Calls {@link SocketHandler#writeLine(String)} to write the data to
	 * the socket's out stream writer.
	 * 
	 * @see DataLine#toString()
	 * @see SocketHandler#writeLine(String)
	 * 
	 * @param socketHandler - The socket handler.
	 * 
	 * @throws IOException If the socket handler throws an IOException.
	 */
	public void writeTo(SocketHandler socketHandler) throws IOException {
		socketHandler.writeLine(toString());
	}
	
	/**
	 * Get the name of the data line. The name is generally a command id string.
	 * 
	 * @return The name, or "" if none.
	 */
	public String getName() {
		return parser.getName();
	}
	
	/**
	 * Get all the info strings as an array.
	 * 
	 * <p> The length of the returned array will be equal to the result of
	 * calling getInfoCount().
	 * 
	 * @return An array of zero or more info strings.
	 */
	public String[] getInfo() {
		String [] infoArray = new String[getInfoCount()];
		for (int i = 0; i < getInfoCount(); i++) {
			infoArray[i] = getInfo(i);
		}
		return infoArray;
	}
	
	/**
	 * Get the info string at the given index.
	 * 
	 * @param index - Index of the info string to get (0-based).
	 * 
	 * @return The info string.
	 * 
	 * @throws IndexOutOfBoundsException if no data exists at the given index.
	 */
	public String getInfo(int index) {
		return parser.getInfo(index);
	}
	
	/**
	 * Parse the info string at the given index into an int value.
	 * 
	 * @param index - Index of the info string to parse (0-based).
	 * 
	 * @return The int represented by the info string.
	 * 
	 * @throws IndexOutOfBoundsException if no data exists at the given index.
	 * @throws NumberFormatException if the string does not contain a parsable
	 *  integer.
	 */
	public int getIntInfo(int index) {
		return Integer.parseInt(parser.getInfo(index));
	}
	
	/**
	 * Parse the info string at the given index into a long value.
	 * 
	 * @param index - Index of the info string to parse (0-based).
	 * 
	 * @return The long represented by the info string.
	 * 
	 * @throws IndexOutOfBoundsException if no data exists at the given index.
	 * @throws NumberFormatException if the string does not contain a parsable
	 *  long.
	 */
	public long getLongInfo(int index) {
		return Long.parseLong(parser.getInfo(index));
	}
	
	/**
	 * Parse the info string at the given index into a boolean value. If the
	 * info string equals "t" then 'true' will be returned, otherwise 'false'
	 * will be returned.
	 * 
	 * @param index - Index of the info string to parse (0-based).
	 * 
	 * @return The boolean value represented by the info string.
	 * 
	 * @throws IndexOutOfBoundsException if no data exists at the given index.
	 */
	public boolean getBooleanInfo(int index) {
		return "t".equals(parser.getInfo(index));
	}
	
	/**
	 * Parse the info string at the given index into a byte array value.
	 * 
	 * @param index - Index of the info string to parse (0-based).
	 * 
	 * @return The byte array value represented by the info string.
	 * 
	 * @throws IndexOutOfBoundsException if no data exists at the given index.
	 */
	public byte[] getByteArrayInfo(int index) {
		String byteStr = parser.getInfo(index);
		return byteStr.getBytes();
	}
	
	/**
	 * Get the count of info strings.
	 * 
	 * @return The count of info strings.
	 */
	public int getInfoCount() {
		return parser.getInfoCount();
	}
	
	/**
	 * Return a string with the name field, followed by the info fields if any,
	 * all delimited with the default delimiter ("::").
	 */
	@Override
	public String toString() {
		return parser.toString();
	}
	
	/**
	 * Add an info string.
	 * 
	 * <p> The info string count will increase by one, and the info string will
	 * be added immediately following the previously last info string.
	 * 
	 * @param s - The info string to add.
	 */
	public DataLine addInfo(String s) {
		parser.addInfo(s);
		return this;
	}
	
	/**
	 * Add an info string representation of the given int.
	 * 
	 * <p> The info string count will increase by one, and the info string will
	 * be added immediately following the previously last info string.
	 * 
	 * @param i - The int to add.
	 */
	public DataLine addInfo(int i) {
		parser.addInfo(Integer.toString(i));
		return this;
	}
	
	/**
	 * Add an info string representation of the given long.
	 * 
	 * <p> The info string count will increase by one, and the info string will
	 * be added immediately following the previously last info string.
	 * 
	 * @param l - The long to add.
	 */
	public DataLine addInfo(long l) {
		parser.addInfo(Long.toString(l));
		return this;
	}
	
	/**
	 * Add an info string representation of the given boolean. The boolean will
	 * be represented by either "t" or "f".
	 * 
	 * <p> The info string count will increase by one, and the info string will
	 * be added immediately following the previously last info string.
	 * 
	 * @param b - The boolean to add.
	 */
	public DataLine addInfo(boolean b) {
		parser.addInfo(b ? "t" : "f");
		return this;
	}
	
	/**
	 * Add an info string respresentation of the given bytes. The bytes must be
	 * encoded (e.g. base64) so that they can be cleanly converted into a string.
	 * 
	 * <p> The info string count will increase by one, and the info string will
	 * be added immediately following the previously last info string.
	 * 
	 * @param bytes - The bytes to add.
	 */
	public DataLine addInfo(byte[] bytes) {
		try {
			parser.addInfo(new String(bytes, "utf-8"));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	/**
	 * Remove the last info item. No effect if no info items.
	 * 
	 * @return This data line.
	 */
	public DataLine removeLastInfo() {
		parser.removeLastInfo();
		return this;
	}
	
}
