package corp.seedling.movie.guess.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import corp.seedling.movie.guess.R;
import corp.seedling.movie.guess.app.GameApp;
import corp.seedling.movie.guess.bridges.ServerResponseListener;
import corp.seedling.movie.guess.services.MovieSearchTask;
import corp.seedling.movie.guess.utils.PauseHandler;
import corp.seedling.movie.guess.utils.Sound;
import corp.seedling.movie.guess.utils.TranslateAnim;
import corp.seedling.movie.guess.utils.Utils;

//TODO:space for showing level, space for ad
//TMDB key : 196527b28198a82e77196ba38b0d32fb
//Alt key : 7b5e30851a9285340e78c201c4e4ab99
//Another alt //API Key: d07f4f6aab5e70d630f77205edcd335f
//sample query: http://api.themoviedb.org/3/discover/movie?api_key=196527b28198a82e77196ba38b0d32fb&primary_release_year.lte=2010&sort_by=vote_average.desc&language=en&vote_count.gte=10&page=1
//check tmdb wrapper
//also make it clickable,, add highlight color on touch,, add sound,, add input type
//1st level: small movie names.. only very popular movies.. very slow speed..space replicated as such..starting capital char
//2nd level: 1st char nt capital..big movie names..high speed..


public class GameScreen extends Activity implements ServerResponseListener{

	private LinearLayout layout;
	private RelativeLayout layoutOuter;
	private Button buttonHint, buttonAskFriend, buttonPause, buttonDown;
	private ArrayList<String> movieList, starcastList, charList;
	private ArrayList<ArrayList<String>> completeMovieList;
	private int layoutHeight = 0;

	int totalRowHeight, rowHeight, rowWidth, colHeight, colWidth, margin;
	Context mContext ;
	LinearLayout.LayoutParams rowParams, cellParams; 
	static int totalRowsAdded = 0;
	static int currRows = 0; //TODO: Check if totalRowHeight can serve this or vice versa
	static int maxRowsAllowed = 0;

	private  Typeface mFontStyle;
	private static int maxBoxesForThisLevel = 3;

	private static int blockSize = 90;
	public static final int ACTIONBAR_SIZE_SP = 30;
	public static final int BOTTOM_LAYOUT_SIZE_SP = 30;
	public static final int GENERIC_MARGIN_SP = 1;

	private int colorBox;
	private int colorText;
	private int colorBoxSelected; 

	public static final int ANIM_DUR = 5000;
	private static boolean GAME_OVER = false;

	private static final int SOUND_SWIPE = 1;
	private static final int SOUND_ANS_CORRECT = 2;
	private static final int SOUND_GAME_OVER  = 3;
	private static final int SOUND_START_FALL  = 4;

	private static int level = 1;
	private static int score = 0;
	public static int coins = 0;

	private TextView tvScoreValue;
	private TextView tvScoreText;
	private TextView tvLevel;
	private TextView tvCoins;

	private int screenHeight = 0; 
	private int screenWidth  = 0;

	boolean latestRowAnswered = false;
	private ProgressDialog progressDialog;

	private TranslateAnim animationFalling;
	private boolean animPaused = false;



	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		level = 0;
		score = 0;
		coins = 0;

		initUIcomponents();

		doViewMeasurements(layoutOuter);

		configureAnimation(); 

		setupNextLevel();

		// ***************  button actions

		// 1. Pause
		buttonPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!animPaused){
					animationFalling.pause();
					handler.pause();
					animPaused = true;
				}
				else{
					animationFalling.resume();
					handler.resume();
					animPaused = false;
				}

			}
		});

		// 2. Down
		buttonDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!animPaused){
					animationFalling.restrictDuration(0);
					handler.removeMessages(9);
					handler.sendEmptyMessage(9);
				}
			}
		});

		//3. Hint
		buttonHint.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LinearLayout row = (LinearLayout) layout.getChildAt(currRows - 1);
				ArrayList<String> movieNames = new ArrayList<String>();

				String movieName = row.getTag().toString();
				movieNames.add(0, row.getTag().toString());
				String jumb = "";
				System.out.println("correct name = " +movieName);


				for(int i = 0 ; i < row.getChildCount() ; i++){
					FrameLayout frameLayout = (FrameLayout)row.getChildAt(i);


					TextView textView = (TextView)frameLayout.getChildAt(0);
					jumb += ((TextView)frameLayout.getChildAt(0)).getText().toString();
					/*					char tvLetter = textView.getText().toString().charAt(0);

					//TODO: blow out anim cud be used here
					if (tvLetter == movieName.charAt(0))
						textView.setBackgroundColor(getResources().getColor(R.color.blue_lighter) ) ;//colorBox);	
					 */
				}
				movieNames.add(1, jumb) ;
				System.out.println("jumble name = " +jumb);
				
				startActivity(new Intent(mContext, HintScreen.class)
				.putExtra("movie_names", movieNames)
				.putExtra( "starcast_names", starcastList.get(row.getId()) )
				.putExtra("char_names", charList.get(row.getId()))
//				.putExtra("coins", coins)
				);


			}//onClick END

		});
	} 


	private void initUIcomponents(){
		setContentView(R.layout.main_layout); 

		layoutOuter = (RelativeLayout)findViewById(R.id.layout_outer);
		layout = (LinearLayout)findViewById(R.id.layout);

		tvScoreText = (TextView)findViewById(R.id.tv_score_text);
		tvScoreValue = (TextView)findViewById(R.id.tv_score_points);
		tvLevel = (TextView)findViewById(R.id.tv_level);
		tvCoins = (TextView)findViewById(R.id.tv_coins);

		buttonAskFriend = (Button)findViewById(R.id.button_ask_friend);
		buttonHint = (Button)findViewById(R.id.button_hint);
		buttonDown = (Button)findViewById(R.id.button_down);
		buttonPause = (Button)findViewById(R.id.button_pause);

		colorBox = getResources().getColor(R.color.blue_lighter);
		colorText = getResources().getColor(R.color.white);
		colorBoxSelected = getResources().getColor(R.color.solid_yellow);

		Sound.initSound();

		mFontStyle = Typeface.createFromAsset(mContext.getResources().getAssets(), "CooperHewitt-Semibold.otf");


		tvScoreText.setTypeface(mFontStyle);
		tvScoreValue.setTypeface(mFontStyle);
		tvLevel.setTypeface(mFontStyle);
		tvCoins.setTypeface(mFontStyle);

		buttonAskFriend.setTypeface(mFontStyle);
		buttonHint.setTypeface(mFontStyle);

		tvScoreValue.setText("0");
		tvScoreText.setText("Score:");
		tvCoins.setText("0");


	}
	/////////////////////////////////////////////
	//TODO: check how heavy is the initial layout, then decide whether to post this as runnable
	private void addRow() {

		layout.post(new Runnable() {

			@Override
			public void run() {

				System.out.println("start adding row");

				String curMovie = movieList.get(totalRowsAdded);

				int totalCellsForThisRow = curMovie.length();//TODO: This will be always level value.. no need to calc here

				LinearLayout row  = new LinearLayout(mContext); 
				row.setLayoutParams(rowParams);
				row.setGravity(Gravity.CENTER);
				row.setAnimation(configureAnimation());
				row.setTag( completeMovieList.get(0).get(totalRowsAdded));
				System.out.println("Ankur jumbled = " + curMovie + " ,correct =" + completeMovieList.get(0).get(totalRowsAdded) );
				row.setId(totalRowsAdded);


				//				animationFalling.reset();
				animationFalling.start();

				int boxColor = Utils.getRandomColor();

				for(int j = 0 ; j < totalCellsForThisRow; j++){
					//create cells
					FrameLayout cell = new FrameLayout(mContext);
					cell.setLayoutParams(cellParams);
					//					cell.setBackgroundColor(colorBox);

					//create textviews
					TextView textView = new TextView(mContext); 
					textView.setId(totalRowsAdded);
					//										textView.setTypeface(mFontStyle);
					textView.setTypeface(null, Typeface.BOLD);
					textView.setText( ( ""+curMovie.charAt(j) ).toUpperCase()); // Remove this when deciding casing as a level or hint.. TODO:
					//										textView.setTextAppearance(mContext, android.R.style.TextAppearance_Large);
					textView.setTextSize((blockSize / 2));//
					textView.setTextColor(colorText);  
					textView.setBackgroundResource(R.drawable.textview_bg);
					textView.setBackgroundColor(boxColor);
					//					( (GradientDrawable) textView.getBackground() ).setColor(boxColor ) ;//colorBox);
					textView.setGravity(Gravity.CENTER);
					textView.setOnTouchListener(new ChoiceTouchListener());
					textView.setOnDragListener(new ChoiceDragListener());


					//add textviews to cells
					cell.addView(textView); 

					//add cells to rows
					row.addView(cell);
				}

				//add row
				layout.addView(row , 0);

				layout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {


						if (currRows != 0)
							Sound.playSound(SOUND_START_FALL);

						totalRowHeight += rowHeight;
						totalRowsAdded++;
						currRows++;
						System.out.println("ROW ADDED !! totalRowHeight = " + totalRowHeight + " , currentRows = " +totalRowsAdded);
						layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						handler.sendMessageDelayed(Message.obtain(handler, 9), ANIM_DUR );

						buttonHint.setEnabled(true);

					}
				});



			}
		}); 
	}

	private final class ChoiceTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {

			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

				//setup drag
				ClipData data = ClipData.newPlainText("AA" , ((TextView)view).getText()); 
				//				( (GradientDrawable) view.getBackground() ).setColor(colorBoxSelected);
				view.setBackgroundColor(colorBoxSelected);
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

				//start dragging the item touched
				view.startDrag(data, shadowBuilder, view, 0);

				colorBox = Utils.getRandomColor();
				return true;
			} 
			else {
				return false;
			}
		}
	}

	private class ChoiceDragListener implements OnDragListener {

		@Override
		public boolean onDrag(View v, DragEvent event) {


			//handle the dragged view being dropped over a target view
			TextView srcView = (TextView) ( (View) event.getLocalState() );
			//view dragged item is being dropped on
			TextView destView = (TextView) v;

			switch (event.getAction()) {

			case DragEvent.ACTION_DROP:



				//restrict movement in the same row
				if (srcView.getId() == destView.getId()){
					//					( (GradientDrawable) srcView.getBackground() ).setColor(colorBox);
					srcView.setBackgroundColor(colorBox);
					//					System.out.println("start text = " + srcView.getText() + " , dest text = " + destView.getText());

					String tempSrcText = event.getClipData().getItemAt(0).getText().toString();//srcView.getText().toString();

					srcView.setText(destView.getText());
					destView.setText(tempSrcText);

					validateAnswer(  ( (View) ( (View) (srcView.getParent()) ).getParent() ).getId());
				}else{
					return false;
				}


				break;

			case DragEvent.ACTION_DRAG_STARTED:
				//no action necessary
				break;

			case DragEvent.ACTION_DRAG_ENTERED:

				if (srcView.getId() == destView.getId()){
					Sound.playSound(SOUND_SWIPE);
					//					( (GradientDrawable) destView.getBackground() ).setColor(colorBoxSelected);
					destView.setBackgroundColor(colorBoxSelected);
				}
				break;

			case DragEvent.ACTION_DRAG_EXITED:        
				if (srcView.getId() == destView.getId())
					//					( (GradientDrawable) destView.getBackground() ).setColor(colorBox);
					destView.setBackgroundColor(colorBox);
				break;

			case DragEvent.ACTION_DRAG_ENDED:
				if (srcView.getId() == destView.getId())
					//					( (GradientDrawable) destView.getBackground() ).setColor(colorBox);
					destView.setBackgroundColor(colorBox);

				break;

			default:
				break;
			}
			return true;
		}


	}

	private void validateAnswer(int cellId) {
		//		System.out.println(" cell id " + cellId);

		LinearLayout layout = (LinearLayout)findViewById(cellId) ;
		int childCount = layout.getChildCount();
		String ans = ""; 

		for (int i = 0 ; i < childCount ; i++){
			FrameLayout frameLayout = (FrameLayout) layout.getChildAt(i);

			TextView textView = (TextView)frameLayout.getChildAt(0); 
			ans += textView.getText();
		} 

		if (layout.getTag().toString().equalsIgnoreCase( ans) ){

			( (LinearLayout) (layout.getParent()) ).removeView(layout);
			( (LinearLayout) (layout.getParent()) ).invalidate();

			totalRowHeight -= rowHeight;
			currRows--;

			//if waiting for next row, then disable "Hint" button.. Enable it back in addRow
			if (currRows == 0)
				buttonHint.setEnabled(false);

			System.out.println("Correct answer : totalRowHeight = " +totalRowHeight );
			Sound.playSound(SOUND_ANS_CORRECT);


			updateScores() ;

			//Level Cleared ?
			if ( (currRows == 0) && (totalRowsAdded == maxRowsAllowed) ){
				handler.removeMessages(9);
				setupNextLevel();
			}

		}else{
			//Wrong Answer
		}
	}


	private void setupNextLevel(){

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Start level "+ (++level) + " ?");

		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				MovieSearchTask task = new MovieSearchTask(GameScreen.this);
				task.setLevel( level + 2);
				task.execute();

				progressDialog = ProgressDialog.show(mContext,
						"Please wait...", "Movie Challenge Getting Ready...", true, true);

				progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
			}
		});

		alertDialog.show();
	}


	@Override
	public void onReceiveResult(ArrayList<ArrayList<String>> list) {

		if(list == null)
			Toast.makeText(this, "Nwk Error", Toast.LENGTH_LONG).show();
		else
			GameApp.getAppInstance().setMovieSearchResult(list);

		if(progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}

		maxRowsAllowed = 0;
		totalRowsAdded = 0;

		updateDimensions();
		fetchMovieList();
		addRow();

		tvLevel.setText("Level " + level);


	}

	// TODO: use map for below
	private void updateScores(){

		score += level * 100; 
		tvScoreValue.setText(Integer.toString(score ));

		coins += 10 ; 
		tvCoins.setText(Integer.toString(coins) );
	}

	private void doViewMeasurements(RelativeLayout layout) {
		layout.post(new Runnable() { 

			public void run() {  

				Rect rect = new Rect();   
				Window win = getWindow();  // Get the Window
				win.getDecorView().getWindowVisibleDisplayFrame(rect); 
				// Get the height of Status Bar 
				int statusBarHeight = rect.top; 
				// Get the height occupied by the decoration contents 
				int contentViewTop = win.findViewById(Window.ID_ANDROID_CONTENT).getTop(); 
				// Calculate titleBarHeight by deducting statusBarHeight from contentViewTop  
				int titleBarHeight = contentViewTop - statusBarHeight; 
				System.out.println("ankur: titleHeight = " + titleBarHeight + " statusHeight = " + statusBarHeight + " contentViewTop = " + contentViewTop); 

				// By now we got the height of titleBar & statusBar
				// Now lets get the screen size
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);   
				screenHeight = metrics.heightPixels;
				screenWidth = metrics.widthPixels;

				// Now calculate the height that our layout can be set
				// If you know that your application doesn't have statusBar added, then don't add here also. Same applies to application bar also 
				layoutHeight = screenHeight - (titleBarHeight + statusBarHeight + Utils.getSizeInPixels(ACTIONBAR_SIZE_SP) 
						+ Utils.getSizeInPixels(BOTTOM_LAYOUT_SIZE_SP));
				System.out.println("ankur: Layout Height = " + layoutHeight);   		

				//				updateDimensions();
			}
		});
	}

	private void updateDimensions(){

		maxBoxesForThisLevel = level + 2;

		blockSize = Utils.getSizeInDp((screenWidth -  (maxBoxesForThisLevel * 2) * Utils.getSizeInPixels(GENERIC_MARGIN_SP) ) / maxBoxesForThisLevel );
		//		rowWidth = (getSizeInPixels(blockSize) * maxBoxesForThisLevel) + ( (maxBoxesForThisLevel * 2) * getSizeInPixels(GENERIC_MARGIN_SP) );


		rowWidth = screenWidth ;
		rowHeight =  ( Utils.getSizeInPixels(blockSize) + ( 2 * Utils.getSizeInPixels(GENERIC_MARGIN_SP)) ); 
		System.out.println("ankur:  Actual Screen Height = " + screenHeight + " Width = " + screenWidth);   
		System.out.println("ankur:  Actual rowHeight = " + rowHeight + " rowwidth = " + rowWidth); 
		System.out.println("ankur block size = " +blockSize);

		colWidth = Utils.getSizeInPixels(blockSize);
		colHeight = Utils.getSizeInPixels(blockSize); 

		margin = Utils.getSizeInPixels(GENERIC_MARGIN_SP);

		rowParams = new LinearLayout.LayoutParams( rowWidth, rowHeight); 
		cellParams = new LinearLayout.LayoutParams( colWidth, colHeight); 
		cellParams.setMargins( margin, margin, margin, margin);

	}

	private void fetchMovieList(){

		//TODO: move this part out into a separate func which can be used on clearing every level
		System.out.println("copy list");
		completeMovieList = GameApp.getAppInstance().getMovieNames();
		movieList = GameApp.getAppInstance().getMovieNames().get(1);
		starcastList = GameApp.getAppInstance().getMovieNames().get(2);
		charList = GameApp.getAppInstance().getMovieNames().get(3);
		maxRowsAllowed = movieList.size();
		System.out.println("Total no of movies available = " +movieList.size());
	}




	private TranslateAnimation configureAnimation() {
		animationFalling = new TranslateAnim(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		animationFalling.setDuration(ANIM_DUR);
		animationFalling.setFillAfter(true);
		//		animationFalling.start();

		return animationFalling;
	}



	/**
	 * Handler for this activity
	 */
	public ConcreteTestHandler handler = new ConcreteTestHandler();

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.setActivity(null);
		Sound.unloadSound();

		maxRowsAllowed = 0;
		totalRowsAdded = 0;
		currRows = 0;
		//		tvList.clear();
		//		selectedBoxId = -1;
	}


	@Override
	protected void onResume() {
		super.onResume();
		handler.setActivity(this);
		if (animPaused){
			animationFalling.resume();
			handler.resume();
			animPaused = false;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (!animPaused){
			animationFalling.pause();
			handler.pause();
			animPaused = true;
		}
	}


	/**
	 * Message Handler class that supports buffering up of messages when the
	 * activity is paused i.e. in the background.
	 */
	class ConcreteTestHandler extends PauseHandler {

		/**
		 * Activity instance
		 */
		protected Activity activity;

		/**
		 * Set the activity associated with the handler
		 * 
		 * @param activity
		 *            the activity to set
		 */
		final void setActivity(Activity activity) {
			this.activity = activity;
		}

		@Override
		final protected boolean storeMessage(Message message) {
			// All messages are stored by default
			return true;
		};

		@Override
		final protected void processMessage(Message msg) {

			final Activity activity = this.activity;
			if (activity != null) {
				if (msg.what == 9) {

					foo();
				}

			}
		}
	}

	void foo(){
		if (layoutHeight < (totalRowHeight + rowHeight) /* && ( currRows < movieList.size()) */) {
			Sound.playSound(SOUND_GAME_OVER);
			System.out.println("MAX boxes added !!");
			Toast.makeText(GameScreen.this, "MAX boxes added !!", Toast.LENGTH_LONG).show();
			startActivity(new Intent(GameScreen.this, GameOverScreen.class));
			finish();
			GAME_OVER = true;

		}else{

			System.out.println("Add new row now");

			if (totalRowsAdded < maxRowsAllowed)
				addRow();
		}

	}


@Override
protected void onStart() {
	// TODO Auto-generated method stub
	super.onStart();
	tvCoins.setText(Integer.toString(coins) );
}

	private class CancelTaskOnCancelListener implements DialogInterface.OnCancelListener{

		private AsyncTask<?,?,?> task;

		private CancelTaskOnCancelListener(AsyncTask<?,?,?> task){
			this.task = task;
		}

		@Override 
		public void onCancel(DialogInterface dialogInterface) {
			if(task != null){
				task.cancel(true);
			}
			startActivity(new Intent(GameScreen.this, GameOverScreen.class)); //TODO:
			finish();
		}
	}

}
