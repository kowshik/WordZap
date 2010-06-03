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
		 * start the opponent
		 */
		case WordZapConstants.SHOW_LEVEL_DIALOG:
			this.gameScreen.setDialogOpen(false);
			dialog.cancel();
			gameScreen.startOpponent();
			break;

		// When the user clicks on 'okay' button, restart the same level
		case WordZapConstants.HUMAN_WIN_DIALOG:
			this.gameScreen.setDialogOpen(false);
			dialog.cancel();
			gameScreen.startNextLevel(WordZapConstants.HUMAN_LOSE_LEVELJUMP);
			break;

		// When user clicks on 'okay' button, start the next level
		case WordZapConstants.HUMAN_LOSE_DIALOG:
			this.gameScreen.setDialogOpen(false);
			dialog.cancel();
			gameScreen.startNextLevel(WordZapConstants.HUMAN_WIN_LEVELJUMP);
			break;
		}
	}

}
