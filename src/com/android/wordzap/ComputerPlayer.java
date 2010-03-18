package com.android.wordzap;

import android.os.Handler;
import android.os.Message;

import com.android.wordzap.datamodel.LetterGrid;

public class ComputerPlayer implements Runnable {

	private Handler mainThreadHandler;

	public ComputerPlayer(LetterGrid humanPlayerGrid, Handler mainThreadHandler)
			throws NullPointerException {

		if (humanPlayerGrid == null) {
			throw new NullPointerException("Human Player Grid is null.");
		}

		if (mainThreadHandler == null) {
			throw new NullPointerException("Main Thread Handler is null.");
		}
		this.mainThreadHandler = mainThreadHandler;

	}

	public void run() {
		while(true) {
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
