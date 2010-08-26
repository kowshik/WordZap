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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * This class checks if human player has made a move once in every WordZapConstants.HUMAN_SLEEP_CHECK milliseconds. 
 * If there is no response, then this thread sends a termination message to the
 * parent thread : com.android.wordzap.GameScreen, indicating that the human player has lost the game.
 */
public class Timer implements Runnable {

	// Handler object to communicate opponent moves to Activity GameScreen
	// This object is created in Activity com.android.wordzap.GameScreen
	private final Handler mainThreadHandler;

	// The last word formed by the human player
	private String lastWord;

	// Handle to the Activity object representing the WordZap Game Screen
	private final GameScreen wordZapGameScreen;

	public Timer(GameScreen wordZapGameScreen, Handler mainThreadHandler)
			throws NullPointerException {

		if (wordZapGameScreen == null) {
			throw new IllegalArgumentException("GameScreen object is null.");
		}
		this.wordZapGameScreen = wordZapGameScreen;

		if (mainThreadHandler == null) {
			throw new IllegalArgumentException("Main Thread Handler is null.");
		}
		this.mainThreadHandler = mainThreadHandler;

		this.lastWord = "";
	}

	public void run() {

		while (true) {
			try {

				Log.i("Timer", "Gonna sleep for : "
						+ (WordZapConstants.HUMAN_SLEEP_CHECK / 1000) + "s");

				/********
				 * Sleep until interrupted by method
				 * com.android.wordzap.GameScreen.endWord()
				 *****************/
				Thread.sleep(WordZapConstants.HUMAN_SLEEP_CHECK);
				/************************************************/

				Log.i("Timer", "Woke up");

				// Check again if game was already over during sleep time
				if (this.wordZapGameScreen.isGameOver()) {
					Log.i("Timer", "Dying");
					return;
				}

				Bundle aBundle = new Bundle();
				int time;

				/**
				 * Run the timer in the Game Screen UI until : The game is over
				 * due to no response from human player (OR) Human player
				 * interrupts by forming a valid word
				 */
				for (time = WordZapConstants.HUMAN_SLEEP_CHECK; time >= 1000
						&& this.lastWord.equals(this.wordZapGameScreen
								.getLastWord())
						&& !wordZapGameScreen.isGameOver(); time -= 1000) {

					/*
					 * Clear bundle and populate with timer value to be
					 * displayed in com.android.wordzap.GameScreen UI
					 */
					aBundle.clear();
					aBundle.putInt(WordZapConstants.TIMER_VALUE_KEYNAME,
							time / 1000);
					aBundle.putBoolean(WordZapConstants.GAME_OVER, false);

					// Obtain message to be sent
					Message msg = mainThreadHandler.obtainMessage();
					msg.setData(aBundle);
					mainThreadHandler.sendMessage(msg);

					/*****************************/
					Thread.sleep(1000);
					/*****************************/

				}

				/*
				 * Oops, game is over because human player failed to respond
				 * with a valid word
				 */
				if (time < 1000) {
					aBundle.putBoolean(WordZapConstants.GAME_OVER, true);
					Message msg = mainThreadHandler.obtainMessage();
					msg.setData(aBundle);
					mainThreadHandler.sendMessage(msg);
				}

			} catch (InterruptedException e) {
				Log.i("Timer", "Interrupted");
				// Don't care as timer is just reset by human player
			}

			// Remember the last word no matter what interrupts occur
			finally {
				this.lastWord = this.wordZapGameScreen.getLastWord();
			}
		}

	}
}
