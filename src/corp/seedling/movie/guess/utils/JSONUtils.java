package corp.seedling.movie.guess.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.hardware.Camera.ShutterCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class JSONUtils {

	public static ArrayList<ArrayList<String>> parseMovieResponse(String jsonStr) {
		try{
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONArray results = jsonObject.getJSONArray("results");
			ArrayList<ArrayList<String>> completeMovieList = new ArrayList<ArrayList<String>>();
			ArrayList<String> nonjumbledMoviesList = new ArrayList<String>();
			ArrayList<String> jumbledMoviesList = new ArrayList<String>();

			for(int i = 0; i < results.length(); i++){
				
				JSONObject result = results.getJSONObject(i);
				String movieName = result.getString("title");
				String[] movieWords = null;
				String jumbledMovieName = "";
				
				if ( ( movieName.length() < 25 ) && ( movieName.matches("[a-zA-Z0-9.? ]*")  )  ){
					
					nonjumbledMoviesList.add(movieName);
					
					movieWords = movieName.split("\\s+");
					
					for(int j = 0 ; j < movieWords.length ; j++){
						
						jumbledMovieName += shuffle(movieWords[j]);
						
						if( j < (movieWords.length - 1) )
						jumbledMovieName += " ";
						
					}
					jumbledMoviesList.add(jumbledMovieName );
				}
			}

			long seed = System.nanoTime();
			Collections.shuffle(nonjumbledMoviesList, new Random(seed));
			Collections.shuffle(jumbledMoviesList, new Random(seed));
			
			completeMovieList.add(0, nonjumbledMoviesList);
			completeMovieList.add(1, jumbledMoviesList);
			
			return completeMovieList;

		}
		catch(JSONException e){
			System.out.println("ERROR: Response field changed !!");
			e.printStackTrace();
		}
		return null;
	}

	private static String shuffle(String input){
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
