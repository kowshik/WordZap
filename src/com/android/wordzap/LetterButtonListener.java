/**
 * 
 * @author Kowshik Prakasam
 * 
 * Code that listens to click operations on letter buttons in the visual game screen of the human player
 */

package com.android.wordzap;

import com.android.wordzap.datamodel.InvalidStackOperationException;
import com.android.wordzap.datamodel.LetterGrid;
import com.android.wordzap.datamodel.WordStackOverflowException;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LetterButtonListener implements OnClickListener {

	private final GameScreen theGameScreen;

	// Ties the GameScreen to this listener
	public LetterButtonListener(GameScreen theGameScreen) {
		this.theGameScreen = (GameScreen) theGameScreen;

	}

	public void onClick(View v) {
		Button letterButton = (Button) v;
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
			theGameScreen.beep(GameScreen.LETTER_PRESS_BEEP);
		} catch (InvalidStackOperationException e) {
			// Beep if there is a runtime error
			theGameScreen.beep(GameScreen.LETTER_PRESS_BEEP);
		}
	}
}
