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

import com.android.wordzap.exceptions.InvalidLevelException;

/* 
 * Denotes a Level in the Word Zap game using the following attributes :
 * 
 * (1) int numVowels : the number of vowels allowed in the character set for this level
 * (2) int[] numConsonants : Each number in this array refers to the number of consonants 
 *                           allowed from each consonant tier generated from consonant frequency list 
 *                           (See class LevelGenerator for more information)
 * (3) isPluralAllowed : Does the char set for this level contain the char used to form plurals. If yes, word formation becomes easier as usage of plurals is allowed.
 * 
 * Used by class LevelGenerator to store levels
 * Provides getter / setters to all level attributes
 * 
 */

public class Level {

	private int numVowels;
	private int[] numConsonants;
	private boolean isPluralAllowed;

	public boolean isPluralAllowed() {
		return isPluralAllowed;
	}

	public void dontAllowPlural(boolean isPluralAllowed) {
		this.isPluralAllowed = isPluralAllowed;
	}

	public int[] getNumberOfConsonants() {
		return numConsonants;
	}

	public void setNumberOfConsonants(int[] numConsonants) {
		this.numConsonants = numConsonants;
	}

	public int getNumberOfVowels() {
		return numVowels;
	}

	public void setNumberOfVowels(int numVowels) {
		this.numVowels = numVowels;
	}

	public Level(int numVowels, int[] numConsonants, boolean isPluralAllowed)
			throws InvalidLevelException {

		// numVowels cant be negative
		if (numVowels > 0) {
			this.numVowels = numVowels;
		} else {
			throw new InvalidLevelException(
					"Number of vowels has to be greater than zero.");
		}

		// All numConsonants value should be positive
		for (int value : numConsonants) {
			if (value < 0) {
				throw new InvalidLevelException(
						"Each value in number of consonants has to be >= zero.");
			}
		}
		this.numConsonants = numConsonants.clone();
		this.isPluralAllowed = isPluralAllowed;
	}

	// Overriding toString() to provide a String representation of any level
	public String toString() {
		String description = "";
		description += "Number of vowels : " + this.getNumberOfVowels();
		description += "\nNumber of consonants : "
				+ this.getNumConsonantsDesc();
		description += "\nPlurals Allowed ? : " + this.isPluralAllowed();

		return description;
	}

	// Returns String description of numConsonants attribute
	private String getNumConsonantsDesc() {
		String description = "{ ";
		for (int index = 0; index < numConsonants.length; index++) {
			if (index != 0) {
				description += ", ";
			}
			description += numConsonants[index];
		}
		description += " }";
		return description;
	}
}
