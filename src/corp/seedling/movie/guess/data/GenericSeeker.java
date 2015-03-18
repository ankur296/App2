package corp.seedling.movie.guess.data;

import java.util.ArrayList;

import android.net.Uri;

public abstract class GenericSeeker<E> {

    protected static final String BASE_URL = "http://api.themoviedb.org/3/";
    protected static final String API_KEY = "d07f4f6aab5e70d630f77205edcd335f";

    protected HttpRetriever httpRetriever = new HttpRetriever();

    public abstract ArrayList<ArrayList<String>> find(String query); 

    public abstract String retrieveSearchMethodPath();

    protected String constructSearchUrl(String query, int pageNo) {
        
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
        .authority("api.themoviedb.org")
        .appendPath("3")
        .appendPath("discover")
        .appendPath("movie")
        .appendQueryParameter("api_key", "196527b28198a82e77196ba38b0d32fb")
        .appendQueryParameter("primary_release_year.lte", "2010")
        .appendQueryParameter("sort_by", "vote_average.desc")
        .appendQueryParameter("language", "en")
        .appendQueryParameter("vote_count.gte", "1000")
        .appendQueryParameter("page", ""+pageNo);
        
        return builder.build().toString();
    }


}
