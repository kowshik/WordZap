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

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream.PutField;
import java.lang.reflect.Field;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.wordzap.R.id;
import com.android.wordzap.datamodel.LetterGrid;
import com.android.wordzap.exceptions.DuplicateWordException;
import com.android.wordzap.exceptions.InvalidCpuDescriptionException;
import com.android.wordzap.exceptions.InvalidFreqFileException;
import com.android.wordzap.exceptions.InvalidGridSizeException;
import com.android.wordzap.exceptions.InvalidLevelException;
import com.android.wordzap.exceptions.InvalidStackOperationException;
import com.android.wordzap.exceptions.InvalidWordException;
import com.android.wordzap.exceptions.WordStackOverflowException;
import com.android.wordzap.listeners.EndWordListener;
import com.android.wordzap.listeners.GameScreenDialogListener;
import com.android.wordzap.listeners.GridTextViewListener;
import com.android.wordzap.listeners.LetterButtonListener;

/*
 * Activity class for the game screen where all the action takes place between the human player and the computer
 * This activity is started from activity : StartScreen
 * 
 */

public class GameScreen extends Activity {
	
	// Current level
	private Level currentLevel;

	// MediaPlayer object which will play the above beep sounds
	private MediaPlayer mMediaPlayer;

	// Letter Buttons - 8 in number and are visible on screen
	private Button btnBotFirst;
	private Button btnBotSecond;
	private Button btnBotThird;
	private Button btnBotFourth;

	private Button btnTopFirst;
	private Button btnTopSecond;
	private Button btnTopThird;
	private Button btnTopFourth;

	/*
	 * An array which will be initialised to the above letter buttons in the
	 * constructor. This array is useful for batch operations on the letter
	 * buttons.
	 */
	private Button[] letterButtons;

	// The button which ends a word
	private Button btnEndWord;

	// Common TextView where errors are posted
	private TextView commonTxtView;
	/*
	 * 2D array of TextViews that represent the visual portion of the letter
	 * grid
	 */

	private TextView[][] gridTxtViews;

	/*
	 * 2D array of Buttons that tie each TextView to the letter button that was
	 * clicked
	 */
	private Button[][] gridTxtViewInputSource;

	// Letter grid data model
	private LetterGrid humanPlayerGrid;

	// Handler object for handling computer player's message interrupts
	private Handler mainThreadHandler;

	// Array of text views that indicate position of the computer player
	private TextView[] computerPlayerTxtViews;

	// Generates Word Zap levels
	private LevelGenerator levelGen;
	private WordCache aWordCache;

	// Computer position on the visual grid
	private int computerPosition;

	// List of words already used by the human player
	private List<String> usedWords;

	// Game over flag shared with ComputerPlayer thread
	private volatile boolean gameOver;
	private volatile boolean dialogOpen;

	// Opponent thread
	private Thread opponent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(WordZapConstants.DEFAULT_ORIENTATION);
		setContentView(R.layout.game_screen);

		try {
			/* Initiate level generator to generate word zap levels */
			InputStream alphaFreqStream = this.getResources().openRawResource(
					WordZapConstants.ALPHABETS_FREQ_FILE);
			String alphaFreqStreamDelim = this.getResources().getString(
					WordZapConstants.ALPHABETS_FREQ_FILE_DELIM);
			this.levelGen = new LevelGenerator(alphaFreqStream,
					alphaFreqStreamDelim);

			/*
			 * 
			 * Retrieve all text views that represent the visual grid on screen
			 * for the human player
			 */
			gridTxtViews = new TextView[WordZapConstants.GRID_NUMROWS][WordZapConstants.GRID_NUMCOLS];
			gridTxtViewInputSource = new Button[WordZapConstants.GRID_NUMROWS][WordZapConstants.GRID_NUMCOLS];
			this.retrieveGridTxtViews();

			/*
			 * Retrieve all common text views where game updates are posted
			 */
			this.retrieveCommonTxtViews();

			/*
			 * Init grid text view listeners
			 */

			this.initGridTxtViewListeners();

			/*
			 * Init start level
			 */
			int startLevel = getIntent().getIntExtra(
					WordZapConstants.DIFFICULTY_PARAM_KEYNAME, WordZapConstants.START_LEVEL);
			Log.i(GameScreen.class.toString(), "" + startLevel);
			this.currentLevel = this.levelGen.generateLevel(startLevel);
			char[] levelChars = this.currentLevel.getAlphabets();

			/*
			 * Init word cache
			 */
			InputStream wordListStream = this.getResources().openRawResource(
					WordZapConstants.WORD_LISTS_FILE);
			this.aWordCache = new EnglishWordCache(wordListStream, levelChars);

			/*
			 * Retrieve command buttons that help add letters to the grid and
			 * end the word
			 */
			this.initCommandButtons(levelChars);
			this.initCommandButtonListeners();

			/*
			 * Init grid text view listeners
			 */
			// Initiate letter grid
			this.humanPlayerGrid = new LetterGrid(WordZapConstants.GRID_NUMROWS,
					WordZapConstants.GRID_NUMCOLS, this.aWordCache);

			/*
			 * Initiates computer player indicator text views
			 */

			this.computerPlayerTxtViews = new TextView[WordZapConstants.GRID_NUMROWS];
			this.initComputerIndicator();
			
			/*
			 * Init mainThreadHandler ! This is an important piece of code that
			 * handles messages from computer player thread
			 */

			mainThreadHandler = new Handler() {

				public void handleMessage(Message msg) {
					if (!isGameOver()) {
						Bundle msgBundle = msg.getData();

						// Is this a genword move by the opponent player ?
						if (msgBundle.getBoolean(WordZapConstants.GENWORD_MOVE_KEYNAME) == true) {
							if (!setOpponentPosition(computerPosition+1)) {
								setGameOver(true);
								showDialog(WordZapConstants.HUMAN_WIN_DIALOG);
							}
						}

						// Is this a zap move by the opponent player ?
						if (msgBundle.getBoolean(WordZapConstants.ZAP_MOVE_KEYNAME) == true) {
							if (zapFromVisualGrid()) {
								setOpponentPosition(computerPosition-1);
							}
						}
					}
				}

			};

			/* Initiate computer position on the visual grid */
			this.computerPosition = -1;

			/*
			 * Initiate list of used words
			 */

			this.usedWords = new Vector<String>();

			/*
			 * Initiate computer player which runs as a separate background
			 * thread
			 */
			this.opponent = new Thread(new ComputerPlayer(this, aWordCache,
					this.currentLevel, mainThreadHandler));
			this.setDialogOpen(true);
			showDialog(WordZapConstants.SHOW_LEVEL_DIALOG);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvalidGridSizeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFreqFileException e) {
			e.printStackTrace();
		} catch (InvalidLevelException e) {
			e.printStackTrace();
		} catch (InvalidCpuDescriptionException e) {
			e.printStackTrace();
		}

	}

	// Starts computer player thread
	public void startOpponent() {
		this.opponent.start();
	}

	/*
	 * Sets opponent player's position
	 */
	private boolean setOpponentPosition(int newPosition) {
		for (TextView txtView : this.computerPlayerTxtViews) {
			txtView.setText("");
		}
		if (newPosition < this.computerPosition && this.computerPosition >= 1) {
			this.computerPosition--;
			this.computerPlayerTxtViews[this.computerPosition].setText("-");
		} else if (newPosition > this.computerPosition) {
			this.computerPosition++;
			this.computerPlayerTxtViews[this.computerPosition].setText("-");
		}
		if (this.computerPosition >= this.computerPlayerTxtViews.length - 1) {
			return false;
		}
		return true;
	}

	private void retrieveCommonTxtViews() {
		this.commonTxtView = (TextView) findViewById(R.id.txtViewCommon);
	}

	/*
	 * Initiates indicator text views that show the position of the computer
	 * player at run time
	 */
	private void initComputerIndicator() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Class<id> idClass = R.id.class;
		for (int rowIndex = 0; rowIndex < WordZapConstants.GRID_NUMROWS; rowIndex++) {

			Field txtViewField = idClass.getField("txtViewComp" + rowIndex);
			TextView aTxtView = (TextView) findViewById(txtViewField
					.getInt(null));
			this.computerPlayerTxtViews[rowIndex] = aTxtView;
			aTxtView.setText("");

		}

	}

	// Initializing grid text view listeners
	private void initGridTxtViewListeners() {
		for (int rowIndex = 0; rowIndex < gridTxtViews.length; rowIndex++) {
			for (int colIndex = 0; colIndex < gridTxtViews[rowIndex].length; colIndex++) {
				gridTxtViews[rowIndex][colIndex]
						.setOnClickListener(new GridTextViewListener(this));
			}
		}
	}

	// Initializing letter buttons
	private void initCommandButtonListeners() {
		this.btnTopFirst.setOnClickListener(new LetterButtonListener(this));
		this.btnTopSecond.setOnClickListener(new LetterButtonListener(this));
		this.btnTopThird.setOnClickListener(new LetterButtonListener(this));
		this.btnTopFourth.setOnClickListener(new LetterButtonListener(this));

		this.btnBotFirst.setOnClickListener(new LetterButtonListener(this));
		this.btnBotSecond.setOnClickListener(new LetterButtonListener(this));
		this.btnBotThird.setOnClickListener(new LetterButtonListener(this));
		this.btnBotFourth.setOnClickListener(new LetterButtonListener(this));

		this.btnEndWord.setOnClickListener(new EndWordListener(this));

	}

	// Retrieving letter buttons from XML resource
	private void initCommandButtons(char[] levelLetters) {
		this.btnTopFirst = (Button) findViewById(R.id.buttonTopFirst);
		this.btnTopSecond = (Button) findViewById(R.id.buttonTopSecond);
		this.btnTopThird = (Button) findViewById(R.id.buttonTopThird);
		this.btnTopFourth = (Button) findViewById(R.id.buttonTopFourth);

		this.btnBotFirst = (Button) findViewById(R.id.buttonBotFirst);
		this.btnBotSecond = (Button) findViewById(R.id.buttonBotSecond);
		this.btnBotThird = (Button) findViewById(R.id.buttonBotThird);
		this.btnBotFourth = (Button) findViewById(R.id.buttonBotFourth);
		this.letterButtons = new Button[] { btnBotFirst, btnBotSecond,
				btnBotThird, btnBotFourth, btnTopFirst, btnTopSecond,
				btnTopThird, btnTopFourth };
		this.btnEndWord = (Button) findViewById(R.id.btnEndWord);

		// End word should be disabled by default
		this.btnEndWord.setEnabled(false);

		// Populate level letters
		int index = 0;
		for (Button btn : this.letterButtons) {
			btn.setText("" + levelLetters[index]);
			index++;
		}
	}

	/*
	 * Retrieve TextView objects from XML
	 * 
	 * Uses reflection API, for cleaner code
	 */

	private void retrieveGridTxtViews() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Class<id> idClass = R.id.class;
		for (int rowIndex = 0; rowIndex < WordZapConstants.GRID_NUMROWS; rowIndex++) {
			for (int colIndex = 0; colIndex < WordZapConstants.GRID_NUMCOLS; colIndex++) {
				Field txtViewField = idClass.getField("txtView" + rowIndex
						+ colIndex);
				TextView aTxtView = (TextView) findViewById(txtViewField
						.getInt(null));
				this.gridTxtViews[rowIndex][colIndex] = aTxtView;
				aTxtView.setText("");
			}
		}

	}

	/*
	 * Updates the visual grid and the data model with a new letter
	 * 
	 * Throws WordStackOverflowException : if the stack at the top of the letter
	 * grid data model overflows
	 * 
	 * Throws InvalidStackOperationException : if locked words are manipulated,
	 * without proper unlock operations
	 */
	public synchronized void pushToVisualGrid(Button srcButton)
			throws WordStackOverflowException, InvalidStackOperationException {

		// Fetch letter from command button
		char letter = srcButton.getText().charAt(0);

		// Push to data model
		Map<String, String> map = this.humanPlayerGrid.putLetter(letter);

		int row = Integer.parseInt(map.get(LetterGrid.ROW_KEY));
		int col = Integer.parseInt(map.get(LetterGrid.COL_KEY));
		char pushedLetter = map.get(LetterGrid.LETTER_KEY).charAt(0);

		// Update the visual grid
		this.gridTxtViews[row][col].setText("" + pushedLetter);
		this.gridTxtViewInputSource[row][col] = srcButton;

		if (this.humanPlayerGrid.getWordAtTop().length() > WordZapConstants.MIN_WORD_SIZE) {
			btnEndWord.setEnabled(true);
		}

	}

	/*
	 * Removes a letter for the top of the visual grid and the data model
	 * 
	 * Throws EmptyStackException : if the visual grid is empty
	 * 
	 * Throws InvalidStackOperationException : if you try to pop a letter from a
	 * word thats already completed using 'End Word' button
	 */
	public synchronized void popFromVisualGrid(TextView touchedTxtView)
			throws EmptyStackException, InvalidStackOperationException {
		// Peek at data model
		Map<String, String> map = this.humanPlayerGrid.peekLetter();
		int topRow = Integer.parseInt(map.get(LetterGrid.ROW_KEY));
		int txtViewRow = this.getTxtViewRow(touchedTxtView);
		if (txtViewRow == topRow) {

			// Pop from data model
			map = this.humanPlayerGrid.popLetter();
			int poppedRow = Integer.parseInt(map.get(LetterGrid.ROW_KEY));
			int poppedCol = Integer.parseInt(map.get(LetterGrid.COL_KEY));

			// Update visual grid
			gridTxtViews[poppedRow][poppedCol].setText("");
			gridTxtViewInputSource[poppedRow][poppedCol]
					.setVisibility(View.VISIBLE);
			gridTxtViewInputSource[poppedRow][poppedCol] = null;

			/*
			 * Remove word at top if the only unlocked letter at top of grid has
			 * been popped
			 */
			if (poppedCol == 0) {
				this.humanPlayerGrid.removeWordAtTop();
			}
			if (poppedCol == WordZapConstants.MIN_WORD_SIZE) {
				this.btnEndWord.setEnabled(false);
			}
		}

	}

	/*
	 * Removes a random word from the visual grid as well as the data model
	 */

	public synchronized boolean zapFromVisualGrid() {
		List<String> completedWords = this.humanPlayerGrid
				.getCompletedWordList();

		/*
		 * Can't zap unless the human player has completed atleast one word in
		 * the grid
		 */
		if (completedWords.size() > 0) {
			String wordToBeRemoved = completedWords.get(new Random()
					.nextInt(completedWords.size()));
			int removedWordIndex = this.humanPlayerGrid
					.removeWord(wordToBeRemoved);
			int gridRowIndex = 0, gridColIndex = 0;
			/*
			 * For each row R in the grid starting after the zapped row, copy R
			 * to previous row
			 */
			Log.i("ComputerPlayer", "removedWordIndex : " + removedWordIndex);
			for (gridRowIndex = removedWordIndex + 1; gridRowIndex < this.gridTxtViews.length; gridRowIndex++) {
				/*
				 * For each letter X in this row, copy X to same location in
				 * previous row
				 */
				for (gridColIndex = 0; gridColIndex < gridTxtViews[gridRowIndex].length; gridColIndex++) {
					gridTxtViews[gridRowIndex - 1][gridColIndex]
							.setText(gridTxtViews[gridRowIndex][gridColIndex]
									.getText());
					gridTxtViewInputSource[gridRowIndex - 1][gridColIndex] = gridTxtViewInputSource[gridRowIndex][gridColIndex];
				}
			}

			// Updates the top most row in the grid
			for (gridColIndex = 0; gridColIndex < gridTxtViews[gridRowIndex - 1].length; gridColIndex++) {
				gridTxtViews[gridRowIndex - 1][gridColIndex].setText("");
				gridTxtViewInputSource[gridRowIndex - 1][gridColIndex] = null;
			}
			Log.i("ComputerPlayer", "Zapping in gamescreen");
			this.displayErrorMessage("'" + wordToBeRemoved
					+ "' was zapped by opponent !");
			return true;
		}
		return false;

	}

	/*
	 * Ends the word at the top of the grid, if its a valid word and wasnt used
	 * earlier. Ends the game if the human player has won the game at the end of
	 * this move
	 * 
	 * Throws EmptyStackException : if data model is empty
	 * 
	 * Throws InvalidStackOperationException : if you try to lock a word with
	 * size zero, or an invalid word not validated by the WordCache object
	 * passed to WordCache object.
	 */
	public synchronized void endWord() throws EmptyStackException,
			InvalidStackOperationException, DuplicateWordException,
			InvalidWordException {

		String wordAtTop = this.humanPlayerGrid.getWordAtTop();
		if (this.humanPlayerGrid.containsWord(wordAtTop)) {
			throw new DuplicateWordException("'" + wordAtTop
					+ "' already exists");
		}
		if (this.usedWords.contains(wordAtTop)) {
			throw new InvalidWordException("'" + wordAtTop
					+ "' was already used");
		}
		if (!this.humanPlayerGrid.lockWordAtTop()) {
			throw new InvalidWordException("'" + wordAtTop
					+ "' is not a valid word");
		}

		// Adds the word to list of already used words
		this.usedWords.add(wordAtTop);

		/*
		 * Check if human player has won if yes, then end the game
		 */
		if (this.humanPlayerGrid.isGridFull()) {
			this.setGameOver(true);
			this.setDialogOpen(true);
			showDialog(WordZapConstants.HUMAN_LOSE_DIALOG);

		}
	}

	/*
	 * Beeps the required beep sound.
	 * 
	 * beepType should be one among the following constants :
	 * GameScreen.LETTER_PRESS_BEEP GameScreen.END_WORD_BEEP
	 */
	public void beep(int beepType) {
		try {

			switch (beepType) {
			case WordZapConstants.CANT_PRESS_LETTER_BEEP:
				mMediaPlayer = MediaPlayer.create(this,
						WordZapConstants.CANT_PRESS_LETTER_BEEP);
				break;
			case WordZapConstants.CANT_POP_LETTER_BEEP:
				mMediaPlayer = MediaPlayer.create(this,
						WordZapConstants.CANT_POP_LETTER_BEEP);
				break;
			case WordZapConstants.CANT_END_WORD_BEEP:
				mMediaPlayer = MediaPlayer.create(this,
						WordZapConstants.CANT_END_WORD_BEEP);
				break;
			case WordZapConstants.BAD_WORD_BEEP:
				mMediaPlayer = MediaPlayer.create(this,
						WordZapConstants.BAD_WORD_BEEP);
			}

			mMediaPlayer.setLooping(false);
			Log.e("beep", "started0");
			mMediaPlayer.start();

		} catch (Exception e) {
			Log.e("beep", "error: " + e.getMessage(), e);
		}
	}

	/*
	 * Cleanup code
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	/*
	 * Displays error message on a common text view
	 */
	public synchronized void displayErrorMessage(String msg) {
		this.commonTxtView.setText(msg);

	}

	/*
	 * Makes all letter buttons visible again
	 */
	public synchronized void reviveLetterButtons() {
		for (Button letterButton : this.letterButtons) {
			letterButton.setVisibility(Button.VISIBLE);
		}
	}

	// Returns the row for any Text View on the visual grid
	public int getTxtViewRow(TextView aTxtView) {
		for (int rowIndex = 0; rowIndex < gridTxtViews.length; rowIndex++) {
			for (int colIndex = 0; colIndex < gridTxtViews[0].length; colIndex++) {
				if (aTxtView == this.gridTxtViews[rowIndex][colIndex]) {
					return rowIndex;
				}
			}
		}

		return -1;
	}

	// Returns the row for any Text View on the visual grid
	public int getTxtViewCol(TextView aTxtView, int row) {
		if (row > 0 && row < gridTxtViews.length) {
			for (int colIndex = 0; colIndex < gridTxtViews[row].length; colIndex++) {
				if (aTxtView == this.gridTxtViews[row][colIndex]) {
					return row;
				}
			}
		}

		return -1;
	}

	// Returns the row for any Text View on the visual grid
	public int getTxtViewCol(TextView aTxtView) {

		int row = this.getTxtViewRow(aTxtView);

		if (row > 0 && row < gridTxtViews.length) {
			for (int colIndex = 0; colIndex < gridTxtViews[row].length; colIndex++) {
				if (aTxtView == this.gridTxtViews[row][colIndex]) {
					return colIndex;
				}
			}
		}

		return -1;
	}

	/*
	 * Clears any message previously displayed on the common text view
	 */
	public synchronized void clearErrorMessage() {
		this.commonTxtView.setText("");
	}

	// Tells ComputerPlayer if game is over
	public boolean isGameOver() {
		return gameOver;

	}

	// Used to communicae end of game to ComputerPlayer
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Log.d(this.getClass().getName(), "back button pressed");
		}
		return super.onKeyDown(keyCode, event);
	}

	// Creates various onscreen dialogs
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = null;
		switch (id) {
		case WordZapConstants.HUMAN_LOSE_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("You won ! Get ready for the next level.")
					.setCancelable(false).setPositiveButton(
							"Okay",
							new GameScreenDialogListener(this,
									WordZapConstants.HUMAN_LOSE_DIALOG));
			dialog = builder.create();
			break;
		case WordZapConstants.HUMAN_WIN_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"You lost ! Get ready to play the same level again.")
					.setCancelable(false).setPositiveButton(
							"Okay",
							new GameScreenDialogListener(this,
									WordZapConstants.HUMAN_WIN_DIALOG));
			dialog = builder.create();
			break;

		case WordZapConstants.SHOW_LEVEL_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Level " + this.currentLevel.getLevelNumber() + "     ")
					.setCancelable(false).setPositiveButton(
							"Start",
							new GameScreenDialogListener(this,
									WordZapConstants.SHOW_LEVEL_DIALOG));
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	// Tells if the user has opened any dialog in the visual game screen and is
	// still viewing it
	public boolean isDialogOpen() {
		return this.dialogOpen;
	}

	// Used to communicate any opened dialog to this class
	public void setDialogOpen(boolean value) {
		this.dialogOpen = value;
	}

	/*
	 * Kills this activity and returns an Intent object to calling activity
	 * (Activity com.android.wordzap.StartScreen) explaining the next level to
	 * be started
	 */
	public void startNextLevel(int levelJump) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra(WordZapConstants.NEXT_LEVEL_PARAM_KEYNAME, currentLevel
				.getLevelNumber()
				+ levelJump);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}

}