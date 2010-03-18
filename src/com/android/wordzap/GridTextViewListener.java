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

	public boolean onTouch(View v, MotionEvent event) {

		TextView touchedLetter = (TextView) v;

		try {
			theGameScreen.popFromVisualGrid(touchedLetter);
		} catch (EmptyStackException e) {
			theGameScreen.beep(GameScreen.LETTER_POP_BEEP);
			e.printStackTrace();
		} catch (InvalidStackOperationException e) {
			theGameScreen.beep(GameScreen.LETTER_POP_BEEP);
			e.printStackTrace();
		}
		return true;
	}

	public void onClick(View v) {
		TextView clickedLetter = (TextView) v;

		try {
			theGameScreen.popFromVisualGrid(clickedLetter);
		} catch (EmptyStackException e) {
			theGameScreen.beep(GameScreen.LETTER_POP_BEEP);
			e.printStackTrace();
		} catch (InvalidStackOperationException e) {
			theGameScreen.beep(GameScreen.LETTER_POP_BEEP);
			e.printStackTrace();
		}
	}

}
