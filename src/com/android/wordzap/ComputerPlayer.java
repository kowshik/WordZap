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

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * Models the opponent player which executes in a separate thread
 */
public class ComputerPlayer implements Runnable {

	// Handler object to communicate opponent moves to Activity GameScreen
	// This object is created in Activity com.android.wordzap.GameScreen
	private final Handler mainThreadHandler;

	// Handle to the Activity object representing the WordZap Game Screen
	private final GameScreen wordZapGameScreen;

	// WordCache to get list of words to be generated during computer moves
	private final WordCache aWordCache;

	// Current level of the game
	private final Level currentLevel;

	// A list of premeditated computer moves for the entire level based on
	// difficulty level
	private List<ComputerMove> computerMoves;

	// Number of rounds of computer moves that are generated before hand
	private static final int NUM_MOVES_GENERATED = 10;

	public ComputerPlayer(GameScreen wordZapGameScreen, WordCache aWordCache,
			Level currentLevel, Handler mainThreadHandler)
			throws NullPointerException {

		if (wordZapGameScreen == null) {
			throw new IllegalArgumentException("GameScreen object is null.");
		}
		this.wordZapGameScreen = wordZapGameScreen;

		if (mainThreadHandler == null) {
			throw new IllegalArgumentException("Main Thread Handler is null.");
		}
		this.mainThreadHandler = mainThreadHandler;

		if (aWordCache == null) {
			throw new IllegalArgumentException("WordCache is null.");
		}
		this.aWordCache = aWordCache;

		if (currentLevel == null) {
			throw new IllegalArgumentException("Level is null.");
		}
		this.currentLevel = currentLevel;

		// Premeditate all moves for this level
		this.populateAllMoves();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * 
	 * Runs the computer player in a separate thread
	 */
	public void run() {
		try {

			// Run this thread until the game is over
			for (int moveIndex = 0; moveIndex < this.computerMoves.size() - 1; moveIndex++) {

				/*
				 * Don't proceed if game is over already (i.e. if computer or
				 * human has won already)
				 */
				if (this.wordZapGameScreen.isGameOver()) {
					Log.i("ComputerPlayer", "Dying");
					return;
				}

				// Get the current move
				ComputerMove thisMove = this.computerMoves.get(moveIndex);

				// Get the next move to calculate the sleep time between current
				// move and next move
				ComputerMove nextMove = this.computerMoves.get(moveIndex + 1);

				// sleepTime is the number of seconds for which computer will
				// sleep before making the next move
				int sleepTime = 0;
				if (moveIndex == 0) {
					// For the first move, sleep time is defined in the move
					sleepTime = thisMove.getTime();
				} else {
					/*
					 * For all other moves, sleep time is difference between
					 * sleep time of current move and next move
					 */
					sleepTime = nextMove.getTime() - thisMove.getTime();
				}
				Log.i("ComputerPlayer", "Gonna sleep for : " + sleepTime + "s");

				// Sleep until next move

				/*****************************/
				Thread.sleep(sleepTime * 1000);
				/****************************/

				// Check again if game was already over during sleep time
				if (this.wordZapGameScreen.isGameOver()) {
					Log.i("ComputerPlayer", "Dying");
					return;
				}
				Log.i("ComputerPlayer", "Woke up");

				// Communicate computer move to Activity GameScreen
				Bundle aBundle = packComputerMove(thisMove);

				// Obtain message to be sent
				Message msg = mainThreadHandler.obtainMessage();
				msg.setData(aBundle);
				mainThreadHandler.sendMessage(msg);

				// Reset loop index so that the method loops for ever
				if (moveIndex == this.computerMoves.size() - 1) {
					moveIndex = 0;
				}

			}
		} catch (InterruptedException e) {
			//Don't care as the thread is interrupted during a human win
			Log.i("ComputerPlayer","Interrupted");
		}

	}

	/*
	 * Find what moves the computer should make, and populate the bundle
	 * accordingly A computer move may contain any,none or both of the following
	 * :
	 * 
	 * (1) Zap move : Computer zaps a word from the human player's visual grid
	 * (2) Gen Word move : Computer generates a word in its own grid against the
	 * human player. This generated word is also packed in the Bundle returned.
	 */
	private Bundle packComputerMove(ComputerMove thisMove) {
		Bundle aBundle = new Bundle();
		if (thisMove.isZapMove()) {
			Log.i("ComputerPlayer", "Packed zap move");
			aBundle.putBoolean(WordZapConstants.ZAP_MOVE_KEYNAME, true);
		} else {
			aBundle.putBoolean(WordZapConstants.ZAP_MOVE_KEYNAME, false);
		}

		if (thisMove.isGenWordMove()) {
			Log.i("ComputerPlayer", "Packed gen word move");
			aBundle.putBoolean(WordZapConstants.GENWORD_MOVE_KEYNAME, true);
			String randomWord=this.aWordCache.getRandomWord(this.wordZapGameScreen.getCompletedWords());
			aBundle.putString(WordZapConstants.GENERATED_WORD_KEYNAME, randomWord);
		} else {
			aBundle.putBoolean(WordZapConstants.GENWORD_MOVE_KEYNAME, false);
		}
		return aBundle;
	}

	/*
	 * Merges sorted list of timings for zap and genword moves generated by the
	 * method : com.android.wordzap.ComputerPlayer.generateMoveTimings(int
	 * endTime, int frequency) into a single list. Implementation is similar to
	 * the classical merge algorithm in 'merge sort'.
	 * 
	 * Parameters :
	 * 
	 * (1) baseTimevalue : A time value that needs to be added to the time
	 * attribute of every ComputerMove in the merged list. This will be
	 * particularly used in generating the second, third, fourth etc. moves
	 * (limited by com.android.wordzap.ComputerPlayer.NUM_MOVES_GENERATED).
	 * 
	 * Example : The time attribute of the last ComputerMove object in the first
	 * round of moves, will be added to the time attribute for each ComputerMove
	 * object in the second round of moves.
	 * 
	 * (2) zapTimings : A list of zap ComputerMove objects
	 * 
	 * (3) genWordTimings : A list of gen word ComputerMove objects
	 * 
	 * Returns : A merged list of ComputerMove objects obtained from lists in
	 * parameters (2) and (3)
	 */

	public List<ComputerMove> generateMoves(int baseTimeValue,
			final List<Integer> zapTimings, final List<Integer> genWordTimings) {

		// Final list of merged ComputerMove objects
		List<ComputerMove> computerMoves = new Vector<ComputerMove>();

		// zapIndex iterates through zapTimings list
		// genWordIndex iterates through genWordTimings list
		int zapIndex = 0, genWordIndex = 0;

		while (zapIndex < zapTimings.size()
				&& genWordIndex < genWordTimings.size()) {
			int zapMoveTime = baseTimeValue + zapTimings.get(zapIndex);
			int genWordMoveTime = baseTimeValue
					+ genWordTimings.get(genWordIndex);

			// Move zapIndex by one to close in on genWordIndex
			if (zapMoveTime < genWordMoveTime) {
				computerMoves.add(new ComputerMove(true, false, zapMoveTime));
				zapIndex++;
				continue;
			}
			// Move genWordIndex and zapIndex ahead by one
			else if (zapMoveTime == genWordMoveTime) {
				computerMoves.add(new ComputerMove(true, true, zapMoveTime));
				zapIndex++;
				genWordIndex++;
			}
			// Move genWordIndex by one to close in on zapIndex
			else {
				computerMoves
						.add(new ComputerMove(false, true, genWordMoveTime));
				genWordIndex++;
			}
		}

		// Merge any left over zap moves
		while (zapIndex < zapTimings.size()) {
			computerMoves.add(new ComputerMove(true, false, zapTimings
					.get(zapIndex)
					+ baseTimeValue));
			zapIndex++;
		}

		// Merge any left over gen word moves
		while (genWordIndex < genWordTimings.size()) {
			computerMoves.add(new ComputerMove(false, true, genWordTimings
					.get(genWordIndex)
					+ baseTimeValue));
			genWordIndex++;
		}

		return computerMoves;
	}

	/*
	 * Produces a list of random computer moves based on frequency of moves, and
	 * time length. Here, the computer move timings are randomized, but NOT the
	 * type of the move (zap/gen word).
	 * 
	 * Parameters :
	 * 
	 * (1) endTime : A list of move timings will be generated from 1 to endTime
	 * (2) frequency : Number of move timings in the list generated using
	 * parameter (1)
	 * 
	 * Returns :
	 * 
	 * A list of move timings. Size of list = frequency (parameter 2).
	 * 
	 * 
	 * Example : If endTime = 10, and frequency = 4, then the following are
	 * examples of random lists of move timings returned by this method :
	 * 
	 * [3,5,7,9] and [1,5,7,9] and [1,2,5,9]
	 * 
	 * 
	 * etc.
	 */
	public List<Integer> generateMoveTimings(int endTime, int frequency)
			throws IllegalArgumentException {

		if (frequency <= 0) {
			throw new IllegalArgumentException("Frequency should be positive");
		}

		Random randObj = new Random();
		Set<Integer> moves = new TreeSet<Integer>();
		int moveIndex = 1;
		do {
			moves.add(randObj.nextInt(endTime));
			moveIndex = moves.size();
		} while (moveIndex < frequency);

		return new Vector<Integer>(moves);
	}

	/*
	 * Populates the class attribute : computerMoves with several round of
	 * computer moves limited by
	 * com.android.wordzap.ComputerPlayer.NUM_MOVES_GENERATED
	 */
	public void populateAllMoves() {
		this.computerMoves = new Vector<ComputerMove>();

		int cpuMoveTime = currentLevel.getCpuDescriptor().getCpuMoveTime();
		int cpuMoveFreq = currentLevel.getCpuDescriptor().getCpuMoveFreq();
		int cpuZapTime = currentLevel.getCpuDescriptor().getCpuZapTime();
		int cpuZapFreq = currentLevel.getCpuDescriptor().getCpuZapFreq();

		int baseTimeValue = 0;

		for (int i = 0; i < ComputerPlayer.NUM_MOVES_GENERATED; i++) {
			List<Integer> zapTimings = this.generateMoveTimings(cpuMoveTime,
					cpuMoveFreq);
			List<Integer> genWordTimings = this.generateMoveTimings(cpuZapTime,
					cpuZapFreq);
			this.computerMoves.addAll(this.generateMoves(baseTimeValue,
					zapTimings, genWordTimings));
			if (computerMoves.size() != 0) {
				baseTimeValue = computerMoves.get(computerMoves.size() - 1)
						.getTime();
			}

		}

	}

}
