package com.android.wordzap;

public class WordStackOverflowException extends Exception {

	

	private char letter;
	private int wordLimit;

	public WordStackOverflowException(char letter, int wordLimit) {
		this.letter = letter;
		this.wordLimit = wordLimit;
	}
	
	public String toString(){
		return "WordStack exceeded word limit : "+wordLimit+" during addition of letter : "+letter;
	}

}
