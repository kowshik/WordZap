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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/* 
 * Activity class for the start screen of WordZap
 * Contains a simple banner with the text WordZap, which when clicked takes the user to activity : GameScreen
 * The user can also set the difficulty level using "+" and "-" buttons
 * 
 */

public class StartScreen extends Activity implements OnClickListener {

	/** Called when the activity is first created. */
	private TextView txtPlus;
	private TextView txtMinus;
	private TextView txtLevel;
	private TextView txtTitle;
	private TextView txtHelp;
	private TextView txtCredits;

	private String helpText;
	private String creditsText;

	private static final int HELP_DIALOG = 1;
	private static final int CREDITS_DIALOG = 2;
	private static final int GAMEOVER_DIALOG = 3;
	private static final int START_GAME = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(WordZapConstants.DEFAULT_ORIENTATION);
		setContentView(R.layout.start_screen);

		// Retrieving on-screen text views
		this.txtTitle = (TextView) findViewById(R.id.txtViewTitle);
		this.txtPlus = (TextView) findViewById(R.id.txtViewPlus);
		this.txtMinus = (TextView) findViewById(R.id.txtViewMinus);
		this.txtLevel = (TextView) findViewById(R.id.txtViewLevel);
		this.txtHelp = (TextView) findViewById(R.id.txtViewHelp);
		this.txtCredits = (TextView) findViewById(R.id.txtViewCredits);

		// Adding listeners
		txtTitle.setOnClickListener(this);
		txtPlus.setOnClickListener(this);
		txtMinus.setOnClickListener(this);
		txtHelp.setOnClickListener(this);
		txtCredits.setOnClickListener(this);

		// Retrieving help text
		this.helpText = this.getResources().getString(R.string.help_text);
		this.creditsText = this.getResources().getString(R.string.credits);

	}

	public void onClick(View v) {
		TextView clickedTxtView = (TextView) v;
		if (clickedTxtView.equals(this.txtTitle)) {
			int chosenLevel = Integer.parseInt(this.txtLevel.getText()
					.toString());
			this.startGame(chosenLevel);
		}
		// User has selected the previous level by clicking on the TextView with
		// the text : "+"
		if (clickedTxtView.equals(this.txtPlus)) {
			int currLevel = Integer
					.parseInt(this.txtLevel.getText().toString());
			int nextLevel;
			if (currLevel < LevelGenerator.MAX_LEVEL) {
				nextLevel = currLevel + 1;
			} else {
				nextLevel = LevelGenerator.MIN_LEVEL;
			}
			txtLevel.setText("" + nextLevel);
		}
		// User has selected the previous level by clicking on the TextView with
		// the text : "-"
		if (clickedTxtView.equals(this.txtMinus)) {
			int currLevel = Integer
					.parseInt(this.txtLevel.getText().toString());
			int nextLevel;
			if (currLevel > LevelGenerator.MIN_LEVEL) {
				nextLevel = currLevel - 1;
			} else {
				nextLevel = LevelGenerator.MAX_LEVEL;
			}

			txtLevel.setText("" + nextLevel);
		}
		// User wants help on how to use WordZap
		if (clickedTxtView.equals(this.txtHelp)) {
			this.showDialog(StartScreen.HELP_DIALOG);
		}
		// User wants see credits
		if (clickedTxtView.equals(this.txtCredits)) {
			this.showDialog(StartScreen.CREDITS_DIALOG);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = null;
		switch (id) {
		case StartScreen.HELP_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(this.helpText)
					.setCancelable(false)
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = builder.create();
			break;
		case StartScreen.CREDITS_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(this.creditsText)
					.setCancelable(false)
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = builder.create();
			break;
		case StartScreen.GAMEOVER_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Congratulations ! You have completed all the levels !")
					.setCancelable(false)
					.setPositiveButton("Okay",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 * 
	 * Gets the next level to be started, and starts it using startGame(...)
	 * method
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == StartScreen.START_GAME
				&& resultCode == Activity.RESULT_OK) {
			int nextLevel = data.getIntExtra(
					WordZapConstants.NEXT_LEVEL_PARAM_KEYNAME,
					WordZapConstants.START_LEVEL);
			 if(nextLevel < LevelGenerator.MIN_LEVEL) {
				 this.startGame(LevelGenerator.MIN_LEVEL);
			 }
			 else if (nextLevel <= LevelGenerator.MAX_LEVEL) {
				this.startGame(nextLevel);
			}else{
				this.showDialog(StartScreen.GAMEOVER_DIALOG);
			}
		}
	}

	/*
	 * Starts the GameScreen activity for any particualar level
	 */

	private void startGame(int level) {
		Intent i = new Intent(this, GameScreen.class);

		/*
		 * Pass this parameter to GameScreen activity for loading the initial
		 * level
		 */
		i.putExtra(WordZapConstants.DIFFICULTY_PARAM_KEYNAME, level);
		startActivityForResult(i, StartScreen.START_GAME);
	}
}