/**
 * 
 * @author Kowshik Prakasam
 * 
 * JUnit 4 Test Driver
 * Add all your test classes here
 * 
 */
package com.android.wordzap.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( { InvalidGridSizeExceptionTest.class, WordStackTest.class,
		LetterGridTest.class })
public class AllTests {
}
