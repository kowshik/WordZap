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

import android.os.Handler;
import android.os.Message;

import com.android.wordzap.datamodel.LetterGrid;

public class ComputerPlayer implements Runnable {

	private final Handler mainThreadHandler;
	private final LetterGrid humanPlayerGrid;
	private final WordCache aWordCache;
	private final Level currentLevel;
	
	public ComputerPlayer(LetterGrid humanPlayerGrid, WordCache aWordCache,
			Level currentLevel, Handler mainThreadHandler) throws NullPointerException {

		if (humanPlayerGrid == null) {
			throw new NullPointerException("Human Player Grid is null.");
		}
		this.humanPlayerGrid = humanPlayerGrid;

		if (mainThreadHandler == null) {
			throw new NullPointerException("Main Thread Handler is null.");
		}
		this.mainThreadHandler = mainThreadHandler;
		
		if(aWordCache == null){
			throw new NullPointerException("WordCache is null.");
		}
		this.aWordCache=aWordCache;
		
		if(currentLevel == null){
			throw new NullPointerException("Level is null.");
		}	
		this.currentLevel=currentLevel;
		
		
	}

	public void run() {
		while (true) {
			try {

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Message msg = mainThreadHandler.obtainMessage();
			mainThreadHandler.sendMessage(msg);
		}

	}

}
