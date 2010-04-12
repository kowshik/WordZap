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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/*
 * 
 * Validates english words in Word Zap
 * 
 */
public class EnglishWordValidator implements WordValidator {

	// Stores all word lists - grouped by word length (key)
	private Map<Integer, Set<String>> wordListsHash;

	/*
	 * Constructs the validator from a list of words. Restricts caching of words
	 * to those that can be formed from chars specified in charSet. All other
	 * words in the word list stream is ignored.
	 * 
	 * Parameter 1 : Path to file containing list of words
	 * 
	 * Parameter 2 : Array of characters, which are the superset of characters
	 * in each word
	 * 
	 * Throws IOException : If I/O errors happen when reading the list of words
	 */
	public EnglishWordValidator(String wordListFile, final char[] charSet)
			throws IOException {
		this(new FileReader(wordListFile), charSet);
	}

	/*
	 * Constructs the validator from a list of words. Restricts caching of words
	 * to those that can be formed from chars specified in charSet. All other
	 * words in the word list stream is ignored.
	 * 
	 * Parameter 1 : Handle to file containing list of words
	 * 
	 * Parameter 2 : Array of characters, which are the superset of characters
	 * in each word
	 * 
	 * Throws IOException : If I/O errors happen when reading the list of words
	 */
	public EnglishWordValidator(final InputStream wordListHandle,
			final char[] charSet) throws IOException {
		this(new InputStreamReader(wordListHandle), charSet);
	}

	/*
	 * Constructs the validator from a list of words. Restricts caching of words
	 * to those that can be formed from chars specified in charSet. All other
	 * words in the word list stream is ignored.
	 * 
	 * Parameter 1 : Handle to file containing list of words
	 * 
	 * Parameter 2 : Array of characters, which are the superset of characters
	 * in each word
	 * 
	 * Throws IOException : If I/O errors happen when reading the list of words
	 */
	public EnglishWordValidator(final Reader wordListHandle,
			final char[] charSet) throws IOException {
		List<Character> charSetCollection = new Vector<Character>();
		for (char alphabet : charSet) {
			charSetCollection.add(Character.toUpperCase(alphabet));
		}
		this.wordListsHash = this.cacheWords(wordListHandle, charSetCollection);

	}

	/*
	 * Returns a hash with keys as word lengths and values as sorted lists of
	 * words of a particular length. Words are hashed only if characters in a
	 * word form a subset of characters in charSet
	 * 
	 * Parameter 1 : Handle to file containing list of words
	 * 
	 * Parameter 2 : List of characters, which are the superset of characters in
	 * each word
	 * 
	 * Throws IOException : If I/O errors happen when reading the list of words
	 */
	private Map<Integer, Set<String>> cacheWords(final Reader wordListHandle,
			final List<Character> charSet) throws IOException {
		BufferedReader buffRdr = new BufferedReader(wordListHandle);
		Map<Integer, Set<String>> wordListsHash = new HashMap<Integer, Set<String>>();

		String word = null;

		while ((word = buffRdr.readLine()) != null) {
			word = word.toUpperCase();
			if (this.isWordSubset(charSet, word)) {
				int wordLength = word.length();
				if (wordLength != 0) {
					Set<String> wordListSet = wordListsHash.get(wordLength);
					if (wordListSet == null) {
						wordListSet = new TreeSet<String>();
						wordListsHash.put(wordLength, wordListSet);
					}
					wordListSet.add(word);
				}
			}
		}
		return wordListsHash;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.wordzap.WordValidator#isWordValid(java.lang.String)
	 * 
	 * Returns true if word is valid, false otherwise
	 */
	public boolean isWordValid(String word) {
		word = word.toUpperCase();
		int wordLength = word.length();
		Set<String> wordListSet = wordListsHash.get(wordLength);
		if (wordListSet != null && wordListSet.contains(word)) {
			return true;
		}
		return false;
	}

	/*
	 * Returns true of characters in the word are a subset of characters in
	 * charSet. Returns false otherwise.
	 * 
	 * Parameter 1 : List of superset characters
	 * 
	 * Parameter 2 : Word that needs to be checked
	 */
	private boolean isWordSubset(final List<Character> charSet, String word) {
		int index = 0;
		List<Character> charSetTmp = new LinkedList<Character>(charSet);

		for (char alphabet : word.toCharArray()) {
			if (charSet.isEmpty() || !charSet.contains(alphabet)) {
				return false;
			}
			charSetTmp.remove(new Character(alphabet));
			word = word.substring(1, word.length());
			index++;
		}
		return true;
	}
}
