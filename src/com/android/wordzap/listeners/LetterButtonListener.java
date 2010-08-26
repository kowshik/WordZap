/**
 *
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


package com.android.wordzap.listeners;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.wordzap.GameScreen;
import com.android.wordzap.WordZapConstants;
import com.android.wordzap.exceptions.InvalidStackOperationException;
import com.android.wordzap.exceptions.WordStackOverflowException;


/*
 * Code that listens to click operations on letter buttons in the visual game screen of the human player
 */

public class LetterButtonListener implements OnClickListener {

	private final GameScreen theGameScreen;

	// Ties the GameScreen to this listener
	public LetterButtonListener(GameScreen theGameScreen) {
		this.theGameScreen = (GameScreen) theGameScreen;

	}

	public void onClick(View v) {
		Button letterButton = (Button) v;

		// Clear any existing messages
		theGameScreen.clearErrorMessage();
		try {
			// Update the grid
			theGameScreen.pushToVisualGrid(letterButton);

			/*
			 * Kill this button temporarily It cannot be reused by the human
			 * player until end word is clicked or when this word is reverted
			 */
			letterButton.setVisibility(View.INVISIBLE);
		} catch (WordStackOverflowException e) {
			// Beep if there is a runtime error
			theGameScreen.beep(WordZapConstants.CANT_PRESS_LETTER_BEEP);
		} catch (InvalidStackOperationException e) {
			// Beep if there is a runtime error
			theGameScreen.beep(WordZapConstants.CANT_PRESS_LETTER_BEEP);
		}
	}
}
