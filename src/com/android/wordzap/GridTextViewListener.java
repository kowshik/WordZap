
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

import java.util.EmptyStackException;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.wordzap.exceptions.InvalidStackOperationException;

/* 
 * This class listens to click events on TextViews in the visual grid
 * Each click operation instructs an instance of the GameScreen activity to pop a letter from the grid
 * 
 */

public class GridTextViewListener implements OnClickListener {
	private GameScreen theGameScreen;

	public GridTextViewListener(GameScreen theGameScreen) {
		this.theGameScreen = theGameScreen;
	}

	public void onClick(View v) {
		TextView clickedLetter = (TextView) v;

		try {

			// Clear any existing messages
			theGameScreen.clearErrorMessage();
			theGameScreen.popFromVisualGrid(clickedLetter);
		} catch (EmptyStackException e) {
			theGameScreen.beep(GameScreen.CANT_POP_LETTER_BEEP);
		} catch (InvalidStackOperationException e) {
			theGameScreen.beep(GameScreen.CANT_POP_LETTER_BEEP);
		}
	}

}
