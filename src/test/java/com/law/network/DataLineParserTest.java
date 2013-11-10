/*
 * Created Mar 4, 2012
 */
package com.law.network;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Lloyd
 */
public class DataLineParserTest {
	
	/**
	 * Test method for {@link com.law.network.DataLineParser#DataLineParser(java.lang.String)}.
	 */
	@Test
	public void testDataLineParser() {
		DataLineParser parser = new DataLineParser("");
		assertEquals("", parser.getName());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLineParser#DataLineParser(java.lang.String)}.
	 */
	@Test
	public void testDataLineParser_Null() {
		DataLineParser parser = new DataLineParser(null);
		assertEquals("", parser.getName());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLineParser#addInfo(java.lang.String)}.
	 */
	@Test
	public void testAddInfo() {
		DataLineParser parser = new DataLineParser("");
		parser.addInfo("hi");
		assertEquals("hi", parser.getInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLineParser#getName()}.
	 */
	@Test
	public void testGetName() {
		DataLineParser parser = new DataLineParser("MyName");
		assertEquals("MyName", parser.getName());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLineParser#getInfo(int)}.
	 */
	@Test
	public void testGetInfo() {
		DataLineParser parser = new DataLineParser("MyName::one");
		assertEquals("one", parser.getInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLineParser#getInfoCount()}.
	 */
	@Test
	public void testGetInfoCount() {
		DataLineParser parser = new DataLineParser("MyName::one");
		assertEquals(1, parser.getInfoCount());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLineParser#toString()}.
	 */
	@Test
	public void testToString() {
		DataLineParser parser = new DataLineParser("MyName::one");
		assertEquals("MyName::one", parser.toString());
	}
	
}
