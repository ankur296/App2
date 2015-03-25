package corp.seedling.movie.guess.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import corp.seedling.movie.guess.app.GameApp;
import corp.seedling.movie.guess.bridges.ServerResponseListener;
import corp.seedling.movie.guess.data.MovieContract.MovieEntry;
import corp.seedling.movie.guess.data.MovieDbHelper;
import corp.seedling.movie.guess.ui.StartingScreen;

public class MovieSearchTask extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {

	private ServerResponseListener responseListener = null;
	private int gameLevel = 3;
	private static String voteCountCondition = " >= 400 ";

	public MovieSearchTask(ServerResponseListener listener){
		this.responseListener = listener;
	}

	public void setLevel(int level){
		this.gameLevel = level;

		if (gameLevel > StartingScreen.TOTAL_LEVELS){

			gameLevel -= 9 ;
			voteCountCondition = " < 100 ";
		}
	}

	@Override
	protected ArrayList<ArrayList<String>> doInBackground(String... strings) {

		ArrayList<ArrayList<String>> completeMovieList = new ArrayList<ArrayList<String>>();
		ArrayList<String> nonjumbledMoviesList = new ArrayList<String>();
		ArrayList<String> jumbledMoviesList = new ArrayList<String>();
		ArrayList<String> starcastList = new ArrayList<String>();
		ArrayList<String> charList = new ArrayList<String>();

		
		JSONArray starcastJsonArray = new JSONArray();

		MovieDbHelper dbHelper = new MovieDbHelper(GameApp.getAppInstance());
		SQLiteDatabase database = dbHelper.getWritableDatabase(); 
		String selectQuery = "SELECT  * FROM " + MovieEntry.TABLE_NAME + " WHERE " + MovieEntry.COLUMN_NAME_LENGTH + " = " + gameLevel
				+ " AND " + MovieEntry.COLUMN_VOTE_COUNT + voteCountCondition ;
		Cursor c = database.rawQuery(selectQuery, null);

		System.out.println("Ankur cursor: " + DatabaseUtils.dumpCursorToString(c) );
		if (c.moveToFirst()){ 

			do{
				String movieName;
				String[] movieWords = null;
				String jumbledMovieName = "";
				String starCastString, charString;
//				JSONObject json;
				
				//fetch names from db and store in list1
				movieName = c.getString(c.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)) ;

				starCastString = c.getString(c.getColumnIndexOrThrow(MovieEntry.COLUMN_STAR_CAST)) ;
				charString = c.getString(c.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_CHARACTER)) ;
//				try {
//				
//					json = new JSONObject(starCast);
//					starcastJsonArray = json.optJSONArray("starcast");
//					
//					for(int i = 0 ; i < starcastJsonArray.length() ; i++)
//						starcastList.add(starcastJsonArray.optString(i));
//						
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}

				starcastList.add(starCastString);
				charList.add(charString);
				nonjumbledMoviesList.add(movieName);

				movieWords = movieName.split("\\s+");

				for(int j = 0 ; j < movieWords.length ; j++){

					jumbledMovieName += shuffle(movieWords[j]);

					if( j < (movieWords.length - 1) )
						jumbledMovieName += " ";

				}
				//jumble the alphabets and store in list2
				jumbledMoviesList.add(jumbledMovieName );

			}while(c.moveToNext());

			long seed = System.nanoTime();
			Collections.shuffle(nonjumbledMoviesList, new Random(seed));
			Collections.shuffle(jumbledMoviesList, new Random(seed));
			Collections.shuffle(starcastList, new Random(seed));
			Collections.shuffle(charList, new Random(seed));

			completeMovieList.add(0, nonjumbledMoviesList);
			completeMovieList.add(1, jumbledMoviesList);
			completeMovieList.add(2, starcastList);
			completeMovieList.add(3, charList);
		}

		return completeMovieList;
	}

	@Override
	protected void onPostExecute(final ArrayList<ArrayList<String>> movieList) {

		if (responseListener != null)
			responseListener.onReceiveResult(movieList);

		//		for(String name: movieList.get(0))
		//			System.out.println("original Names = " + name);
		//
		//		for(String name: movieList.get(1))
		//			System.out.println("jumbled Names = " + name);
	}

	private String shuffle(String input){
		List<Character> characters = new ArrayList<Character>();
		for(char c:input.toCharArray()){
			characters.add(c);
		}
		StringBuilder output = new StringBuilder(input.length());
		while(characters.size()!=0){
			int randPicker = (int)(Math.random()*characters.size());
			output.append(characters.remove(randPicker));
		}
		return output.toString();
	}
}
