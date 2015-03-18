package corp.seedling.movie.guess.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import corp.seedling.movie.guess.app.GameApp;
import corp.seedling.movie.guess.bridges.ServerResponseListener;
import corp.seedling.movie.guess.data.GenericSeeker;
import corp.seedling.movie.guess.data.MovieContract.MovieEntry;
import corp.seedling.movie.guess.data.MovieDbHelper;
import corp.seedling.movie.guess.data.MovieSeeker;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class MovieSearchTask extends AsyncTask<String, Void, ArrayList<ArrayList<String>>> {

	private GenericSeeker<String> movieSeeker = new MovieSeeker();
	private ServerResponseListener responseListener = null;

	public MovieSearchTask(ServerResponseListener listener){
		this.responseListener = listener;
	}

	@Override
	protected ArrayList<ArrayList<String>> doInBackground(String... strings) {

		ArrayList<ArrayList<String>> completeMovieList = new ArrayList<ArrayList<String>>();
		ArrayList<String> nonjumbledMoviesList = new ArrayList<String>();
		ArrayList<String> jumbledMoviesList = new ArrayList<String>();
		ArrayList<String> votecountList = new ArrayList<String>();


		MovieDbHelper dbHelper = new MovieDbHelper(GameApp.getAppInstance());
		SQLiteDatabase database = dbHelper.getWritableDatabase(); 
		String selectQuery = "SELECT  * FROM " + MovieEntry.TABLE_NAME;
		Cursor c = database.rawQuery(selectQuery, null);

		//		System.out.println("ANKUR "+c.getCount()); 
		if (c.moveToFirst()){ 

			//			for(int i = 0 ; i < 500 ; i++){
			do{
				String movieName;
				String[] movieWords = null;
				String jumbledMovieName = "";
				int votecount;
				//fetch names from db and store in list1
				movieName = c.getString(c.getColumnIndexOrThrow(MovieEntry.COLUMN_NAME_TITLE)) ;
				votecount = c.getInt(c.getColumnIndexOrThrow(MovieEntry.COLUMN_VOTE_COUNT)) ;
				//				if (movieName.length() < 11){
				//					System.out.println("movie name fetched from DB " + movieName + " , vote = " +votecount + " , " + Integer.toString(votecount) );
				nonjumbledMoviesList.add(movieName);

				movieWords = movieName.split("\\s+");

				for(int j = 0 ; j < movieWords.length ; j++){

					jumbledMovieName += shuffle(movieWords[j]);

					if( j < (movieWords.length - 1) )
						jumbledMovieName += " ";

				}
				//jumble the alphabets and store in list2
				jumbledMoviesList.add(jumbledMovieName );
				votecountList.add(Integer.toString(votecount) );
				//				}
				c.moveToNext();
			}while(c.moveToNext());
			//			}

			long seed = System.nanoTime();
			Collections.shuffle(nonjumbledMoviesList, new Random(seed));
			Collections.shuffle(jumbledMoviesList, new Random(seed));
			Collections.shuffle(votecountList, new Random(seed));

			completeMovieList.add(0, nonjumbledMoviesList);
			completeMovieList.add(1, jumbledMoviesList);
			completeMovieList.add(2, votecountList);



		}

		return completeMovieList;
	}

	@Override
	protected void onPostExecute(final ArrayList<ArrayList<String>> movieList) {

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
