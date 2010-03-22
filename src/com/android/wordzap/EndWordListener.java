/**
 * @author Kowshik Prakasam
 * 
 * Code that listens to clicks on End Word button in GameScreen
 * 
 */
package com.android.wordzap;

import java.util.EmptyStackException;

import android.view.View;
import android.view.View.OnClickListener;

import com.android.wordzap.exceptions.DuplicateWordException;
import com.android.wordzap.exceptions.InvalidStackOperationException;
import com.android.wordzap.exceptions.InvalidWordException;

public class EndWordListener implements OnClickListener {
	private final GameScreen theGameScreen;

	// Ties the Game Screen to this listener
	public EndWordListener(GameScreen theGameScreen) {
		this.theGameScreen = (GameScreen) theGameScreen;
	}

	public void onClick(View v) {
		try {
			//Clear any existing messages
			theGameScreen.clearErrorMessage();
			
			// Attempts to end the word at top of stack
			theGameScreen.endWord();
			

			// Beep the end word sound
			theGameScreen.beep(GameScreen.CANT_END_WORD_BEEP);

			// Make visible all letter buttons
			// They can be used for the next word by the user
			theGameScreen.reviveLetterButtons();
			
			v.setEnabled(false);
		} catch (EmptyStackException e) {
			 theGameScreen.beep(GameScreen.CANT_END_WORD_BEEP);
		} catch (InvalidStackOperationException e) {
			 theGameScreen.beep(GameScreen.CANT_END_WORD_BEEP);
		} catch (DuplicateWordException e) {
			theGameScreen.displayErrorMessage(e.getMessage());
			theGameScreen.beep(GameScreen.BAD_WORD_BEEP);
		} catch (InvalidWordException e) {
			theGameScreen.displayErrorMessage(e.getMessage());
			theGameScreen.beep(GameScreen.BAD_WORD_BEEP);
		}

	}

}
