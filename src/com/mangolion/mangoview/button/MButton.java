package com.mangolion.mangoview.button;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

import com.mangolion.mangoview.R;

public class MButton extends Button {
	// http://all-graphic.net/en/web-site/button/item/the-rectangular-gray-glass-button-for-a-web-site-psd-photoshop
	/**
	 * toggle the button's glowing text background effect
	 */
	public boolean glowtext = true;
	/**
	 * toggle the button's glowing background effect when the user holds the
	 * button
	 */
	public boolean glowtouch = true;
	/**
	 * The resId of the glowing background of text
	 */
	public static int glowtextId = R.drawable.bgglow;
	/**
	 * The resID of the dynamic glowing background when the user holds the
	 * button
	 */
	public static int bitGlowTouchId = R.drawable.glowgradient;
	/**
	 * the resId for the border(main background) of the button
	 */
	public static int bitBorderId = R.drawable.buttonglass;
	/**
	 * The resId of the lighted border bitmap when the user starts pressing
	 */
	public static int bitButtonClicked = R.drawable.buttonglow;
	/**
	 * The bitmap for the dynamic glowing background when the user holds the
	 * button
	 */
	public Bitmap glowtouchbitmap;
	/**
	 * The bitmap of the lighted border bitmap when the user starts pressing
	 */
	public NinePatchDrawable glowClicked;
	/**
	 * This is the thread that handles the dynamic glowing background when the
	 * user holds button
	 */
	Thread threadTouch;
	/**
	 * trivial temproray vars for calculating touch
	 */
	private Integer eventX = 0, eventY = 0, touchX = 0, touchY = 0;
	/**
	 * The alpha value of the dynamic glowing background, need to be synchonzied
	 * accross 2 threads
	 */
	AtomicInteger malpha = new AtomicInteger();
	/**
	 * Tells the {@link #threadTouch} whether the user is holding the button
	 */
	AtomicBoolean isTouching = new AtomicBoolean();

	/**
	 * load the initialized bitmaps and start the {@link #threadTouch}
	 */
	private void init() {
		if (glowtouchbitmap == null) {
			glowtouchbitmap = BitmapFactory.decodeResource(getContext()
					.getResources(), bitGlowTouchId);
		}
		if (glowClicked == null) {
			glowClicked = (NinePatchDrawable) getContext().getResources()
					.getDrawable(bitButtonClicked);
		}

		setBackgroundResource(bitBorderId);
		malpha.set(0);
		isTouching.set(false);
		threadTouch = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(40);

						// Increase the alpha if the user is touching and
						// vice-versa
						if (isTouching.get()) {
							if (malpha.get() < 160) {
								if (malpha.get() + 12 < 160)
									malpha.set((malpha.get() + 12));
								postInvalidate();
							}
						} else if (malpha.get() > 0) {
							malpha.set((malpha.get() - 6));
							postInvalidate();
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		threadTouch.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isEnabled())
			return false;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			eventX = (int) ((int) event.getX());
			eventY = (int) ((int) event.getY());

			// only if the touch is inside this button would it be count to the
			// threadTouch as isTouching
			if (eventX > 0 && eventX < getWidth() && eventY > 0
					&& eventY < getHeight()) {
				isTouching.set(true);
				touchX = eventX;
				touchY = eventY;
			} else
				isTouching.set(false);
			return true;
		case MotionEvent.ACTION_CANCEL:
			isTouching.set(false);
			break;
		case MotionEvent.ACTION_UP:
			isTouching.set(false);
			if (eventX > 0 && eventX < getWidth() && eventY > 0
					&& eventY < getHeight())
				performClick();
			return false;

		}
		return super.onTouchEvent(event);
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		// draw the glowing border if the user is touching
		if (isTouching.get()) {
			glowClicked.setBounds(0, 0, getWidth(), getHeight());
			glowClicked.draw(canvas);
		}
		// draw the dynamic glowing background at the user's finger when its
		// alpha is above 0, even when the user is no longer touching
		if (malpha.get() > 0) {
			Paint alphaPaint = new Paint();

			alphaPaint.setAlpha(malpha.get());
			canvas.drawBitmap(glowtouchbitmap, null, new Rect(
					(int) (touchX - getWidth() * 0.3), 0,
					(int) (touchX + getWidth() * 0.3), getHeight()), alphaPaint);
		}
		super.onDraw(canvas);
	}

	public MButton(Context context) {
		super(context);
		if (!isInEditMode())
			init();
	}

	public MButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode())
			init();
	}

	public MButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		if (!isInEditMode())
			init();
	}
}
