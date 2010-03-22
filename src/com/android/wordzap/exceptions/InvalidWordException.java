/**
 * @author Kowshik Prakasam
 * 
 * Used to represent conditions where the player attempts to insert invalid word into the grid
 * 
 */
package com.android.wordzap.exceptions;

public class InvalidWordException extends Exception {
	public InvalidWordException(String msg){
		super(msg);
	}
}
