/**
 * 
 * @author Kowshik Prakasam
 * 
 * This class listens to click events on TextViews in the visual grid
 * Each click operation instructs an instance of the GameScreen activity to pop a letter from the grid
 * 
 */

package com.android.wordzap;

import java.util.EmptyStackException;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.android.wordzap.exceptions.InvalidStackOperationException;

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
