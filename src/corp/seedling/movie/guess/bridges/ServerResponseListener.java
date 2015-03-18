package corp.seedling.movie.guess.bridges;

import java.util.ArrayList;

public interface ServerResponseListener {

	void onReceiveResult(ArrayList<ArrayList<String>> list);
}
