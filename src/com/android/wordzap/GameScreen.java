/**
 * 
 * @author Kowshik Prakasam
 * 
 * Activity for the game screen where all the action takes place between the human player and the computer
 * 
 */

package com.android.wordzap;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GameScreen extends Activity implements OnClickListener {
	private Button btnLeftBotFirst;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.main);

		btnLeftBotFirst = (Button) findViewById(R.id.buttonLeftBotFirst);
		btnLeftBotFirst.setOnClickListener(this);

	}

	public void onClick(View v) {

	}
}