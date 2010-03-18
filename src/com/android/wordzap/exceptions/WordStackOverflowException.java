/**
 * 
 * @author Kowshik Prakasam
 * 
 * Exception to represent stack overflows
 *
 */

package com.android.wordzap.exceptions;

public class WordStackOverflowException extends Exception {

	private char letter;
	private int wordLimit;

	public WordStackOverflowException(char letter, int wordLimit) {
		this.letter = letter;
		this.wordLimit = wordLimit;
	}

	public String toString() {
		return "Stack exceeded limit : " + wordLimit
				+ " during addition of letter : " + letter;
	}

}
