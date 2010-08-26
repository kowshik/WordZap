/**
 *  
 * The MIT License : http://www.opensource.org/licenses/mit-license.php

 * Copyright (c) 2010 Kowshik Prakasam

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */


package com.android.wordzap.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.android.wordzap.exceptions.InvalidGridSizeException;

/* 
 * JUnit Test Cases for class InvalidGridSizeException
 * 
 */
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
