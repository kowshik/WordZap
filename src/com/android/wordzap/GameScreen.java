/**
 * 
 * @author Kowshik Prakasam
 * 
 * Activity for the game screen where all the action takes place between the human player and the computer
 * 
 */

package com.android.wordzap;

import java.lang.reflect.Field;
import java.util.EmptyStackException;
import java.util.Map;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.wordzap.datamodel.InvalidGridSizeException;
import com.android.wordzap.datamodel.InvalidStackOperationException;
import com.android.wordzap.datamodel.LetterGrid;
import com.android.wordzap.datamodel.WordStackOverflowException;

public class GameScreen extends Activity {
	// Number of rows and cols in the visual grid
	// This will be constant throughout the game
	private final static int GRID_NUMROWS = 7;
	private final static int GRID_NUMCOLS = 5;

	// Beep sounds during special situations
	public final static int LETTER_PRESS_BEEP = R.raw.letter_press_beep;
	public final static int END_WORD_BEEP = R.raw.end_word_beep;

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

	// 2D array of TextViews that represent the visual portion of the letter
	// grid
	private TextView[][] gridTxtViews;

	// Letter grid data model
	private LetterGrid humanPlayerGrid;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.main);

		try {
			/*
			 * Retrieve all text views that represent the visual grid on screen
			 * for the human player
			 */
			gridTxtViews = new TextView[GRID_NUMROWS][GRID_NUMCOLS];
			this.retrieveGridTxtViews();

			/*
			 * Retrieve command buttons that help add letters to the grid and
			 * end the word
			 */
			this.retrieveLetterButtons();
			this.initLetterBtnListeners();

			// Initiate letter grid
			this.humanPlayerGrid = new LetterGrid(this.GRID_NUMROWS,
					this.GRID_NUMCOLS);

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
		}

	}

	// Initialising letter buttons
	private void initLetterBtnListeners() {
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
	private void retrieveLetterButtons() {
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
	}

	/*
	 * Retrieve TextView objects from XML
	 * 
	 * Uses reflection API, for cleaner code
	 */

	private void retrieveGridTxtViews() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Class idClass = R.id.class;
		String word = "";
		for (int rowIndex = 0; rowIndex < this.GRID_NUMROWS; rowIndex++) {
			for (int colIndex = 0; colIndex < this.GRID_NUMCOLS; colIndex++) {
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
	 * Attempts to update the visual grid, and the data model with a new letter
	 * 
	 * Throws WordStackOverflowException : if the stack at the top of the letter
	 * grid data model overflows
	 * 
	 * Throws InvalidStackOperationException : if locked words are manipulated,
	 * without proper unlock operations
	 */
	public void updateGrid(char letter) throws WordStackOverflowException,
			InvalidStackOperationException {
		// Push to data model
		Map<String, String> map = this.humanPlayerGrid.putLetter(letter);

		int row = Integer.parseInt(map.get(LetterGrid.ROW_KEY));
		int col = Integer.parseInt(map.get(LetterGrid.COL_KEY));
		char pushedLetter = map.get(LetterGrid.LETTER_KEY).charAt(0);

		// Update the visual grid
		gridTxtViews[row][col].setText("" + pushedLetter);

	}

	/*
	 * Ends the word at the top of the grid
	 * 
	 * Throws EmptyStackException : if data model is empty
	 * 
	 * Throws InvalidStackOperationException : if you try to lock a word with
	 * size zero
	 */
	public void endWord() throws EmptyStackException,
			InvalidStackOperationException {
		this.humanPlayerGrid.lockWordAtTop();
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
			case LETTER_PRESS_BEEP:
				mMediaPlayer = MediaPlayer.create(this, this.LETTER_PRESS_BEEP);
				break;
			case END_WORD_BEEP:
				mMediaPlayer = MediaPlayer.create(this, this.END_WORD_BEEP);
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
	 * Sets all letter butons to visible again
	 */
	public void reviveLetterButtons() {
		for (Button letterButton : this.letterButtons) {
			letterButton.setVisibility(Button.VISIBLE);
		}
	}

}