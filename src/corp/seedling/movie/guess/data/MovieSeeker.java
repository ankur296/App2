package corp.seedling.movie.guess.data;

import android.util.Log;
import java.util.ArrayList;

import corp.seedling.movie.guess.ui.GameScreen;
import corp.seedling.movie.guess.utils.JSONUtils;

public class MovieSeeker extends GenericSeeker<String> {

    private static final String MOVIE_SEARCH_PATH = "search/movie";

    @Override
    public ArrayList<ArrayList<String>> find(String query) {
        return retrieveMoviesList(query);
    }


    private ArrayList<ArrayList<String>> retrieveMoviesList(String query){
        
    	
    	
    	String url = constructSearchUrl(query, 1);
        String response = httpRetriever.retrieve(url);
        
        if(response == null){
            Log.d(getClass().getSimpleName(), "Network error");
            return null;
        }
        
        ArrayList<ArrayList<String>> completeMovieList, additionalMovieList;
        completeMovieList = JSONUtils.parseMovieResponse(response);
        
        String url2 = constructSearchUrl(query, 2);
        String response2 = httpRetriever.retrieve(url2);
        if(response2 == null){
            Log.d(getClass().getSimpleName(), "Network error");
            return null;
        }
        additionalMovieList = JSONUtils.parseMovieResponse(response2); 
        
        completeMovieList.get(0).addAll(additionalMovieList.get(0));
        completeMovieList.get(1).addAll(additionalMovieList.get(1));
        
        String url3 = constructSearchUrl(query, 3);
        String response3 = httpRetriever.retrieve(url3);
        if(response3 == null){
            Log.d(getClass().getSimpleName(), "Network error");
            return null;
        }
        
        additionalMovieList = JSONUtils.parseMovieResponse(response3); 
        completeMovieList.get(0).addAll(additionalMovieList.get(0));
        completeMovieList.get(1).addAll(additionalMovieList.get(1));
        
        String url4 = constructSearchUrl(query, 4);
        String response4 = httpRetriever.retrieve(url4);
        if(response4 == null){
            Log.d(getClass().getSimpleName(), "Network error");
            return null;
        }
        
        additionalMovieList = JSONUtils.parseMovieResponse(response4); 
        completeMovieList.get(0).addAll(additionalMovieList.get(0));
        completeMovieList.get(1).addAll(additionalMovieList.get(1));        
        
        return completeMovieList;
    }

    @Override
    public String retrieveSearchMethodPath() {
        return MOVIE_SEARCH_PATH;
    }


}
