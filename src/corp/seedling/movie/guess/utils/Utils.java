package corp.seedling.movie.guess.utils;

import corp.seedling.movie.guess.R;
import corp.seedling.movie.guess.app.GameApp;
import android.util.DisplayMetrics;

public class Utils {

	//TODO: Move this shit to utils class
	//TODO: add more colors
	private static final int[] COLOR_RES = {R.color.color_1_indigo , R.color.color_2_red_orangish , R.color.color_3_pink_cancer ,
		R.color.color_4_blue_royal ,
		R.color.color_5_pink_dull , R.color.color_6_orange , R.color.color_7_green_bright , R.color.color_8_blue_bright ,
		R.color.color_9_blue_light , R.color.color_10_purple ,
		R.color.color_11_red_dark , R.color.color_12 , R.color.color_13 , R.color.color_14 , R.color.color_15 , R.color.color_16 ,
		R.color.color_17 , R.color.color_18 , R.color.color_19 , R.color.color_20 , R.color.color_21 , R.color.color_22 ,
		R.color.color_23 , R.color.color_24 , R.color.color_25 , R.color.color_26 , R.color.color_27 , R.color.color_28 ,
		R.color.color_29 , R.color.color_30 , R.color.color_31 , R.color.color_32 , R.color.color_33 , R.color.color_34 ,
		R.color.color_35 , R.color.color_36 , R.color.color_37 , R.color.color_38 , R.color.color_39 , R.color.color_40 ,
		R.color.color_41 , R.color.color_42 , R.color.color_43 , R.color.color_44 , R.color.color_45 , R.color.color_46 ,
		R.color.color_47 , R.color.color_48 , R.color.color_49 , R.color.color_50 , R.color.color_51 
	};

	
	public static int getRandomColor(){
		return GameApp.getAppInstance().getResources().getColor(COLOR_RES[(int)(1000000.0D * Math.random()) % 19] );
	}
	
	
	public static int getSizeInPixels(int dp){
		return (int) ( (dp * GameApp.getAppInstance().getResources().getDisplayMetrics().density)  + 0.5f) ; 
	}


	
	public static int getSizeInDp(float px){
		DisplayMetrics metrics = GameApp.getAppInstance().getResources().getDisplayMetrics();
		return (int) (px / (metrics.densityDpi / 160f) );
	}

}
