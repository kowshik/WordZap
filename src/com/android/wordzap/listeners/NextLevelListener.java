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

package com.android.wordzap.listeners;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.wordzap.GameScreen;
import com.android.wordzap.WordZapConstants;


/*
 * Class that listens to events triggering start of a new WordZap level
 * 
 */
public class NextLevelListener implements OnClickListener {
	private final GameScreen theGameScreen;
	private int whoWon;

	// Ties the Game Screen to this listener
	public NextLevelListener(GameScreen theGameScreen, int whoWon) {
		this.theGameScreen = (GameScreen) theGameScreen;
		this.whoWon = whoWon;

	}

	@Override
	public void onClick(View v) {
		Log.i("NextLevel",""+this.whoWon);
		switch (this.whoWon) {
		
		case WordZapConstants.HUMAN_LOSS:
			theGameScreen.startNextLevel(WordZapConstants.HUMAN_LOSE_LEVELJUMP);
			break;

		case WordZapConstants.HUMAN_WIN:
			theGameScreen.startNextLevel(WordZapConstants.HUMAN_WIN_LEVELJUMP);
			break;
			
		}

	}

}
