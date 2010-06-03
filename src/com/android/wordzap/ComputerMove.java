/**
 *  
 * @author Kowshik Prakasam
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

package com.android.wordzap;

/*
 * 
 * Defines a move made by the opposite player (Computer) using three attributes :
 * 
 * (1) isZapMove : Will the computer zap a word from the human player's letter grid during this move ?
 * (2) isGenWordMove : Will the computer player generate a new word against the human player during this move ?
 * (3) time : At what time (in seconds) will the computer make its move ?
 * 
 */

public class ComputerMove {
	private boolean isZapMove;
	private boolean isGenWordMove;
	private int time;

	// Getters,Setters for all attributes
	public boolean isZapMove() {
		return isZapMove;
	}

	public void setZapMove(boolean zapMove) {
		this.isZapMove = zapMove;
	}

	public boolean isGenWordMove() {
		return isGenWordMove;
	}

	public void setGenWordMove(boolean genWordMove) {
		this.isGenWordMove = genWordMove;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public ComputerMove(boolean zapMove, boolean genWordMove, int time) {
		this.isZapMove = zapMove;
		this.isGenWordMove = genWordMove;
		this.time = time;

	}

	// Describes a computer move
	@Override
	public String toString() {
		return "\n\nZap Move : " + isZapMove + "\nGenWord Move : "
				+ isGenWordMove + "\nTime : " + time + "\n\n";
	}
}
