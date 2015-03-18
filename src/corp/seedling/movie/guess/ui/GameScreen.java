package corp.seedling.movie.guess.ui;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import corp.seedling.movie.guess.R;
import corp.seedling.movie.guess.app.GameApp;
import corp.seedling.movie.guess.utils.PauseHandler;
import corp.seedling.movie.guess.utils.Sound;
import corp.seedling.movie.guess.utils.TranslateAnim;

//TODO:space for showing level, space for ad
//TMDB key : 196527b28198a82e77196ba38b0d32fb
//Alt key : 7b5e30851a9285340e78c201c4e4ab99
//Another alt //API Key: d07f4f6aab5e70d630f77205edcd335f
//sample query: http://api.themoviedb.org/3/discover/movie?api_key=196527b28198a82e77196ba38b0d32fb&primary_release_year.lte=2010&sort_by=vote_average.desc&language=en&vote_count.gte=10&page=1
//check tmdb wrapper
//also make it clickable,, add highlight color on touch,, add sound,, add input type
//1st level: small movie names.. only very popular movies.. very slow speed..space replicated as such..starting capital char
//2nd level: 1st char nt capital..big movie names..high speed..


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class GameScreen extends Activity implements AnimationListener{

	private LinearLayout layout, layoutOuter;
	private ArrayList<String> movieList;
	private ArrayList<ArrayList<String>> completeMovieList;
	private int layoutHeight = 0;

	int totalRowHeight, rowHeight, rowWidth, colHeight, colWidth, margin;
	Context mContext ;
	LinearLayout.LayoutParams rowParams, cellParams; 
	static int totalRowsAdded = 0;
	static int currRows = 0; //TODO: Check if totalRowHeight can serve this or vice versa

	private  Typeface mFontStyle;
	private static int MAX_BLOCKS = 10;
	private static int BLOCK_SIZE_SP = 30; 
	private static int ACTIONBAR_SIZE_SP = 30;
	private static int GENERIC_MARGIN_SP = 1;

	private int colorBox;
	private int colorText;
	private int colorBoxSelected; 

	private int startTime = 10000;
	private static boolean GAME_OVER = false;

	private static final int SOUND_SWIPE = 1;
	private static final int SOUND_ANS_CORRECT = 2;
	private static final int SOUND_GAME_OVER  = 3;
	private static final int SOUND_START_FALL  = 4;

	private static int level = 0;
	private static int score = 0;
	private static int coins = 0;

	private TextView tvScore;
	private TextView tvLevel;
	private TextView tvCoins;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;

		setContentView(R.layout.main_layout); 
		layoutOuter = (LinearLayout)findViewById(R.id.layout_outer);
		layout = (LinearLayout)findViewById(R.id.layout);	
		doViewMeasurements(layoutOuter);
		configureAnimation();

		colorBox = getResources().getColor(R.color.blue_lighter);
		colorText = getResources().getColor(R.color.solid_yellow);
		colorBoxSelected = getResources().getColor(R.color.solid_yellow);

		Sound.initSound();

		tvScore = (TextView)findViewById(R.id.tv_score_points);
		tvLevel = (TextView)findViewById(R.id.tv_level);
		tvCoins = (TextView)findViewById(R.id.tv_coins);

		level = 1;
		score = 0;
		coins = 0;

		tvScore.setText("0");
		tvCoins.setText("0");
		tvLevel.setText("Level 1");

	} 

	/////////////////////////////////////////////
	//TODO: check how heavy is the initial layout, then decide whether to post this as runnable
	private void addRow() {

		layout.post(new Runnable() {

			@Override
			public void run() {

				System.out.println("start adding row");

				//				System.out.println("ankur Movie name FETCHED  " + movieList.get(currentRows));


				//Remove all the movies which exceed the width of the device
				while (movieList.get(totalRowsAdded).length() > MAX_BLOCKS){

					System.out.println("ankur Movie name RMEOVED !!" + movieList.remove(totalRowsAdded));
					completeMovieList.get(0).remove(totalRowsAdded); //TODO: ERROR! remove such names from both lists
				}


				String curMovie = movieList.get(totalRowsAdded);
				//				System.out.println("ankur Movie name ADDED **" + curMovie);

				int totalCellsForThisRow = curMovie.length();

				LinearLayout row  = new LinearLayout(mContext); 
				row.setLayoutParams(rowParams);
				row.setGravity(Gravity.CENTER);
				row.setAnimation(configureAnimation());
				row.setTag( completeMovieList.get(0).get(totalRowsAdded));
				System.out.println("Ankur jumbled = " + curMovie + " ,correct =" + completeMovieList.get(0).get(totalRowsAdded) );
				row.setContentDescription(completeMovieList.get(2).get(totalRowsAdded));
				row.setId(totalRowsAdded);

				animationFalling.start();
				Sound.playSound(SOUND_START_FALL);
				for(int j = 0 ; j < totalCellsForThisRow; j++){
					//create cells
					FrameLayout cell = new FrameLayout(mContext);
					cell.setLayoutParams(cellParams);
					//					cell.setBackgroundColor(colorBox);

					//create textviews
					TextView textView = new TextView(mContext); 
					textView.setId(totalRowsAdded);
					//					textView.setTypeface(mFontStyle);
					textView.setTypeface(null, Typeface.BOLD);
					textView.setText( ( ""+curMovie.charAt(j) ).toUpperCase()); // Remove this when deciding casing as a level or hint.. TODO:
					//					textView.setTextAppearance(mContext, android.R.style.TextAppearance_Large);
					textView.setTextSize(getSizeInPixels(BLOCK_SIZE_SP - 22));//
					textView.setTextColor(colorText);
					textView.setBackgroundResource(R.drawable.textview_bg);

					( (GradientDrawable) textView.getBackground() ).setColor(colorBox);
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
						totalRowHeight += rowHeight;
						totalRowsAdded++;
						currRows++;
						System.out.println("ROW ADDED !! totalRowHeight = " + totalRowHeight + " , currentRows = " +totalRowsAdded);
						layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
						handler.sendMessageDelayed(Message.obtain(handler, 9), startTime );

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
				ClipData data = ClipData.newPlainText("AA", ((TextView)view).getText()); 
				( (GradientDrawable) view.getBackground() ).setColor(colorBoxSelected);
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

				//start dragging the item touched
				view.startDrag(data, shadowBuilder, view, 0);

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
					( (GradientDrawable) srcView.getBackground() ).setColor(colorBox);
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
					( (GradientDrawable) destView.getBackground() ).setColor(colorBoxSelected);
				}
				break;

			case DragEvent.ACTION_DRAG_EXITED:        
				if (srcView.getId() == destView.getId())
					( (GradientDrawable) destView.getBackground() ).setColor(colorBox);
				break;

			case DragEvent.ACTION_DRAG_ENDED:
				if (srcView.getId() == destView.getId())
					( (GradientDrawable) destView.getBackground() ).setColor(colorBox);

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
			
			System.out.println("Correct answer : totalRowHeight = " +totalRowHeight );
			Sound.playSound(SOUND_ANS_CORRECT);


			int localScore = Integer.parseInt(layout.getContentDescription().toString()) ;

			if (localScore >= 1000){
				score += 100; 
				tvScore.setText(Integer.toString(score ));

				coins += 10 ; 
				tvCoins.setText(Integer.toString(coins) );
			}
			else if ( (localScore < 1000) && (localScore >= 500) ){
				score += 200; 
				tvScore.setText(Integer.toString(score ));

				coins += 20 ; 
				tvCoins.setText(Integer.toString(coins) );
			}
			else if ( (localScore < 500) && (localScore >= 200) ){
				score += 300; 
				tvScore.setText(Integer.toString(score ));

				coins += 30 ; 
				tvCoins.setText(Integer.toString(coins) );
			}
			else if ( (localScore < 200) && (localScore >= 100) ){
				score += 400; 
				tvScore.setText(Integer.toString(score ));

				coins += 40 ; 
				tvCoins.setText(Integer.toString(coins) );
			}
			else if ( (localScore < 100) && (localScore >= 50) ){
				score += 500; 
				tvScore.setText(Integer.toString(score ));

				coins += 50 ; 
				tvCoins.setText(Integer.toString(coins) );
			}
			else if ( (localScore < 50) && (localScore >= 20) ){
				score += 1000; 
				tvScore.setText(Integer.toString(score ));

				coins += 100 ; 
				tvCoins.setText(Integer.toString(coins) );
			}
			else if ( (localScore < 20)){
				score += 2000; 
				tvScore.setText(Integer.toString(score ));

				coins += 200 ; 
				tvCoins.setText(Integer.toString(coins) );
			}
			
			if ( (currRows == 0) && (totalRowsAdded == 10) )
				Toast.makeText(mContext, "Level Cleared" , Toast.LENGTH_LONG).show();

		}else{

			//			Sound.playSound(SOUND_ANS_WRONG);			
			//			Animation shakeAnimation = AnimationUtils.loadAnimation(GameScreen.this, R.anim.shake);
			//			layout.startAnimation(shakeAnimation);
		}
	}

	int screenHeight = 0;
	int screenWidth  = 0;
	private void doViewMeasurements(LinearLayout layout) {
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
				System.out.println("ankur:  Actual Screen Height = " + screenHeight + " Width = " + screenWidth);   

				// Now calculate the height that our layout can be set
				// If you know that your application doesn't have statusBar added, then don't add here also. Same applies to application bar also 
				layoutHeight = screenHeight - (titleBarHeight + statusBarHeight + getSizeInPixels(ACTIONBAR_SIZE_SP));
				System.out.println("ankur: Layout Height = " + layoutHeight);   		

				MAX_BLOCKS = screenWidth / (getSizeInPixels(BLOCK_SIZE_SP) + ( 2 * getSizeInPixels(GENERIC_MARGIN_SP) ) );

				System.out.println("ankur MAX_BLOCKS = " +MAX_BLOCKS);

				rowWidth = (getSizeInPixels(BLOCK_SIZE_SP) * MAX_BLOCKS) + ( (MAX_BLOCKS * 2) * getSizeInPixels(GENERIC_MARGIN_SP) );
				rowHeight =  ( getSizeInPixels(BLOCK_SIZE_SP) + ( 2 * getSizeInPixels(GENERIC_MARGIN_SP)) ); 

				colWidth = getSizeInPixels(BLOCK_SIZE_SP);
				colHeight = getSizeInPixels(BLOCK_SIZE_SP); 

				margin = getSizeInPixels(GENERIC_MARGIN_SP);

				rowParams = new LinearLayout.LayoutParams( rowWidth, rowHeight); 
				cellParams = new LinearLayout.LayoutParams( colWidth, colHeight); 
				cellParams.setMargins( margin, margin, margin, margin);

				//				mFontStyle = Typeface.createFromAsset(mContext.getResources().getAssets(), /*"BEBAS___.ttf" */"ClearSans-Bold.ttf");

				System.out.println("copy list");
				completeMovieList = GameApp.getAppInstance().getMovieNames();
				movieList = GameApp.getAppInstance().getMovieNames().get(1);
				System.out.println("Total no of movies available = " +movieList.size());


				//Trigger row addition
				addRow();

			}
		});
	}

	private int getSizeInPixels(int dp){
		return (int) ( (dp * getResources().getDisplayMetrics().density)  + 0.5f) ; 
	}


	private TranslateAnim animationFalling; 
	private TranslateAnimation configureAnimation() {
		animationFalling = new TranslateAnim(Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f,
				Animation.RELATIVE_TO_PARENT, -1.0f,
				Animation.RELATIVE_TO_PARENT, 0.0f);

		//		animationFalling.setAnimationListener(this);
		animationFalling.setDuration(startTime);
		animationFalling.setFillAfter(true);
		//		animationFalling.start();

		return animationFalling;
	}

	@Override
	public void onAnimationEnd(Animation animation) {



		System.out.println(" onAnimationEnd start");

		if (layoutHeight < (totalRowHeight + rowHeight) && ( totalRowsAdded < movieList.size() )) {

			Sound.playSound(SOUND_GAME_OVER);
			System.out.println("MAX boxes added !!");
			Toast.makeText(this, "MAX boxes added !!", Toast.LENGTH_LONG).show();
			startActivity(new Intent(this, GameOverScreen.class));
			finish();

		}else{

			System.out.println("Add new row now");
			addRow();
		}
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
		//		tvList.clear();
		//		selectedBoxId = -1;
	}


	@Override
	protected void onResume() {
		super.onResume();
		handler.setActivity(this);
		handler.resume();
		animationFalling.resume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		handler.pause();
		animationFalling.pause();
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
		System.out.println("onAnimationStart");
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

			if (totalRowsAdded <10)
				addRow();
		}

	}

}
