/*
 * @author Kowshik Prakasam
 * 
 * Interface to be implemented by any WordValidator
 * 
 * You can use this interface to design Word Validators for different languages in the future
 * Just write a class that implements this interface and pass it as a parameter to constructor of class LetterGrid
 * 
 */
package com.android.wordzap;

public interface WordValidator {

	// Returns true if word is valid, false otherwise
	boolean isWordValid(String word);

}
