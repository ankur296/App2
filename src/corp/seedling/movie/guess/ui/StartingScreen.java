package corp.seedling.movie.guess.ui;

import java.util.ArrayList;
import corp.seedling.movie.guess.R;
import corp.seedling.movie.guess.app.GameApp;
import corp.seedling.movie.guess.bridges.ServerResponseListener;
import corp.seedling.movie.guess.services.MovieSearchTask;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class StartingScreen extends Activity implements ServerResponseListener{

	private Button playButton, instructionsButton, settingsButton;
	private ProgressDialog progressDialog;
	private  Typeface mFontStyle;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starting_screen);  
		
		mFontStyle = Typeface.createFromAsset(getResources().getAssets(), "Blackout-2am.ttf");


		instructionsButton = (Button)findViewById(R.id.button_instructions);
		instructionsButton.setTypeface(mFontStyle);

		settingsButton = (Button)findViewById(R.id.button_settings);
		settingsButton.setTypeface(mFontStyle);

		playButton = (Button)findViewById(R.id.button_play);
		playButton.setTypeface(mFontStyle);

		playButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(StartingScreen.this, GameScreen.class));
			}
		});

		MovieSearchTask task = new MovieSearchTask(this);
		task.execute();

		progressDialog = ProgressDialog.show(this,
				"Please wait...", "Movie Challenge Getting Ready...", true, true);

		progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
	}

	@Override
	public void onReceiveResult(ArrayList<ArrayList<String>> list) {

		if(list == null)
			Toast.makeText(this, "Nwk Error", Toast.LENGTH_LONG).show();
		else
			GameApp.getAppInstance().setMovieSearchResult(list);

		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	private class CancelTaskOnCancelListener implements DialogInterface.OnCancelListener{

		private AsyncTask<?,?,?> task;

		private CancelTaskOnCancelListener(AsyncTask<?,?,?> task){
			this.task = task;
		}

		@Override 
		public void onCancel(DialogInterface dialogInterface) {
			if(task != null){
				task.cancel(true);
			}
			startActivity(new Intent(StartingScreen.this, GameOverScreen.class));
			finish();
		}
	}

}
