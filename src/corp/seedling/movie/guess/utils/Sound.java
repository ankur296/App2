package corp.seedling.movie.guess.utils;

import android.media.AudioManager;
import android.media.SoundPool;
import corp.seedling.movie.guess.R;
import corp.seedling.movie.guess.app.GameApp;

public class Sound {

	private static final int rIdSwipe = R.raw.swipe_long;
	private static int sidSwipe = -1;
	private static final int rIdCorrect = R.raw.correct;
	private static int sidCorrect = -1;
	private static final int rIdOver= R.raw.bell;
	private static int sidStart = -1;
	private static final int rIdStart = R.raw.start;
	
	private static int sidOver = -1;
	private static boolean loaded = false;
	private static SoundPool soundPool;
	private static final int SOUND_SWIPE = 1;
	private static final int SOUND_ANS_CORRECT = 2;
	private static final int SOUND_GAME_OVER  = 3;
	private static final int SOUND_START_FALL  = 4;


	public static SoundPool.OnLoadCompleteListener listener =  
			new SoundPool.OnLoadCompleteListener(){
		@Override
		public void onLoadComplete(SoundPool soundPool, int sid, int status){ // could check status value here also

			System.out.println("ankur sound onLoadComplete");
			if (sidSwipe == sid  || sidCorrect == sid || sidOver == sid || sidStart == sid) {
				loaded = true;
			}
		}
	};

	public static void initSound() {
		System.out.println("ankur init sound");
		soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
		soundPool.setOnLoadCompleteListener(listener);
		sidSwipe = soundPool.load(GameApp.getAppInstance(), rIdSwipe, 1); 
		sidCorrect = soundPool.load(GameApp.getAppInstance(), rIdCorrect, 1);
		sidOver = soundPool.load(GameApp.getAppInstance(), rIdOver, 1);
		sidStart = soundPool.load(GameApp.getAppInstance(), rIdStart, 1);
	}

	public static void unloadSound() {
		soundPool.unload(sidSwipe); 
		soundPool.unload(sidCorrect); 
		soundPool.unload(sidOver); 
		soundPool.unload(sidStart); 
		}


	public static void playSound(final int soundType) {

		//		if (isMute == false) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (loaded) {

					switch (soundType) {

					case SOUND_SWIPE:
						soundPool.play(sidSwipe, 1.0f, 1.0f, 1, 0, 1f);
						break;

					case SOUND_ANS_CORRECT:
						soundPool.play(sidCorrect, 1.0f, 1.0f, 1, 0, 1f);
						break;

					case SOUND_GAME_OVER:
						soundPool.play(sidOver, 1.0f, 1.0f, 1, 0, 1f);
						break;

					case SOUND_START_FALL:
						soundPool.play(sidStart, 1.0f, 1.0f, 1, 0, 1f);
						break;

					default:
						soundPool.play(sidSwipe, 1.0f, 1.0f, 1, 0, 1f);
						break;
					}

				}
			} }).start();

		//		}
	}
}
