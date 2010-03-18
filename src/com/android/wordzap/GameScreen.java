/**
 * 
 * @author Kowshik Prakasam
 * 
 * Activity class for the game screen where all the action takes place between the human player and the computer
 * 
 */

package com.android.wordzap;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.wordzap.R.id;
import com.android.wordzap.datamodel.LetterGrid;
import com.android.wordzap.exceptions.InvalidFreqFileException;
import com.android.wordzap.exceptions.InvalidGridSizeException;
import com.android.wordzap.exceptions.InvalidLevelException;
import com.android.wordzap.exceptions.InvalidStackOperationException;
import com.android.wordzap.exceptions.WordStackOverflowException;

public class GameScreen extends Activity {
	// Number of rows and cols in the visual grid
	// This will be constant throughout the game
	private final static int GRID_NUMROWS = 7;
	private final static int GRID_NUMCOLS = 5;

	// Beep sounds during special situations
	public final static int LETTER_PRESS_BEEP = R.raw.letter_press_beep;
	public final static int LETTER_POP_BEEP = R.raw.letter_pop_beep;
	public final static int END_WORD_BEEP = R.raw.end_word_beep;
	
	//English alphabet frequencies file
	private static final int ALPHABETS_FREQ_FILE = R.raw.english_alphabets_frequencies;
	private static final int ALPHABETS_FREQ_FILE_DELIM = R.string.english_alphabets_frequencies_delim;
	
	//Minimum word size allowed on the word zap screen
	private static final int MIN_WORD_SIZE = 2;
	private static final int START_LEVEL = LevelGenerator.MIN_LEVEL;
	

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

	//Generates Word Zap levels
	private LevelGenerator levelGen;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.main);

		try {
			/*Initiate level generator to generate word zap levels*/
			InputStream alphaFreqStream = this.getResources().openRawResource(GameScreen.ALPHABETS_FREQ_FILE);
			String alphaFreqStreamDelim = this.getResources().getString(GameScreen.ALPHABETS_FREQ_FILE_DELIM);
			this.levelGen = new LevelGenerator(alphaFreqStream,alphaFreqStreamDelim);
	
			/*
			 * Retrieve all text views that represent the visual grid on screen
			 * for the human player
			 */
			gridTxtViews = new TextView[GRID_NUMROWS][GRID_NUMCOLS];
			gridTxtViewInputSource = new Button[GRID_NUMROWS][GRID_NUMCOLS];
			this.retrieveGridTxtViews();

			/*
			 * Init grid text view listeners
			 */

			this.initGridTxtViewListeners();
			/*
			 * Retrieve command buttons that help add letters to the grid and
			 * end the word
			 */
			this.initCommandButtons(this.levelGen.generateLevel(GameScreen.START_LEVEL));
			this.initCommandButtonListeners();

			/*
			 * Init grid text view listeners
			 */
			// Initiate letter grid
			this.humanPlayerGrid = new LetterGrid(GameScreen.GRID_NUMROWS,
					GameScreen.GRID_NUMCOLS);

			/*
			 * Initiates computer player indicator text views
			 */

			this.computerPlayerTxtViews = new TextView[GameScreen.GRID_NUMROWS];
			this.initComputerIndicator();
			/*
			 * Init mainThreadHandler ! This is an important piece of code that handles
			 * messages from computer player thread
			 */

			
			
			mainThreadHandler = new Handler() {
				public void handleMessage(Message msg) {
					
					for(TextView txtView : computerPlayerTxtViews){
						txtView.setText("");
						
					}

					Random rand = new Random();
					int randomIndicator = -1;
					do{
						randomIndicator = rand.nextInt(GRID_NUMROWS-1);
						
					}while(randomIndicator<0);
					computerPlayerTxtViews[randomIndicator].setText("-");
					
				}

			};

			/*
			 * Initiate computer player which runs as a separate background
			 * thread
			 */
			Thread opponent = new Thread(new ComputerPlayer(humanPlayerGrid,
					mainThreadHandler));
			opponent.start();

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
		}

	}

	/*
	 * Initiates indicator text views that show the position of the computer
	 * player at run time
	 */
	private void initComputerIndicator() throws SecurityException,
			NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Class<id> idClass = R.id.class;
		for (int rowIndex = 0; rowIndex < GameScreen.GRID_NUMROWS; rowIndex++) {

			Field txtViewField = idClass.getField("txtViewComp" + rowIndex);
			TextView aTxtView = (TextView) findViewById(txtViewField
					.getInt(null));
			this.computerPlayerTxtViews[rowIndex] = aTxtView;
			aTxtView.setText("");

		}

	}

	// Initialising grid text view listeners
	private void initGridTxtViewListeners() {
		for (int rowIndex = 0; rowIndex < gridTxtViews.length; rowIndex++) {
			for (int colIndex = 0; colIndex < gridTxtViews[rowIndex].length; colIndex++) {
				gridTxtViews[rowIndex][colIndex]
						.setOnClickListener(new GridTextViewListener(this));
			}
		}
	}

	// Initialising letter buttons
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

		//Populate level letters
		int index=0;
		for(Button btn : this.letterButtons){
			btn.setText(""+levelLetters[index]);
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
		for (int rowIndex = 0; rowIndex < GameScreen.GRID_NUMROWS; rowIndex++) {
			for (int colIndex = 0; colIndex < GameScreen.GRID_NUMCOLS; colIndex++) {
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
	public void pushToVisualGrid(Button srcButton)
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

		if (this.humanPlayerGrid.getWordAtTop().length() > GameScreen.MIN_WORD_SIZE) {
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
	public void popFromVisualGrid(TextView touchedTxtView)
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

			// Remove word at top if the only unlocked letter at top of grid has
			// been popped
			if (poppedCol == 0) {
				this.humanPlayerGrid.removeWordAtTop();
			}
			if (poppedCol == GameScreen.MIN_WORD_SIZE) {
				this.btnEndWord.setEnabled(false);
			}
		}

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
			case GameScreen.LETTER_PRESS_BEEP:
				mMediaPlayer = MediaPlayer.create(this, GameScreen.LETTER_PRESS_BEEP);
				break;
			case GameScreen.LETTER_POP_BEEP:
				mMediaPlayer = MediaPlayer.create(this, GameScreen.LETTER_POP_BEEP);
				break;
			case END_WORD_BEEP:
				mMediaPlayer = MediaPlayer.create(this, GameScreen.END_WORD_BEEP);
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

}