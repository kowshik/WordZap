package com.android.wordzap.tests;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.android.wordzap.InvalidGridSizeException;

public class InvalidGridSizeExceptionTest {
	private InvalidGridSizeException exception1;
	private InvalidGridSizeException exception2;
	private InvalidGridSizeException exception3;
	private InvalidGridSizeException exception4;
	
	@Before
	public void setUp() throws Exception {
		
		exception1=new InvalidGridSizeException(5, 5);
		exception2=new InvalidGridSizeException(-1, 5);
		exception3=new InvalidGridSizeException(6, -3);
		exception4=new InvalidGridSizeException(-5,-10);
	}

	@After
	public void tearDown() throws Exception {
		
		exception1=exception2=exception3=exception4=null;
	}

	@Test
	public void testToString() {
		
		String err="Passed positive values for numRows and numCols. InvalidGridSizeException is expected to not print an error message.";
		assertTrue(err,exception1.toString().equals(""));
				
		err="Passed negative value for numRows. InvalidGridSizeException is expected to print an error message.";		
		assertFalse(err,exception2.toString().equals(""));
		
		err="Passed negative value for numCols. InvalidGridSizeException is expected to print an error message.";		
		assertFalse(err,exception3.toString().equals(""));
		
		err="Passed negative value for numRows and numCols. InvalidGridSizeException is expected to print an error message.";			
		assertFalse(exception4.toString().equals(""));
	}

}
