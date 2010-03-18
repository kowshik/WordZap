/**
 * @author Kowshik Prakasam
 * 
 * Exception class to represent invalid contents in the file mapping each alphabet to its frequency of occurence in its language
 */
package com.android.wordzap.exceptions;

public class InvalidFreqFileException extends Exception {

	public InvalidFreqFileException(String msg) {
		super(msg);
	}

	public InvalidFreqFileException(String msg, NumberFormatException otherEx) {
		super(msg, otherEx);
	}

}
