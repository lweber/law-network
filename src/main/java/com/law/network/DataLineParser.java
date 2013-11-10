/*
 * Created on Dec 16, 2007
 */
package com.law.network;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Low level representation of a line of data.
 * 
 * @author Lloyd
 */
public final class DataLineParser {
	
	static private final String DELIM = "::";
	static private final Pattern DELIMPATTERN = Pattern.compile(DELIM);
	static private final String DELIM_REPLACEMENT = "[:]dR[:]";
	
	private String name;
	private List<String> info;
	
	/**
	 * Construct a data line parser.
	 * 
	 * <p> The given string will be split using "::" as the delimeter. The
	 * first piece becomes the name, and each successive piece becomes an info
	 * string, starting with index zero.
	 * 
	 * <p> An input string of null is treated as "".
	 * 
	 * @param s - The string to split into a data line.
	 */
	public DataLineParser(String s) {
		if (s == null) {
			s = "";
		}
		String [] data = DELIMPATTERN.split(s, -1);
		
		name = data.length > 0 ? data[0] : "";
		
		if (data.length > 1) {
			info = new ArrayList<String>(data.length - 1);
			for (int i = 1; i < data.length; i++) {
				info.add(data[i]);
			}
		}
	}
	
	/**
	 * Add an info string.
	 * 
	 * <p> The info string count will increase by one, and the info string will
	 * be added immediately following the previously last info string.
	 * 
	 * @param s - The info string to add.
	 */
	void addInfo(String s) {
		if (info == null) {
			info = new ArrayList<String>();
		}
		info.add(escapeDelim(s));
	}
	
	String removeLastInfo() {
		if (info != null && info.size() > 0) {
			return info.remove(info.size() - 1);
		}
		return null;
	}
	
	private String escapeDelim(String s) {
		return s.indexOf(DELIM) >= 0
				? s.replace(DELIM, DELIM_REPLACEMENT)
				: s;
	}
	
	private String unescapeDelim(String s) {
		return s.indexOf(DELIM_REPLACEMENT) >= 0
				? s.replace(DELIM_REPLACEMENT, DELIM)
				: s;
	}
	
	/**
	 * Get the name of the data line. The name is generally a command id string.
	 * 
	 * @return The name, or "" if none.
	 */
	public String getName() {
		return name;
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
		if (info == null) {
			throw new IndexOutOfBoundsException(
					"Attempt info read (index = " + index + ") but info is null.");
		}
		if (index >= info.size()) {
			throw new IndexOutOfBoundsException("Out-of-bounds info read: index = " +
					index + " but info.size() = " + info.size() + ".");
		}
		return info != null ? unescapeDelim(info.get(index)) : "";
	}
	
	/**
	 * Get the count of info strings.
	 * 
	 * @return The count of info strings.
	 */
	public int getInfoCount() {
		return info != null ? info.size() : 0;
	}
	
	/**
	 * Return a string with the name field, followed by the info fields if any,
	 * all delimited with the default delimiter ("::").
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer(name);
		if (info != null) {
			for (String s : info) {
				result.append(DELIM);
				result.append(s);
			}
		}
		return result.toString();
	}
	
}
