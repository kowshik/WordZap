/**
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

import com.android.wordzap.exceptions.InvalidLevelException;

/*
 * 
 * Captures the following details about any generated level :
 * 
 * (1) alphabets - List of alphabets presented to the human player
 * (2) cpu descriptor - Configuration of CPU (opposite player) for this level. Higher levels will have tougher opposite players.
 * 
 */

public class Level {

	private char[] alphabets;
	private CpuDescriptor aCpuDescriptor;
	private int levelNumber;

	public int getLevelNumber() {
		return levelNumber;
	}

	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}

	public char[] getAlphabets() {
		return alphabets;
	}

	public void setAlphabets(char[] alphabets) {
		this.alphabets = alphabets;
	}

	public CpuDescriptor getCpuDescriptor() {
		return aCpuDescriptor;
	}

	public void setCpuDescriptor(CpuDescriptor aCpuDescriptor) {
		this.aCpuDescriptor = aCpuDescriptor;
	}

	public Level(char[] alphabets, CpuDescriptor aCpuDescriptor, int levelNumber) {
		this.alphabets = alphabets.clone();
		this.aCpuDescriptor = (CpuDescriptor) aCpuDescriptor.clone();
		this.levelNumber=levelNumber;
	}

}
