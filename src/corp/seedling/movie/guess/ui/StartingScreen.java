package corp.seedling.movie.guess.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import corp.seedling.movie.guess.R;

public class StartingScreen extends Activity{

	private Button playButton, instructionsButton, settingsButton;
	private  Typeface mFontStyle;
	private LinearLayout linearLayout;
	public static int TOTAL_LEVELS ;
	private static final int MIN_BOX_SIZE_DP = 30;
	private static int GENERIC_MARGIN_DP = 1;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.starting_screen);  
		
		linearLayout = (LinearLayout)findViewById(R.id.layout_start_screen);
		
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
				Intent intent = new Intent(StartingScreen.this, GameScreen.class);
//				intent.putExtra("Total_Levels", TOTAL_LEVELS);
				startActivity(intent);
			}
		});

		//TODO: Ensure that layout has been created before this call occurrs else NPE
		calcTotalLevels(linearLayout);
	}

	private void calcTotalLevels(LinearLayout layout) {
		layout.post(new Runnable() { 

			public void run() {  
				
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);   
				int screenWidth = metrics.widthPixels;
				
				TOTAL_LEVELS = screenWidth /  ( MIN_BOX_SIZE_DP + (2* getSizeInPixels(GENERIC_MARGIN_DP) )  );
			}
		});
	}

	private int getSizeInPixels(int dp){
		return (int) ( (dp * getResources().getDisplayMetrics().density)  + 0.5f) ; 
	}
}
