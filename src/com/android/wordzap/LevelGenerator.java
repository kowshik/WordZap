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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import com.android.wordzap.exceptions.InvalidCpuDescriptionException;
import com.android.wordzap.exceptions.InvalidFreqFileException;
import com.android.wordzap.exceptions.InvalidLevelException;

/* 
 * This class generates levels for the game.
 * The class should be initialised with an input stream providing a list of alphabets and their frequency.
 * (See the documentation of the constructor for more information about this)
 * 
 * You can then use the LevelGenerator.generateLevel(int level) method to generate characters for a Word Zap level
 * 
 * NOTE : LevelGenerator currently works only with English alphabets ('A' to 'Z')
 * 
 * 
 */

public class LevelGenerator {

	public static final int LEVEL_SIZE = 8;
	public static final int MIN_LEVEL = 1;
	public static final int MAX_LEVEL = 53;

	private static final String VOWELS_STR = "AEIOU";
	private static List<Character> vowelsList;

	private static final String CONSONANTS_STR = "BCDFGHJKLMNPQRTVWXYZ";
	private static List<Character> consonantsStr;

	private static final char PLURAL_CHAR = 'S';

	private static final int NUM_CONSONANT_TIERS = 5;
	private static final int CONSONANT_TIER_SIZE = 4;

	private static final int[] numVowelsInLevels = { 4, 4, 4, 4, 3, 3, 3, 3, 3,
			3, 3, 3, 2, 3, 2, 3, 3, 3, 2, 3, 2, 2, 2, 2, 3, 2, 2, 2, 3, 2, 2,
			2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 3, 2, 2, 2, 3, 2, 2, 2, 3, 2, 2, 2 };
	private static final int[] numConsInTier1 = { 3, 2, 1, 1, 3, 2, 2, 1, 1, 1,
			0, 0, 1, 0, 1, 4, 0, 3, 3, 1, 4, 3, 3, 2, 1, 1, 0, 0, 0, 0, 0, 4,
			0, 3, 2, 1, 4, 4, 3, 3, 2, 2, 4, 4, 3, 3, 2, 2, 0, 0, 0, 0, 0 };
	private static final int[] numConsInTier2 = { 1, 1, 2, 1, 1, 2, 1, 2, 2, 1,
			4, 3, 2, 1, 1, 0, 2, 0, 0, 4, 2, 3, 2, 3, 2, 2, 4, 4, 2, 2, 1, 0,
			2, 0, 0, 4, 2, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 0, 4, 3, 1, 1, 0 };
	private static final int[] numConsInTier3 = { 0, 1, 1, 2, 0, 0, 1, 1, 2, 3,
			1, 1, 2, 2, 1, 0, 0, 0, 3, 0, 0, 0, 1, 1, 2, 3, 2, 1, 2, 2, 2, 1,
			1, 1, 4, 0, 0, 1, 1, 2, 2, 2, 0, 1, 2, 0, 2, 1, 0, 0, 0, 0, 2 };
	private static final int[] numConsInTier4 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 1, 1, 2, 3, 1, 3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 1,
			3, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 0 };
	private static final int[] numConsInTier5 = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 1, 1, 2, 2, 3, 2, 1, 3, 4, 4 };

	private static final int[][] numConsonantTiers = { numConsInTier1,
			numConsInTier2, numConsInTier3, numConsInTier4, numConsInTier5 };

	private static final boolean[] pluralsAllowed = { true, true, true, true,
			true, true, true, true, true, true, true, true, true, true, true,
			true, true, true, true, true, true, false, true, false, true,
			false, false, true, false, false, false, true, false, false, false,
			false, true, false, true, false, true, false, true, false, true,
			false, true, false, true, false, false, false, false };

	private static final int[] cpuMoveTimeIntervals = { 100, 90, 85, 80, 75,
			70, 65, 60, 55, 53, 50, 45, 45, 45, 45, 45, 45, 40, 40, 40, 40, 40,
			40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 40, 35, 35, 35, 35, 35,
			35, 35, 30, 30, 30, 30, 30, 30, 30, 30, 25, 25, 25, 25 };

	private static final int[] cpuMoveFrequencies = { 1, 1, 1, 3, 2, 2, 2, 2,
			2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
			4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 };

	private static final int[] cpuZapTimeIntervals = { 300, 294, 288, 282, 276,
			270, 264, 258, 252, 246, 240, 234, 228, 222, 216, 210, 204, 198,
			192, 186, 180, 174, 168, 162, 156, 150, 144, 138, 132, 126, 120,
			114, 108, 102, 96, 90, 84, 78, 72, 66, 60, 54, 48, 42, 36, 30, 30,
			30, 30, 30, 30, 30, 30 };

	private static final int[] cpuZapFrequencies = { 1, 2, 2, 3, 3, 3, 3, 4, 4,
			4, 4, 4, 4, 4, 4, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 9, 9, 9, 9,
			9, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13, 13,
			13, 13, 13, 13, 13 };

	private SortedMap<Character, Double> freqSortedConsonants;
	private List<List<Character>> levelConsonantTiers;

	private HumanLevelDescriptor[] allLevelDescriptors;

	// Comparator for sorting vowels and consonants based on frequency
	private class ValueSorter<K, V> implements Comparator<K> {
		private Map<K, V> mapObject;

		// Taking a reference to the map that needs to be sorted
		public ValueSorter(final Map<K, V> mapToBeSorted) {
			this.mapObject = mapToBeSorted;
		}

		// Comparison function
		public int compare(K keyOne, K keyTwo) {
			// System.out.println("here");
			double valueOne = (Double) (mapObject.get(keyOne));
			double valueTwo = (Double) (mapObject.get(keyTwo));
			if (valueTwo < valueOne) {
				return -1;
			}
			if (valueTwo > valueOne) {
				return 1;
			}
			return 0;
		}
	};

	static {
		// Initialising all vowels in english
		LevelGenerator.vowelsList = new Vector<Character>();
		for (char vowel : LevelGenerator.VOWELS_STR.toCharArray()) {
			LevelGenerator.vowelsList.add(vowel);
		}

		// Initialising all consonants in english
		LevelGenerator.consonantsStr = new Vector<Character>();
		for (char consonant : LevelGenerator.CONSONANTS_STR.toCharArray()) {
			LevelGenerator.consonantsStr.add(consonant);
		}

	}

	/*
	 * 
	 * Generates characters for a required level.
	 * 
	 * Parameter : Level for which characters have to be generated
	 * 
	 * Returns : An array of characters. Size of the array returned is always
	 * equal to LevelGenerator.LEVEL_SIZE.
	 * 
	 * Throws InvalidLevelException : if the level parameter passed doesnt fall
	 * between LevelGenerator.MIN_LEVEL and LevelGenerator.MAX_LEVEL
	 */

	public Level generateLevel(int level) throws InvalidLevelException,
			InvalidCpuDescriptionException {

		if (level < LevelGenerator.MIN_LEVEL
				|| level > LevelGenerator.MAX_LEVEL) {
			throw new InvalidLevelException(
					"Level should be within the range : LevelGenerator.MIN_LEVEL and LevelGenerator.MAX_LEVEL");
		}

		// Fetching the required level
		level--;
		HumanLevelDescriptor thisLevel = allLevelDescriptors[level];

		CpuDescriptor thisCpuLevel = new CpuDescriptor(
				cpuMoveTimeIntervals[level], cpuMoveFrequencies[level],
				cpuZapTimeIntervals[level], cpuZapFrequencies[level]);

		int numLevelVowels = thisLevel.getNumberOfVowels();

		Random rand = new Random();

		/*
		 * *********************************************
		 * Generating list of vowels to be returned
		 * *********************************************
		 */

		// This List will contain final list of chars to be returned
		List<Character> finalChars = new Vector<Character>();

		// Set that will contain final list of vowels to be returned
		Set<Character> thisLevelVowels = new HashSet<Character>();
		while (thisLevelVowels.size() < numLevelVowels) {

			/*
			 * Choosing the required number of vowels for this levels at random
			 * from the set of all vowels
			 */
			int randIndex = rand.nextInt(vowelsList.size());
			thisLevelVowels.add(LevelGenerator.vowelsList.get(randIndex));
		}

		// Adding all generated vowels to final list
		finalChars.addAll(thisLevelVowels);

		/*
		 * *********************************************
		 * Generating list of consonants to be returned
		 * *********************************************
		 */

		Set<Character> thisLevelTierConsonants = new HashSet<Character>();

		// This List will contain final list of consonants to be returned
		List<Character> finalConsonants = new Vector<Character>();
		int tierIndex = 0;
		for (int numTierConsonant : thisLevel.getNumberOfConsonants()) {
			thisLevelTierConsonants.clear();
			List<Character> aConsonantTier = levelConsonantTiers.get(tierIndex);
			while (thisLevelTierConsonants.size() < numTierConsonant) {

				/*
				 * Choosing the required number of consonants for this ier at
				 * random from the set of all consonants in this tier
				 */
				int randIndex = rand.nextInt(aConsonantTier.size());
				thisLevelTierConsonants.add(aConsonantTier.get(randIndex));
			}

			tierIndex++;
			finalConsonants.addAll(thisLevelTierConsonants);
		}

		/*
		 * *****************************************************
		 * Checking if LetterGenerator.PLURAL_CHAR can be included at this level
		 * *****************************************************
		 */

		if (thisLevel.isPluralAllowed()) {
			int randIndex = rand.nextInt(finalConsonants.size());

			/*
			 * if plural is allowed, some consonant char is randomly removed and
			 * replaced with LetterGenerator.PLURAL_CHAR
			 */
			finalConsonants.remove(randIndex);
			finalConsonants.add(LevelGenerator.PLURAL_CHAR);
		}

		// Forming final list
		finalChars.addAll(finalConsonants);
		char[] levelAlphabets = new char[finalChars.size()];
		int index = 0;
		for (Character c : finalChars) {
			levelAlphabets[index] = c;
			index++;
		}

		return new Level(levelAlphabets,thisCpuLevel);
	}

	/*
	 * Allows initialising the class with a specific list of english alphaets
	 * containing frequencies of the set of alphabets that will be used during
	 * the game.
	 * 
	 * The constructor uses this list to generate various levels
	 * 
	 * Parameter 1 : Name of file containing list of letters along with their
	 * frequencies. Example of such a file : A-8.167 B-1.492 C-2.782 D-4.253
	 * E-12.702 F-2.228 G-2.015 H-6.094 I-6.966 J-0.153 K-0.772 L-4.025 M-2.406
	 * N-6.749 O-7.507 P-1.929 Q-0.095 R-5.987 S-6.327 T-9.056 U-2.758 V-0.978
	 * W-2.360 X-0.150 Y-1.974 Z-0.074
	 * 
	 * (Replace whitespaces with end of line in the above example)
	 * 
	 * Parameter 2 : File delimiter for the above file.
	 * 
	 * Throws IOException : If an IOException occurs during I/O with file
	 * mentioned in Parameter 1.
	 * 
	 * Throws InvalidFreqFileException : If data in file mentioned in Parameter
	 * 1 is incorrect.
	 */
	public LevelGenerator(String freqFile, String delimiter)
			throws IOException, InvalidFreqFileException {

		this(new FileReader(freqFile), delimiter);
	}

	/*
	 * Allows initialising the class with a specific list of english alphaets
	 * containing frequencies of the set of alphabets that will be used during
	 * the game.
	 * 
	 * The constructor uses this list to generate various levels
	 * 
	 * Parameter 1 : Handle to file containing list of letters along with their
	 * frequencies. Example of such a file : A-8.167 B-1.492 C-2.782 D-4.253
	 * E-12.702 F-2.228 G-2.015 H-6.094 I-6.966 J-0.153 K-0.772 L-4.025 M-2.406
	 * N-6.749 O-7.507 P-1.929 Q-0.095 R-5.987 S-6.327 T-9.056 U-2.758 V-0.978
	 * W-2.360 X-0.150 Y-1.974 Z-0.074
	 * 
	 * (Replace whitespaces with end of line in the above example)
	 * 
	 * Parameter 2 : File delimiter for the above file.
	 * 
	 * Throws IOException : If an IOException occurs during I/O with file
	 * mentioned in Parameter 1.
	 * 
	 * Throws InvalidFreqFileException : If data in file mentioned in Parameter
	 * 1 is incorrect.
	 */

	public LevelGenerator(final InputStream freqFileStream, String delimiter)
			throws IOException, InvalidFreqFileException {

		this(new InputStreamReader(freqFileStream), delimiter);
	}

	/*
	 * Allows initialising the class with a specific list of english alphaets
	 * containing frequencies of the set of alphabets that will be used during
	 * the game.
	 * 
	 * The constructor uses this list to generate various levels
	 * 
	 * Parameter 1 : Handle to file containing list of letters along with their
	 * frequencies. Example of such a file : A-8.167 B-1.492 C-2.782 D-4.253
	 * E-12.702 F-2.228 G-2.015 H-6.094 I-6.966 J-0.153 K-0.772 L-4.025 M-2.406
	 * N-6.749 O-7.507 P-1.929 Q-0.095 R-5.987 S-6.327 T-9.056 U-2.758 V-0.978
	 * W-2.360 X-0.150 Y-1.974 Z-0.074
	 * 
	 * (Replace whitespaces with end of line in the above example)
	 * 
	 * Parameter 2 : File delimiter for the above file.
	 * 
	 * Throws IOException : If an IOException occurs during I/O with file
	 * mentioned in Parameter 1.
	 * 
	 * Throws InvalidFreqFileException : If data in file mentioned in Parameter
	 * 1 is incorrect.
	 */

	public LevelGenerator(final Reader freqFileHandle, String delimiter)
			throws IOException, InvalidFreqFileException {

		// Parsing alphabet-frequency stream
		Map<Character, Double> freqHash = this.parseFrequencies(freqFileHandle,
				delimiter);

		/*
		 * Temp hash object to store consonants and their frequency before
		 * sorting
		 */
		Map<Character, Double> consonantsTmpHash = new HashMap<Character, Double>();

		// Segregating consonants from other alphabets
		for (Map.Entry<Character, Double> freqEntry : freqHash.entrySet()) {
			if (consonantsStr.contains(freqEntry.getKey())) {
				consonantsTmpHash.put(freqEntry.getKey(), freqEntry.getValue());
			}
		}

		// Generating frequency sorted consonants
		this.freqSortedConsonants = new TreeMap<Character, Double>(
				new ValueSorter<Character, Double>(consonantsTmpHash));
		this.freqSortedConsonants.putAll(consonantsTmpHash);

		// Filling up consonants into different tiers based on frequency
		levelConsonantTiers = new Vector<List<Character>>();

		int index = 0;
		int listIndex = 0;

		// Filling list with an empty Vector for each consonant tier
		while (listIndex < LevelGenerator.NUM_CONSONANT_TIERS) {
			levelConsonantTiers.add(new Vector<Character>());
			listIndex++;
		}

		/*
		 * The top LevelGenerator.CONSONANT_TIER_SIZE number of consonants go to
		 * tier 1 The next LevelGenerator.CONSONANT_TIER_SIZE number of
		 * consonants go to tier 2 The next LevelGenerator.CONSONANT_TIER_SIZE
		 * number of consonants go to tier 3 and so on upto total number of
		 * consonants : LevelGenerator.NUM_CONSONANT_TIERS
		 */
		index = listIndex = 0;
		for (Map.Entry<Character, Double> entry : freqSortedConsonants
				.entrySet()) {
			Character consonant = entry.getKey();

			List<Character> freqTier = levelConsonantTiers.get(listIndex);
			freqTier.add(consonant);

			index++;
			if (index % LevelGenerator.CONSONANT_TIER_SIZE == 0) {
				listIndex++;
			}

		}

		// Initiating array of Levels
		this.allLevelDescriptors = new HumanLevelDescriptor[MAX_LEVEL];

		// Generating all levels here
		for (int levelIndex = LevelGenerator.MIN_LEVEL - 1; levelIndex < LevelGenerator.MAX_LEVEL; levelIndex++) {
			int[] levelConsonantTierNum = new int[NUM_CONSONANT_TIERS];
			for (int tierIndex = 0; tierIndex < NUM_CONSONANT_TIERS; tierIndex++) {
				levelConsonantTierNum[tierIndex] = numConsonantTiers[tierIndex][levelIndex];

			}
			try {
				this.allLevelDescriptors[levelIndex] = new HumanLevelDescriptor(
						numVowelsInLevels[levelIndex], levelConsonantTierNum,
						pluralsAllowed[levelIndex]);
			} catch (InvalidLevelException e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * This methods parses a specific list of english alphaets containing
	 * frequencies of the set of alphabets that will be used during the game.
	 * 
	 * 
	 * Parameter 1 : Handle to file containing list of letters along with their
	 * frequencies. Example of such a file : A-8.167 B-1.492 C-2.782 D-4.253
	 * E-12.702 F-2.228 G-2.015 H-6.094 I-6.966 J-0.153 K-0.772 L-4.025 M-2.406
	 * N-6.749 O-7.507 P-1.929 Q-0.095 R-5.987 S-6.327 T-9.056 U-2.758 V-0.978
	 * W-2.360 X-0.150 Y-1.974 Z-0.074
	 * 
	 * (Replace whitespaces with end of line in the above example)
	 * 
	 * Parameter 2 : File delimiter for the above file.
	 * 
	 * Throws IOException : If an IOException occurs during I/O with file
	 * mentioned in Parameter 1.
	 * 
	 * Throws InvalidFreqFileException : If data in file mentioned in Parameter
	 * 1 is incorrect.
	 */

	private Map<Character, Double> parseFrequencies(
			final Reader freqFileHandle, String delimiter) throws IOException,
			InvalidFreqFileException {
		// Attempting to read from file containing frequency mapping
		// BufferedReader
		BufferedReader buffRdr = new BufferedReader(freqFileHandle);
		String line = "";
		boolean emptyFile = true;
		Map<Character, Double> freqHash = new HashMap<Character, Double>();
		while ((line = buffRdr.readLine()) != null) {
			emptyFile = false;
			String[] fields = line.split(delimiter);

			// Number of fields should always be two
			if (fields.length != 2) {
				throw new InvalidFreqFileException(
						"Each line in the frequency mapping file should contain exactly two fields.");
			}

			String alphabetStr = fields[0].trim().toUpperCase();
			String frequency = fields[1].trim().toUpperCase();

			if (alphabetStr.length() == 0 || frequency.length() == 0) {
				throw new InvalidFreqFileException(
						"The frequency mapping file cannot contain entries with length zero.");
			}
			if (alphabetStr.length() != 1) {
				throw new InvalidFreqFileException(
						"The frequency mapping file cannot contain alphabet entries with length not equal to 1.");
			}
			Character alphabet = alphabetStr.charAt(0);

			// Oops .. file cant contain duplicate alphabets
			if (freqHash.containsKey(alphabet)) {
				throw new InvalidFreqFileException(
						"The frequency mapping file cannot contain duplicate entries for alphabets");
			}

			// Oops .. file cant contain non-english characters
			if (!alphabet.equals(LevelGenerator.PLURAL_CHAR)
					&& !vowelsList.contains(alphabet)
					&& !consonantsStr.contains(alphabet)) {
				throw new InvalidFreqFileException(
						"The frequency mapping file cannot contain non-english characters");
			}
			try {
				// Looks like the second fields isnt a valid double value
				freqHash.put(alphabet, Double.parseDouble(frequency));
			} catch (NumberFormatException ex) {
				throw new InvalidFreqFileException(
						"An error occured while parsing a 'Double' frequency value from the frequency mapping file.",
						ex);

			}
		}
		if (emptyFile) {
			throw new InvalidFreqFileException(
					"The frequency mapping file cannot be empty.");

		}
		return freqHash;
	}
}
