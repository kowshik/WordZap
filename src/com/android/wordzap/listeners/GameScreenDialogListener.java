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

import com.android.wordzap.GameScreen;
import com.android.wordzap.WordZapConstants;

import android.content.DialogInterface;

/*
 * 
 * 
 * Listens to click events in dialog boxes presented to the user on the game screen.
 * 
 */
public class GameScreenDialogListener implements
		DialogInterface.OnClickListener {

	private int whatDialog;
	private final GameScreen gameScreen;

	public GameScreenDialogListener(final GameScreen gameScreen, int whatDialog) {
		this.gameScreen = gameScreen;
		this.whatDialog = whatDialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (this.whatDialog) {
		/*
		 * If user clicks on 'start' button when level number is displayed, then
		 * start the level
		 */
		case WordZapConstants.SHOW_LEVEL_DIALOG:
			dialog.cancel();
			gameScreen.startOpponent();

			break;

		/*
		 * After the user clicks on 'okay' button, restart the same level when
		 * user clicks on 'start' button later
		 */
		case WordZapConstants.HUMAN_LOSE_DIALOG:
			dialog.cancel();
			gameScreen.endLevel(WordZapConstants.HUMAN_LOSS);
			break;

		/*
		 * After the user clicks on 'okay' button, start the next level when
		 * user clicks on 'start' button
		 */
		case WordZapConstants.HUMAN_WIN_DIALOG:
			dialog.cancel();
			gameScreen.endLevel(WordZapConstants.HUMAN_WIN);
			break;

		/*
		 * After the user clicks on 'okay' button, restart the same level when
		 * user clicks on 'start' button
		 */
		case WordZapConstants.DRAW_DIALOG:
			dialog.cancel();
			gameScreen.endLevel(WordZapConstants.DRAW);
			break;

		}

	}

}
