package corp.seedling.movie.guess.app;

import java.util.ArrayList;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;

public class GameApp extends Application {

	private static GameApp appInstance;
	private ArrayList<ArrayList<String>> movieNames;
	private ProgressDialog progressDialog;
	private static Context context;
	
	public static GameApp getAppInstance(){
		return appInstance;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		GameApp.context = getApplicationContext();
		appInstance = this;
	}
	
	public void setMovieSearchResult(ArrayList<ArrayList<String>> list){
		movieNames =  new ArrayList<ArrayList<String>>(list);
	}
	
	public ArrayList<ArrayList<String>> getMovieNames(){
		return movieNames;
	}
	
}
