package com.mangolion.mangoview.button;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.mangolion.mangoview.R;

public class ApplyReflective {
	/**
	 * The glass background for the Window's glass reflective effect, similar to
	 * the Aero effect of window 7
	 */
	public static Bitmap bgGlass;
	/**
	 * The parent activity of the view
	 */
	public Activity activity;
	
	
	public static void init(Activity activity) {
		if (bgGlass == null) {
			// get the screen size
			WindowManager wm = (WindowManager) activity
					.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			int width = size.x;
			int height = size.y;

			// stretch the reflective glass background to screen size
			bgGlass = BitmapFactory.decodeResource(activity.getResources(),
					R.drawable.bgglass);
			bgGlass = Bitmap.createScaledBitmap(bgGlass, width, height, false);
		}
	}
	
	public static void drawReflection(Canvas canvas, View view) {
			// get the real coordinates of the view in the screen
			int location[] = { 0, 0 };
			int screenx = location[0], screeny = location[1];
			view.getLocationInWindow(location);
			Rect src = new Rect((int) location[0], (int) location[1],
					(int) location[0] + view.getWidth(), (int) location[1]
							+ view.getHeight());
			Rect dst = new Rect(0, 0, view.getWidth(), view.getHeight());
			canvas.drawBitmap(bgGlass, src, dst, null);
	}
}
