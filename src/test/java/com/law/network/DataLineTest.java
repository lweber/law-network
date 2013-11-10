package com.law.network;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Lloyd
 */
public class DataLineTest {
	
	/**
	 * Test method for {@link com.law.network.DataLine#DataLine(com.law.network.DataLineParser)}.
	 */
	@Test
	public void testDataLineDataLineParser() {
		DataLineParser parser = new DataLineParser("MyName::one");
		DataLine dl = new DataLine(parser);
		assertEquals("MyName", dl.getName());
		assertEquals("one", dl.getInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#DataLine(java.lang.String)}.
	 */
	@Test
	public void testDataLineString() {
		DataLine dl = new DataLine("one::two::three");
		assertEquals("one", dl.getName());
		assertEquals("two", dl.getInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#DataLine(java.lang.String)}.
	 */
	@Test
	public void testDataLineString_Empty() {
		DataLine dl = new DataLine("");
		assertEquals("", dl.getName());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#DataLine(java.lang.String)}.
	 */
	@Test
	public void testDataLineString_NullString() {
		DataLine dl = new DataLine((String)null);
		assertEquals("", dl.getName());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#DataLine(com.law.network.DataLineParser)}.
	 */
	@Test (expected=NullPointerException.class)
	public void testDataLineString_NullParser() {
		DataLine dl = new DataLine((DataLineParser)null);
		assertEquals("", dl.getName());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getName()}.
	 */
	@Test
	public void testGetName() {
		DataLine dl = new DataLine("one::two::three");
		assertEquals("one", dl.getName());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getInfo(int)}.
	 */
	@Test
	public void testGetInfo() {
		DataLine dl = new DataLine("one::two::three");
		assertEquals("two", dl.getInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getInfo(int)}.
	 */
	@Test (expected=IndexOutOfBoundsException.class)
	public void testGetInfo_BadIndex() {
		DataLine dl = new DataLine("one::two::three");
		dl.getInfo(2);
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getIntInfo(int)}.
	 */
	@Test
	public void testGetIntInfo() {
		DataLine dl = new DataLine("one::1::three");
		assertEquals(1, dl.getIntInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getIntInfo(int)}.
	 */
	@Test (expected=IndexOutOfBoundsException.class)
	public void testGetIntInfo_BadIndex() {
		DataLine dl = new DataLine("one::1::three");
		dl.getIntInfo(2);
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getLongInfo(int)}.
	 */
	@Test
	public void testGetLongInfo() {
		DataLine dl = new DataLine("one::1::three");
		assertEquals(1, dl.getLongInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getLongInfo(int)}.
	 */
	@Test (expected=IndexOutOfBoundsException.class)
	public void testGetLongInfo_BadIndex() {
		DataLine dl = new DataLine("one::1::three");
		dl.getLongInfo(2);
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getBooleanInfo(int)}.
	 */
	@Test
	public void testGetBooleanInfo() {
		DataLine dl = new DataLine("one::t::f");
		assertTrue(dl.getBooleanInfo(0));
		assertFalse(dl.getBooleanInfo(1));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getBooleanInfo(int)}.
	 */
	@Test (expected=IndexOutOfBoundsException.class)
	public void testGetBooleanInfo_BadIndex() {
		DataLine dl = new DataLine("one::t::f");
		dl.getBooleanInfo(2);
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#getInfoCount()}.
	 */
	@Test
	public void testGetInfoCount() {
		DataLine dl = new DataLine("one::1::three");
		assertEquals(2, dl.getInfoCount());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#toString()}.
	 */
	@Test
	public void testToString() {
		DataLine dl = new DataLine("one::two::three");
		assertEquals("one::two::three", dl.toString());
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#addInfo(java.lang.String)}.
	 */
	@Test
	public void testAddInfoString() {
		DataLine dl = new DataLine("one::two::three");
		dl.addInfo("four");
		assertEquals("four", dl.getInfo(2));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#addInfo(int)}.
	 */
	@Test
	public void testAddInfoInt() {
		DataLine dl = new DataLine("one");
		dl.addInfo(2);
		assertEquals(2, dl.getIntInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#addInfo(long)}.
	 */
	@Test
	public void testAddInfoLong() {
		DataLine dl = new DataLine("one").addInfo(2L);
		assertEquals(2, dl.getLongInfo(0));
	}
	
	/**
	 * Test method for {@link com.law.network.DataLine#addInfo(boolean)}.
	 */
	@Test
	public void testAddInfoBoolean() {
		DataLine dl = new DataLine("one");
		dl.addInfo(true);
		dl.addInfo(false);
		assertTrue(dl.getBooleanInfo(0));
		assertFalse(dl.getBooleanInfo(1));
	}
	
	@Test
	public void testAddInfoByteArray() {
		DataLine dl = new DataLine("one").addInfo(new byte[] {0,50,51,52,53,Byte.MAX_VALUE});
		assertArrayEquals(new byte[] {0,50,51,52,53,Byte.MAX_VALUE}, dl.getByteArrayInfo(0));
	}
	
	private enum TestEnum {
		A, B
	}
	
	@Test
	public void testEnumConstructor() {
		DataLine dl = new DataLine(TestEnum.A);
		assertEquals("A", dl.getName());
	}
	
	@Test
	public void testToType() {
		DataLine dl = new DataLine("B");
		assertEquals(TestEnum.B, dl.toType(TestEnum.class));
	}
	
	@Test
	public void testToType_NotExists() {
		DataLine dl = new DataLine("C");
		assertNull(dl.toType(TestEnum.class));
	}
	
	@Test
	public void testToType_Switch() {
		DataLine dl = new DataLine("A");
		TestEnum t = dl.toType(TestEnum.class);
		boolean gotA = false;
		if (t != null) {
			switch (t) {
			case A:
				gotA = true;
				break;
			case B:
				break;
			default:
				break;
			}
		}
		assertTrue(gotA);
	}
	
	@Test (expected=NullPointerException.class)
	public void testToType_NullSwitch() {
		DataLine dl = new DataLine("C");
		switch (dl.toType(TestEnum.class)) {
		case A:
			break;
		case B:
			break;
		default:
			break;
		}
	}
	
	@Test
	public void testIsType() {
		DataLine dl = new DataLine(TestEnum.A);
		assertTrue(dl.isType(TestEnum.A));
	}
	
	@Test
	public void testIsType_NotType() {
		DataLine dl = new DataLine(TestEnum.A);
		assertFalse(dl.isType(TestEnum.B));
	}
	
}
