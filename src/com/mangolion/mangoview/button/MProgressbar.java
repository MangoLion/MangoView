package com.mangolion.mangoview.button;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.mangolion.mangoview.R;

public class MProgressbar extends TextView {
	/**
	 * the resId of the running animation
	 */
	public static int progressid = R.drawable.glowgradient;
	/**
	 * the resId of the glowing text background
	 */
	public static int textglowid = R.drawable.bgglow;
	/**
	 * the resId of the glowing line in the lower half of the bar
	 */
	public static int glowlineid = R.drawable.glowline;
	/**
	 * The bitmap of the running animation
	 */
	public Bitmap bitRunning;
	/**
	 * The bitmap of the glowing line
	 */
	public Bitmap bitLine;
	/**
	 * The bitmap of the text's glowing background
	 */
	public Bitmap bitTextGlow;
	private int progress;
	/**
	 * This is the running animation's counter, which helps determine the
	 * animation's x location
	 */
	AtomicInteger efprogress = new AtomicInteger(0);
	/**
	 * The thread that runs the animation in a loop
	 */
	Thread threadProgress;
	/**
	 * the color of the bar
	 */
	private int color;

	private void init() {
		setGravity(Gravity.CENTER);

		if (isInEditMode()) {
			return;
		}

		// this is the thread that moves the running rectangle
		threadProgress = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					while (true) {
						Thread.sleep(50);
						// the rectangle has to run pass the progress bar
						// completely before moving back to the beginning, hence
						// it has to go to 120% instead of 100
						if (efprogress.addAndGet(8) > 120) {
							Thread.sleep(1200);
							efprogress.set(-20);
						}
						postInvalidate();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		threadProgress.start();

		// glow line is the glowing line in the bottom half of the bar
		setBackgroundResource(R.drawable.glowline);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected synchronized void onDraw(Canvas canvas) {

		// don't draw if view is in xml editor
		if (isInEditMode()) {
			return;
		}

		// random increment
		if ((new Random()).nextInt(12) == 1)
			setProgress(progress + 1);
		if (progress > 100)
			progress = 0;

		// load the static bitmaps if they haven't been loaded
		if (bitRunning == null) {
			bitRunning = BitmapFactory.decodeResource(getContext()
					.getResources(), progressid);
			bitRunning = Bitmap.createScaledBitmap(bitRunning,
					bitRunning.getWidth(), getHeight(), false);

			bitLine = BitmapFactory.decodeResource(getContext().getResources(),
					glowlineid);

			bitTextGlow = BitmapFactory.decodeResource(getContext()
					.getResources(), R.drawable.bgglow);
		}

		// draw x location of running rectangle
		int drawX = efprogress.get() * getWidth() / 100;
		// find the limiting width where the progress bar is reaching to
		int drawLimit = getProgress() * getWidth() / 100;

		Paint alphapaint = new Paint();
		alphapaint.setColor(color);
		// draw the "progress" part of the bar
		canvas.drawRect(0, 0, drawLimit, getHeight(), alphapaint);

		// first calculate where the running rectangle will reach
		int drawGlowLimit = drawX + bitRunning.getWidth() / 2;
		Rect rect = null;

		// if this reach is further than draw limit, limit this reach
		if (drawGlowLimit > drawLimit) {
			rect = new Rect(0, 0, bitRunning.getWidth()
					- (drawGlowLimit - drawLimit), bitRunning.getHeight());
			drawGlowLimit = drawLimit;
		}

		canvas.drawRect(0, 0, drawLimit, getHeight(), alphapaint);
		alphapaint = new Paint();
		alphapaint.setAlpha(90);

		// draw the running rectangle
		canvas.drawBitmap(bitRunning, rect,
				new Rect(drawX - bitRunning.getWidth() / 2, 0, drawGlowLimit,
						getHeight()), alphapaint);

		// draw the glowing line
		canvas.drawBitmap(bitLine, null,
				new Rect(0, 0, drawLimit, getHeight()), alphapaint);

		// draw the glow behind the progress text
		canvas.drawBitmap(bitTextGlow, getWidth() / 2 - bitTextGlow.getWidth()
				/ 2, getHeight() / 2 - bitTextGlow.getHeight() / 2, null);
		setText(String.valueOf(getProgress()));
		super.onDraw(canvas);
	}

	// get set stuff

	public int getProgress() {
		return progress;
	}

	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
	}

	/**
	 * @return the color of the bar
	 */
	public int getColor() {
		return color;
	}
	/**
	 * @param color the color that the bar will be set to
	 */
	public void setColor(int color) {
		this.color = color;
		invalidate();
	}

	/**
	 *Make the color darker
	 * @param color
	 * @return the darker color
	 */
	public static int darker(int color) {
		int alpha = Color.alpha(color);
		int r = Color.red(color);
		int b = Color.blue(color);
		int g = Color.green(color);

		return Color
				.argb(alpha, (int) (r * .7), (int) (g * .7), (int) (b * .7));
	}

	// boring constructors
	public MProgressbar(Context context) {
		super(context, null);
		init();
	}

	public MProgressbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public MProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
}
