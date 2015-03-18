package corp.seedling.movie.guess.ui;
import corp.seedling.movie.guess.R;
import android.app.Activity;
import android.os.Bundle;


public class GameOverScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_game_over);
    }
}
