/**
 * 
 * @author Kowshik Prakasam
 * 
 * A class that represents invalid stack operations performed when in class WordStack when the word at top of stack is locked
 *
 */

package com.android.wordzap.exceptions;

public class InvalidStackOperationException extends Exception {

	public InvalidStackOperationException(String msg) {
		super(msg);
	}

}
