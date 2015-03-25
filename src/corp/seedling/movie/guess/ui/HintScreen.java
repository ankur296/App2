package corp.seedling.movie.guess.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import corp.seedling.movie.guess.R;
import corp.seedling.movie.guess.utils.Utils;



public class HintScreen extends Activity{

	private LinearLayout row;
	private LinearLayout.LayoutParams  cellParams;
	private int colHeight, colWidth, margin;
	private int blockSize = 90;
	private String correctMovieName, jumbledMovieName;
	private Button btnshowLetter, btnshowAllCorrect, btnshowAll, btnshowActorSingle, btnshowActorAll, 
	btnshowCharSingle, btnshowCharAll, btnshowAllActorChar, btnCoinsFree, btnCoinsBuy;
	private TextView tvCoinStatus;
	private ArrayList<String> movieNames;
	private ArrayList<String> starcastList = new ArrayList<String>();
	private ArrayList<String> charList = new ArrayList<String>();
	private String starCastString, charString;
	private static int actorsshowed = 0;
	private static int ACTORS_MAX ;
	private static int charsshowed = 0;
	private static int CHARS_MAX ;
	private Bundle bundleIntent;
	private int coins;
	private static final int COINS_SHOW_LETTER = 10; 
	private static final int COINS_SHOW_LETTER_CORRECT = 20; 
	private static final int COINS_SHOW_LETTER_ALL = 60; 
	private static final int COINS_ACTOR_SINGLE = 10; 
	private static final int COINS_ACTOR_ALL = 30; 
	private static final int COINS_CHAR_SINGLE = 10; 
	private static final int COINS_CHAR_ALL = 30; 
	private static final int COINS_ACTOR_CHAR_ALL = 40; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.screen_hint);
		bundleIntent = getIntent().getExtras();

		prepUI();

		//Handle btnon Clicks

		//1. show Letter
		btnshowLetter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (coins >= COINS_SHOW_LETTER){
					manageHintsShowSingle();
					updateCoinStatus(COINS_SHOW_LETTER);
				}
				else{
					Toast.makeText(HintScreen.this, "Insufficient Coins !!", Toast.LENGTH_LONG).show();
					btnCoinsFree.setBackgroundColor(R.color.color_11_red_dark);
				}
			}
		});

		//2. show All Correct
		btnshowAllCorrect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (coins >= COINS_SHOW_LETTER_CORRECT){
					manageHintsShowAllCorrect();
					updateCoinStatus(COINS_SHOW_LETTER_CORRECT);
				}
				else{
					Toast.makeText(HintScreen.this, "Insufficient Coins !!", Toast.LENGTH_LONG).show();
					btnCoinsFree.setBackgroundColor(R.color.color_11_red_dark);
				}
				
			}
		});

		//3. show All 
		btnshowAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (coins >= COINS_SHOW_LETTER_ALL){
					manageHintsShowAll();
					updateCoinStatus(COINS_SHOW_LETTER_ALL);
				}
				else{
					Toast.makeText(HintScreen.this, "Insufficient Coins !!", Toast.LENGTH_LONG).show();
					btnCoinsFree.setBackgroundColor(R.color.color_11_red_dark);
				}
				
			}

		});

		//4. show Actor Single
		btnshowActorSingle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (coins >= COINS_ACTOR_SINGLE){
					manageHintsShowActorSingle();
					updateCoinStatus(COINS_ACTOR_SINGLE);
				}
				else{
					Toast.makeText(HintScreen.this, "Insufficient Coins !!", Toast.LENGTH_LONG).show();
					btnCoinsFree.setBackgroundColor(R.color.color_11_red_dark);
				}
			}

		});

		//5. show Actor All
		btnshowActorAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (coins >= COINS_ACTOR_ALL){
					manageHintsShowActorAll();
					updateCoinStatus(COINS_ACTOR_ALL);
				}
				else{
					Toast.makeText(HintScreen.this, "Insufficient Coins !!", Toast.LENGTH_LONG).show();
					btnCoinsFree.setBackgroundColor(R.color.color_11_red_dark);
				}
			}

		});

		//6. show Char Single
		btnshowCharSingle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (coins >= COINS_CHAR_SINGLE){
					manageHintsShowCharSingle();
					updateCoinStatus(COINS_CHAR_SINGLE);
				}
				else{
					Toast.makeText(HintScreen.this, "Insufficient Coins !!", Toast.LENGTH_LONG).show();
					btnCoinsFree.setBackgroundColor(R.color.color_11_red_dark);
				}
			}

		});

		//7. show Char All
		btnshowCharAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (coins >= COINS_CHAR_ALL){
					manageHintsShowCharAll();
					updateCoinStatus(COINS_CHAR_ALL);
				}
				else{
					Toast.makeText(HintScreen.this, "Insufficient Coins !!", Toast.LENGTH_LONG).show();
					btnCoinsFree.setBackgroundColor(R.color.color_11_red_dark);
				}
			}

		});

		//8. show actor-Char All
		btnshowAllActorChar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (coins >= COINS_ACTOR_CHAR_ALL){
					manageHintsShowAllActorChar();
					updateCoinStatus(COINS_ACTOR_CHAR_ALL);
				}
				else{
					Toast.makeText(HintScreen.this, "Insufficient Coins !!", Toast.LENGTH_LONG).show();
					btnCoinsFree.setBackgroundColor(R.color.color_11_red_dark);
				}
			}


		});
	}

	protected void updateCoinStatus(int i) {
		coins -= i;
		GameScreen.coins = coins;
		tvCoinStatus.setText("You have " + coins  + " coins" );
	}

	private void manageHintsShowAllActorChar() {
		final AlertDialog dialogActorSingle = new AlertDialog.Builder(this).create();

		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogActorSingle.dismiss();
			}
		};

		dialogActorSingle.setTitle("Complete Actor->Character Mapping");
		dialogActorSingle.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", clickListener);

		String starNames = "";

		for(int i = 0 ; i < starcastList.size(); i++){
			starNames += starcastList.get(i);
			starNames += "	->	";
			starNames += charList.get(i);
			starNames += "\n";
		}

		dialogActorSingle.setMessage(starNames);
		dialogActorSingle.show();

	}

	private void manageHintsShowCharSingle() {
		final AlertDialog dialogActorSingle = new AlertDialog.Builder(this).create();

		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogActorSingle.dismiss();
			}
		};

		dialogActorSingle.setTitle("One of the Characters in the movie");
		dialogActorSingle.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", clickListener);

		if (charsshowed < CHARS_MAX){
			dialogActorSingle.setMessage(charList.get(charsshowed));
			btnshowCharSingle.setText("Show Another Char From This Movie");

		}
		else{
			dialogActorSingle.setTitle("");
			dialogActorSingle.setMessage("No More Characters To Show !!");
		}

		charsshowed++;
		dialogActorSingle.show();
	}

	private void manageHintsShowCharAll() {
		final AlertDialog dialogActorSingle = new AlertDialog.Builder(this).create();

		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogActorSingle.dismiss();
			}
		};

		dialogActorSingle.setTitle("Complete Characters List");
		dialogActorSingle.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", clickListener);

		String starNames = "";

		for(String starName:charList){
			starNames += starName;
			starNames += "\n";
		}

		dialogActorSingle.setMessage(starNames);
		dialogActorSingle.show();
	}


	private void manageHintsShowActorSingle() {
		final AlertDialog dialogActorSingle = new AlertDialog.Builder(this).create();

		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogActorSingle.dismiss();
			}
		};

		dialogActorSingle.setTitle("One of the Actors in the movie");
		dialogActorSingle.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", clickListener);

		if (actorsshowed < ACTORS_MAX){
			dialogActorSingle.setMessage(starcastList.get(actorsshowed));
			btnshowActorSingle.setText("Show Another Actor From This Movie");

		}
		else{
			dialogActorSingle.setTitle("");
			dialogActorSingle.setMessage("No More Actor Names To Show !!");
		}

		actorsshowed++;
		dialogActorSingle.show();
	}

	private void manageHintsShowActorAll() {
		final AlertDialog dialogActorSingle = new AlertDialog.Builder(this).create();

		DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialogActorSingle.dismiss();
			}
		};

		dialogActorSingle.setTitle("Complete Actors List");
		dialogActorSingle.setButton(DialogInterface.BUTTON_NEUTRAL, "Ok", clickListener);

		String starNames = "";

		for(String starName:starcastList){
			starNames += starName;
			starNames += "\n";
		}

		dialogActorSingle.setMessage(starNames);
		dialogActorSingle.show();
	}


	private void manageHintsShowAll() {
		for (int i = 0 ; i < jumbledMovieName.length() ; i++){

			FrameLayout frameLayout = (FrameLayout)row.getChildAt(i);
			TextView textView = (TextView)frameLayout.getChildAt(0);

			char movieChar = correctMovieName.toUpperCase().charAt(i);

			textView.setText(String.valueOf(movieChar));
			textView.setBackgroundColor(getResources().getColor(R.color.solid_green) ) ;
		}
	}

	private void manageHintsShowAllCorrect() {
		int noOfCorrectlyPlaced = 0;
		for (int i = 0 ; i < jumbledMovieName.length() ; i++){


			FrameLayout frameLayout = (FrameLayout)row.getChildAt(i);
			TextView textView = (TextView)frameLayout.getChildAt(0);

			char boxChar = textView.getText().toString().charAt(0);
			char movieChar = correctMovieName.toUpperCase().charAt(i);


			//Step#1: Start highlighting from beginning to see which letters are already arranged
			if (boxChar  ==  movieChar) {
				textView.setBackgroundColor(getResources().getColor(R.color.solid_green) ) ;	
				noOfCorrectlyPlaced++;
			}
		}
		
		if (noOfCorrectlyPlaced == 0)
			Toast.makeText(this, "None of the letters are correctly placed", Toast.LENGTH_LONG).show();//TODO:string
	}


	private void manageHintsShowSingle() {

		int checkedUntilIndex = jumbledMovieName.length() ;
		for (int i = 0 ; i < jumbledMovieName.length() ; i++){


			FrameLayout frameLayout = (FrameLayout)row.getChildAt(i);
			TextView textView = (TextView)frameLayout.getChildAt(0);

			char boxChar = textView.getText().toString().charAt(0);
			char movieChar = correctMovieName.toUpperCase().charAt(i);


			//Step#1: Start highlighting from beginning to see which letters are already arranged
			if (boxChar  ==  movieChar) {
				textView.setBackgroundColor(getResources().getColor(R.color.solid_green) ) ;	

			}
			else{
				checkedUntilIndex = i;
				break;
			}


		} 

		for (int i = checkedUntilIndex ; i < jumbledMovieName.length() ; i++){


			FrameLayout frameLayoutGettingChecked = (FrameLayout)row.getChildAt(i);
			TextView textViewGettingChecked  = (TextView)frameLayoutGettingChecked .getChildAt(0);
			char charGettingChecked = textViewGettingChecked.getText().toString().charAt(0);
			char charWeAreLookingFor = correctMovieName.toUpperCase().charAt(checkedUntilIndex);

			//Step#2: Highlight the correct letter
			if ( charGettingChecked == charWeAreLookingFor ){

				//swap the char btw this view and the first incorrect view
				FrameLayout frameLayoutFirstIncorrect = (FrameLayout)row.getChildAt(checkedUntilIndex);
				TextView textViewFirstIncorrect  = (TextView)frameLayoutFirstIncorrect .getChildAt(0);
				char tempTextIncorrect = textViewFirstIncorrect.getText().toString().charAt(0);

				textViewFirstIncorrect.setBackgroundColor(getResources().getColor(R.color.solid_green) ) ;	
				textViewFirstIncorrect.setText(String.valueOf(charWeAreLookingFor));

				textViewGettingChecked.setText(String.valueOf(tempTextIncorrect));				
				break;
			}
		} 
	}

	private void prepUI() {
		row = (LinearLayout)findViewById(R.id.layout_hint_moviename);

		btnshowLetter = (Button)findViewById(R.id.btn_show_single);
		btnshowAllCorrect= (Button)findViewById(R.id.btn_show_all_correct);
		btnshowAll = (Button)findViewById(R.id.btn_show_all);
		btnshowActorSingle = (Button)findViewById(R.id.btn_show_actor_single);
		btnshowActorAll = (Button)findViewById(R.id.btn_show_all_actors);
		btnshowCharSingle = (Button)findViewById(R.id.btn_show_char_single);
		btnshowCharAll = (Button)findViewById(R.id.btn_show_all_chars);
		btnshowAllActorChar = (Button)findViewById(R.id.btn_show_all_actors_chars);
		btnCoinsFree = (Button)findViewById(R.id.btn_coins_free);
		btnCoinsBuy = (Button)findViewById(R.id.btn_coins_buy);
		tvCoinStatus = (TextView)findViewById(R.id.tv_coin_status);

		coins = GameScreen.coins;///bundleIntent.getInt("coins") ;
		tvCoinStatus.setText("You have " + coins + " coins");
		starCastString = bundleIntent.getString("starcast_names");

		try {
			JSONObject starJsonObject = new JSONObject(starCastString);
			JSONArray starJsonArray =	starJsonObject.optJSONArray("starcast");

			for (int i = 0 ; i < starJsonArray.length(); i++){
				starcastList.add(starJsonArray.getString(i));
			}

			ACTORS_MAX = starcastList.size();

		} catch (JSONException e) {
			e.printStackTrace();
		}

		charString = bundleIntent.getString("char_names");

		try {
			JSONObject charJsonObject = new JSONObject(charString);
			JSONArray charJsonArray =	charJsonObject.optJSONArray("character");

			for (int i = 0 ; i < charJsonArray.length(); i++){
				charList.add(charJsonArray.getString(i));
			}

			CHARS_MAX = charList.size();

		} catch (JSONException e) {
			e.printStackTrace();
		}


		movieNames = bundleIntent.getStringArrayList("movie_names");
		correctMovieName = movieNames.get(0);
		jumbledMovieName = movieNames.get(1);

		blockSize = 35;
		System.out.println("hint " + movieNames.get(0) + " , " + movieNames.get(1));

		colWidth = Utils.getSizeInPixels(blockSize);
		colHeight = Utils.getSizeInPixels(blockSize); 

		margin = Utils.getSizeInPixels(GameScreen.GENERIC_MARGIN_SP);

		cellParams = new LinearLayout.LayoutParams( colWidth, colHeight); 
		cellParams.setMargins( margin, margin, margin, margin);

		int totalCellsForThisRow = correctMovieName.length();//TODO: This will be always level value.. no need to calc here



		for(int j = 0 ; j < totalCellsForThisRow; j++){
			//create cells
			FrameLayout cell = new FrameLayout(this);
			cell.setLayoutParams(cellParams);

			//create textviews
			TextView textView = new TextView(this); 
			textView.setTypeface(null, Typeface.BOLD);
			textView.setText( ( ""+jumbledMovieName.charAt(j) ).toUpperCase()); 
			textView.setTextSize((blockSize / 2));//
			textView.setTextColor(getResources().getColor(R.color.white));  
			textView.setBackgroundResource(R.drawable.textview_bg);
			textView.setBackgroundColor(getResources().getColor(R.color.pink));
			textView.setGravity(Gravity.CENTER);

			//add textviews to cells
			cell.addView(textView); 

			//add cells to rows
			row.addView(cell);
		}		
	}

}
