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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.android.wordzap.listeners.DispCompGridListener;
import com.android.wordzap.listeners.EndWordListener;
import com.android.wordzap.listeners.GameScreenDialogListener;
import com.android.wordzap.listeners.GridTextViewListener;
import com.android.wordzap.listeners.LetterButtonListener;
import com.android.wordzap.listeners.NextLevelListener;

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

	// Letter grid data model for human player
	private LetterGrid humanPlayerGrid;

	// Letter grid data model for computer player
	private LetterGrid compPlayerGrid;

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

	// Last word formed successfully by the human player
	private volatile String lastWord;

	// Game over flag shared with ComputerPlayer thread
	private volatile boolean gameOver;

	// Opponent thread
	private Thread opponent;

	// Timer thread
	private Thread timer;

	// Handler object for handling computer player's message interrupts
	private Handler timerThreadHandler;

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
					WordZapConstants.DIFFICULTY_PARAM_KEYNAME,
					WordZapConstants.START_LEVEL);
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

			// Init last word formed by human player
			this.lastWord = "";

			// Initiate human player letter grid
			this.humanPlayerGrid = new LetterGrid(
					WordZapConstants.GRID_NUMROWS,
					WordZapConstants.GRID_NUMCOLS, this.aWordCache);

			// Initiate computer player letter grid
			this.compPlayerGrid = new LetterGrid(WordZapConstants.GRID_NUMROWS,
					WordZapConstants.GRID_NUMCOLS, this.aWordCache);

			/*
			 * Initiates computer player indicator text views
			 */

			this.computerPlayerTxtViews = new TextView[WordZapConstants.GRID_NUMROWS];
			this.initComputerIndicator();

			/*
			 * Init timerThreadHandler ! This is an important piece of code that
			 * handles messages from timer thread
			 */

			timerThreadHandler = new Handler() {
				public void handleMessage(Message msg) {
					if (!isGameOver()) {
						Bundle msgBundle = msg.getData();
						int gameStatus = msgBundle
								.getInt(WordZapConstants.GAME_STATUS);

						switch (gameStatus) {
						case WordZapConstants.HUMAN_LOSS:
							setGameOver(true);
							showDialog(WordZapConstants.HUMAN_LOSE_DIALOG);
							break;
						case WordZapConstants.HUMAN_WIN:
							setGameOver(true);
							showDialog(WordZapConstants.HUMAN_WIN_DIALOG);
							break;
						case WordZapConstants.DRAW:
							setGameOver(true);
							showDialog(WordZapConstants.DRAW_DIALOG);
							break;
						case WordZapConstants.NONE:
							int timeValue = msgBundle
									.getInt(WordZapConstants.TIMER_VALUE_KEYNAME);
							displayMessage("" + timeValue + " seconds left");
							break;
						default:
							break;
						}

					}
				}
			};

			/*
			 * Init mainThreadHandler ! This is an important piece of code that
			 * handles messages from computer player thread
			 */

			mainThreadHandler = new Handler() {

				public void handleMessage(Message msg) {
					if (!isGameOver()) {
						Bundle msgBundle = msg.getData();

						// Is this a genword move by the opponent player ?
						if (msgBundle
								.getBoolean(WordZapConstants.GENWORD_MOVE_KEYNAME) == true) {
							String generatedWord = msgBundle
									.getString(WordZapConstants.GENERATED_WORD_KEYNAME);
							try {
								pushToCompGrid(generatedWord);
							} catch (WordStackOverflowException e) {
								e.printStackTrace();
							} catch (InvalidStackOperationException e) {
								e.printStackTrace();
							}
							Log.i("ComputerPlayer", "cpu grid : "
									+ compPlayerGrid.getCompletedWordList());
							if (!setOpponentPosition(computerPosition + 1)) {
								
								setGameOver(true);

								// Killing opponent
								opponent.interrupt();

								// Waking up timer thread
								timer.interrupt();
								showDialog(WordZapConstants.HUMAN_LOSE_DIALOG);
							}
						}

						// Is this a zap move by the opponent player ?
						if (msgBundle
								.getBoolean(WordZapConstants.ZAP_MOVE_KEYNAME) == true) {
							zapFromVisualGrid();
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

			/*
			 * Initiate timer thread
			 */

			this.timer = new Thread(new Timer(this, timerThreadHandler));

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

	/*
	 * Pushes a given word into the computer player's letter grid
	 */
	protected void pushToCompGrid(String generatedWord)
			throws WordStackOverflowException, InvalidStackOperationException {
		for (int letterIndex = 0; letterIndex < generatedWord.length(); letterIndex++) {
			compPlayerGrid.putLetter(generatedWord.charAt(letterIndex));
		}
		compPlayerGrid.lockWordAtTop();

	}

	// Starts computer player thread
	public void startOpponent() {
		this.opponent.start();
		this.timer.start();
	}

	/*
	 * Sets opponent player's position
	 */
	private boolean setOpponentPosition(int newPosition) {

		if ((Math.abs(this.computerPosition - newPosition) > 1)
				|| (this.computerPosition >= this.computerPlayerTxtViews.length - 1)) {
			return false;
		}

		clearOpponentPosition();
		if (newPosition < this.computerPosition && this.computerPosition >= 1) {
			this.computerPosition--;
			this.computerPlayerTxtViews[this.computerPosition].setText("-");
		} else if (newPosition > this.computerPosition) {
			this.computerPosition++;
			this.computerPlayerTxtViews[this.computerPosition].setText("-");
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

	/*
	 * Switches listeners of visual grid text views so that after the level is
	 * over, a click event on any of the text views will populate the human's
	 * letter grid with words formed by the computer player or vice versa
	 */
	private void switchGridTxtViewListeners() {
		OnClickListener aListener = new DispCompGridListener(this);
		for (int rowIndex = 0; rowIndex < gridTxtViews.length; rowIndex++) {
			for (int colIndex = 0; colIndex < gridTxtViews[rowIndex].length; colIndex++) {
				gridTxtViews[rowIndex][colIndex].setOnClickListener(aListener);
			}
		}
	}

	/*
	 * Populates the visual grid with words formed by the computer/human player.
	 * Parameter 'target' should be
	 */

	public void populateVisualGrid(int target) {

		// Remove all letters in the grid
		this.clearVisualGrid();

		LetterGrid targetGrid = null;

		// Find which grid has to be populated visually
		if (target == WordZapConstants.HUMAN_PLAYER_GRID) {
			targetGrid = this.humanPlayerGrid;
		} else if (target == WordZapConstants.COMP_PLAYER_GRID) {
			targetGrid = this.compPlayerGrid;
		}

		List<String> wordList = targetGrid.getWordList();

		for (int rowIndex = 0; rowIndex < wordList.size(); rowIndex++) {
			String word = wordList.get(rowIndex);
			for (int colIndex = 0; colIndex < word.length(); colIndex++) {
				this.gridTxtViews[rowIndex][colIndex].setText(""
						+ word.charAt(colIndex));
			}
		}

	}

	private void clearVisualGrid() {
		for (int rowIndex = 0; rowIndex < gridTxtViews.length; rowIndex++) {
			for (int colIndex = 0; colIndex < gridTxtViews[0].length; colIndex++) {
				this.gridTxtViews[rowIndex][colIndex].setText("");
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

	// Purge letter button listeners
	private void purgeCommandButtonListeners() {
		this.btnTopFirst.setOnClickListener(null);
		this.btnTopSecond.setOnClickListener(null);
		this.btnTopThird.setOnClickListener(null);
		this.btnTopFourth.setOnClickListener(null);

		this.btnBotFirst.setOnClickListener(null);
		this.btnBotSecond.setOnClickListener(null);
		this.btnBotThird.setOnClickListener(null);
		this.btnBotFourth.setOnClickListener(null);

		this.btnEndWord.setOnClickListener(null);

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
	 * Retrieves all completed words from the human player's grid
	 */

	public synchronized List<String> getCompletedWords() {
		return this.humanPlayerGrid.getCompletedWordList();
	}

	/*
	 * Retrieves size of computer player's grid
	 */

	public synchronized int getOpponentGridSize() {
		return this.compPlayerGrid.getCompletedWordList().size();
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
			this.displayMessage("'" + wordToBeRemoved
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

		// Is this word currently existent in the human player's grid ?
		if (this.humanPlayerGrid.containsWord(wordAtTop)) {
			throw new DuplicateWordException("'" + wordAtTop
					+ "' already exists");
		}

		// Has this word been used already ?
		if (this.usedWords.contains(wordAtTop)) {
			throw new InvalidWordException("'" + wordAtTop
					+ "' was already used");
		}

		// The computer player has already formed this word on its grid.
		// So it has to be removed(zapped) from human and computer grids now

		if (this.compPlayerGrid.getCompletedWordList().contains(wordAtTop)) {

			for (int index = 0; index < wordAtTop.length(); index++) {
				Map<String, String> map = this.humanPlayerGrid.peekLetter();
				int topWordRow = Integer.parseInt(map.get(LetterGrid.ROW_KEY));
				int topWordCol = Integer.parseInt(map.get(LetterGrid.COL_KEY));
				this.popFromVisualGrid(this.gridTxtViews[topWordRow][topWordCol]);
			}

			this.compPlayerGrid.removeWord(wordAtTop);
			this.setOpponentPosition(this.computerPosition - 1);
			Log.i("HumanPlayer", "Zapping in gamescreen");
			this.displayMessage("'" + wordAtTop
					+ "' was zapped by human player !");
		}

		if (!this.humanPlayerGrid.lockWordAtTop()) {
			throw new InvalidWordException("'" + wordAtTop
					+ "' is not a valid word");
		}

		// Adds the word to list of already used words
		this.usedWords.add(wordAtTop);

		// Setting this word as the last word successfully formed by the human
		this.setLastWord(wordAtTop);

		/*
		 * Check if human player has won if yes, then end the game
		 */
		if (this.humanPlayerGrid.isGridFull()) {
			this.setGameOver(true);

			// Waking up timer thread
			this.timer.interrupt();

			// Killing opponent
			this.opponent.interrupt();

			showDialog(WordZapConstants.HUMAN_WIN_DIALOG);
		}

		// Waking up timer thread
		this.timer.interrupt();

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
	public synchronized void displayMessage(String msg) {
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
	public synchronized void clearMessage() {
		this.commonTxtView.setText("");
	}

	// Tells ComputerPlayer if game is over
	public synchronized boolean isGameOver() {
		return gameOver;

	}

	// Used to communicate end of game to ComputerPlayer
	public synchronized void setGameOver(boolean gameOver) {
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
		case WordZapConstants.HUMAN_WIN_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage("You won ! Get ready for the next level.")
					.setCancelable(false)
					.setPositiveButton(
							"Okay",
							new GameScreenDialogListener(this,
									WordZapConstants.HUMAN_WIN_DIALOG));
			dialog = builder.create();
			break;
		case WordZapConstants.HUMAN_LOSE_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"You lost ! Get ready to play the same level again.")
					.setCancelable(false)
					.setPositiveButton(
							"Okay",
							new GameScreenDialogListener(this,
									WordZapConstants.HUMAN_LOSE_DIALOG));
			dialog = builder.create();
			break;
		case WordZapConstants.DRAW_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Game Drawn. Get ready to play the same level again.")
					.setCancelable(false)
					.setPositiveButton(
							"Okay",
							new GameScreenDialogListener(this,
									WordZapConstants.DRAW_DIALOG));
			dialog = builder.create();
			break;

		case WordZapConstants.SHOW_LEVEL_DIALOG:
			builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Level " + this.currentLevel.getLevelNumber() + "     ")
					.setCancelable(false)
					.setPositiveButton(
							"Start",
							new GameScreenDialogListener(this,
									WordZapConstants.SHOW_LEVEL_DIALOG));
			dialog = builder.create();
			break;
		}
		return dialog;
	}

	/*
	 * Kills this activity and returns an Intent object to calling activity
	 * (Activity com.android.wordzap.StartScreen) explaining the next level to
	 * be started
	 */
	public void startNextLevel(int levelJump) {
		int nextLevel = currentLevel.getLevelNumber() + levelJump;

		Intent returnIntent = new Intent();
		returnIntent.putExtra(WordZapConstants.NEXT_LEVEL_PARAM_KEYNAME,
				nextLevel);
		setResult(Activity.RESULT_OK, returnIntent);

		finish();
	}

	/*
	 * Modifies UI to suit the end of a level.
	 * 
	 * Sets up listeners to switch between human/computer grids when text views
	 * are clicked on the visual grid.
	 */

	public void endLevel(int whoWon) {
		this.clearMessage();

		this.btnEndWord.setText("START");
		this.btnEndWord.setEnabled(true);
		this.purgeCommandButtonListeners();
		this.btnEndWord.setOnClickListener(new NextLevelListener(this, whoWon));

		this.reviveLetterButtons();
		this.switchGridTxtViewListeners();
		this.clearOpponentPosition();

	}

	// Clears marker that represents the computer player's position
	private void clearOpponentPosition() {
		for (TextView txtView : this.computerPlayerTxtViews) {
			txtView.setText("");
		}
	}

	// Getter method for lastWord attribute
	public synchronized String getLastWord() {
		return lastWord;
	}

	// Setter method for lastWord attribute
	public synchronized void setLastWord(String lastWord) {
		this.lastWord = lastWord;
	}

}