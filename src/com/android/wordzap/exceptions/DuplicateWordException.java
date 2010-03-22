/**
 * @author Kowshik Prakasam
 * 
 * Used to represent conditions where the player attempts to insert duplicates word into the grid
 * 
 */
package com.android.wordzap.exceptions;

public class DuplicateWordException extends Exception {
	public DuplicateWordException(String msg) {
		super(msg);
	}
}
