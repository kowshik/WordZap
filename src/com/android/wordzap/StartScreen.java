/**
 * 
 * @author Kowshik Prakasam
 * 
 * 
 * Activity class for the start screen of WordZap
 * Contains a simple banner with the text WordZap, which when clicked takes the user to activity : GameScreen
 * The user can also set the difficulty level using "+" and "-" buttons
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
	
	private static final int HELP_DIALOG=1;
	private static final int CREDITS_DIALOG = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(WordZapConstants.DEFAULT_ORIENTATION);
		setContentView(R.layout.start_screen);
		
		//Retrieving on-screen text views
		this.txtTitle = (TextView) findViewById(R.id.txtViewTitle);
		this.txtPlus = (TextView) findViewById(R.id.txtViewPlus);
		this.txtMinus = (TextView) findViewById(R.id.txtViewMinus);
		this.txtLevel = (TextView) findViewById(R.id.txtViewLevel);
		this.txtHelp =  (TextView) findViewById(R.id.txtViewHelp);
		this.txtCredits =  (TextView) findViewById(R.id.txtViewCredits);
		
		//Adding listeners
		txtTitle.setOnClickListener(this);
		txtPlus.setOnClickListener(this);
		txtMinus.setOnClickListener(this);
		txtHelp.setOnClickListener(this);
		txtCredits.setOnClickListener(this);
		
		//Retrieving help text
		this.helpText=this.getResources().getString(R.string.help_text);
		this.creditsText=this.getResources().getString(R.string.credits);
		
	}

	public void onClick(View v) {
		TextView clickedTxtView = (TextView) v;
		if(clickedTxtView.equals(this.txtTitle)){
			Intent i=new Intent(this, GameScreen.class);
			//Pass this parameter to GameScreen activity for loading the initial level
			int chosenLevel=Integer.parseInt(this.txtLevel.getText().toString());
			i.putExtra(GameScreen.DIFFICULTY_PARAM_NAME, chosenLevel);
			startActivity(i);
		}
		//User has selected the previous level by clicking on the TextView with the text : "+" 
		if(clickedTxtView.equals(this.txtPlus)){
			int currLevel=Integer.parseInt(this.txtLevel.getText().toString());
			if(currLevel < LevelGenerator.MAX_LEVEL){
				int nextLevel = currLevel+1;
				txtLevel.setText(""+nextLevel);
			}
		}
		//User has selected the previous level by clicking on the TextView with the text : "-" 
		if(clickedTxtView.equals(this.txtMinus)){
			int currLevel=Integer.parseInt(this.txtLevel.getText().toString());
			if(currLevel > LevelGenerator.MIN_LEVEL){
				int nextLevel = currLevel-1;
				txtLevel.setText(""+nextLevel);
			}
		}
		//User wants help on how to use WordZap
		if(clickedTxtView.equals(this.txtHelp)){
			this.showDialog(StartScreen.HELP_DIALOG);
		}
		//User wants see credits
		if(clickedTxtView.equals(this.txtCredits)){
			this.showDialog(StartScreen.CREDITS_DIALOG);
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog=null;
	    AlertDialog.Builder builder=null;
	    switch(id) {
	    case StartScreen.HELP_DIALOG:
	    	builder = new AlertDialog.Builder(this);
	    	builder.setMessage(this.helpText)
	    	       .setCancelable(false)
	    	       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dialog.cancel();
	    	           }
	    	       })
	    	       ;
	    	dialog = builder.create();
	    	break;
	    case StartScreen.CREDITS_DIALOG:
	    	builder = new AlertDialog.Builder(this);
	    	builder.setMessage(this.creditsText)
	    	       .setCancelable(false)
	    	       .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dialog.cancel();
	    	           }
	    	       })
	    	       ;
	    	dialog = builder.create();
	    	break;
	    }
	    return dialog;
	}
}