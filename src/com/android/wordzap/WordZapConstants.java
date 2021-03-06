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

import com.android.wordzap.R.raw;
import com.android.wordzap.R.string;

import android.content.pm.ActivityInfo;

/* 
 * Contains all constants shared across WordZap code base
 */

public interface WordZapConstants {
	/****** UI PARAMETERS ******/
	int DEFAULT_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	/***************************/

	/****** LEVEL INCREMENT/DECREMENT DURING HUMAN WIN/LOSS/DRAW ******/
	int HUMAN_LOSE_LEVELJUMP = -1;
	int HUMAN_WIN_LEVELJUMP = 1;
	int GAME_DRAW_LEVELJUMP = 0;
	/*************************************************************/

	/****** DIALOG BOXES IN THE USER INTERFACE *****/
	int HUMAN_WIN_DIALOG = 0;
	int HUMAN_LOSE_DIALOG = 1;
	int SHOW_LEVEL_DIALOG = 2;
	int DRAW_DIALOG = 3;
	/***********************************************/

	/****** IDENTIFIES HUMAN LOSS/WIN IN A LEVEL *****/
	int HUMAN_LOSS = 0;
	int HUMAN_WIN = 1;
	int DRAW = 2;
	int NONE=-1;
	/***********************************************/

	
	/****** KEY NAMES IN KEY-VALUE PAIRS ******/
	String GENWORD_MOVE_KEYNAME = "genword_move";
	String GENERATED_WORD_KEYNAME = "genword";
	String ZAP_MOVE_KEYNAME = "zap_move";
	String NEXT_LEVEL_PARAM_KEYNAME = "next_level";
	String DIFFICULTY_PARAM_KEYNAME = "difficulty";
	String TIMER_VALUE_KEYNAME = "timer_value";
	String GAME_STATUS = "game_status"; 
	/******************************************/

	/****** OTHER PARAMETERS ******/
	// Default level with which the game starts
	int START_LEVEL = LevelGenerator.MIN_LEVEL;
	/******************************/

	/****** GAME SOUNDS *******/
	int BAD_WORD_BEEP = R.raw.bad_word_beep;
	int CANT_END_WORD_BEEP = R.raw.end_word_beep;
	int CANT_POP_LETTER_BEEP = R.raw.letter_pop_beep;
	// Beep sounds during special situations
	int CANT_PRESS_LETTER_BEEP = R.raw.letter_press_beep;
	/**************************/

	/****** VISUAL LETTER GRID CONSTANTS ******/
	int GRID_NUMCOLS = 5;
	int GRID_NUMROWS = 7;
	// Minimum word size allowed on the word zap screen
	int MIN_WORD_SIZE = 2;
	// Maximum word size allowed on the word zap screen
	// Same as the number of cols allowed in the letter grid
	int MAX_WORD_SIZE = GRID_NUMCOLS;
	/******************************************/

	/****** REFERENCE DICTIONARY (ENGLISH) ******/
	// English alphabet frequencies file
	int ALPHABETS_FREQ_FILE = R.raw.english_alphabets_frequencies;
	// English word lists file
	int WORD_LISTS_FILE = R.raw.word_list;
	int ALPHABETS_FREQ_FILE_DELIM = R.string.english_alphabets_frequencies_delim;
	/********************************************/

	//Number of milliseconds for which the human player can afford to sleep
	int HUMAN_SLEEP_CHECK = 30000;

	//Identifies the human player's visual grid
	int HUMAN_PLAYER_GRID = 0;

	//Identifies the coimputer player's visual grid
	int COMP_PLAYER_GRID = 1;

	



	

}
