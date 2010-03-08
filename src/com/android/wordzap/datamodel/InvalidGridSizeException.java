/**
 * 
 * @author Kowshik Prakasam
 * 
 * This exception is thrown when class LetterGrid is initialised with invalid size
 * 
 */

package com.android.wordzap.datamodel;

public class InvalidGridSizeException extends Exception {
	private int numRows;
	private int numCols;

	public InvalidGridSizeException(int numRows, int numCols) {
		this.numRows = numRows;
		this.numCols = numCols;
	}

	public String toString() {
		String message = "";
		if (numRows < 0 || numCols < 0) {
			message = "Letter grid size cannot have negative dimensions";
			if (numRows < 0) {
				message += "\nNumber of rows : " + numRows
						+ " should have a positive value";
			}
			if (numCols < 0) {
				message += "\nNumber of columns : " + numCols
						+ " should have a positive value";
			}
		}
		return message;
	}

}
