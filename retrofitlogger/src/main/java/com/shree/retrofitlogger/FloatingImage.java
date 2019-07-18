package com.shree.retrofitlogger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.*;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class FloatingImage extends androidx.appcompat.widget.AppCompatImageView implements OnTouchListener {
    WindowManager windowManager; // to hold our image on screen
    Context ctx; // context so in case i use it somewhere.
    GestureDetector gestureDetector; // to detect some listener on the image.
    WindowManager.LayoutParams params; // layoutParams where i set the image height/width and other.
    WindowManager.LayoutParams paramsF;
    int initialX;
    int initialY;
    float initialTouchX;
    float initialTouchY;

    /**
     * @param context
     */
    @SuppressLint("WrongConstant")
    public FloatingImage(Context context) {
        super(context);
        this.ctx = context;
        this.setOnTouchListener(this); // setting touchListener to the imageView
        this.setImageResource(R.drawable.bug); // setting icon to the imageView
        gestureDetector = new GestureDetector(ctx, new GestureListener());
        windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE); // ini the windowManager
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT); // assigning height/width to the imageView
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM; // assigning some flags to the layout
        params.gravity = Gravity.TOP | Gravity.LEFT; // setting the gravity of the imageView
        params.windowAnimations = android.R.style.Animation_Toast; // adding an animation to it.
        params.x = 0; // horizontal location of imageView
        params.y = 100; // vertical location of imageView
        params.height = 120; // given it a fixed height in case of large image
        params.width = 120; // given it a fixed width in case of large image
        params.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
        try {
            windowManager.addView(this, params); // adding the imageView & the  params to the WindowsManger.
        } catch (Exception ex) {

        }
    }

    /**
     * @param context
     * @param attrs
     */
    public FloatingImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public FloatingImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param v
     * @param event
     * @return true/false
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        paramsF = params; // getting the layout params from the current one and assigning new one.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                paramsF = params;
                initialX = paramsF.x; // Horizontal location of the ImageView
                initialY = paramsF.y; // Vertical location of the ImageView
                initialTouchX = event.getRawX(); //X coordinate  location of the ImageView
                initialTouchY = event.getRawY(); //Y coordinate  location of the ImageView
                break;
            case MotionEvent.ACTION_UP: // this called when we actually leave the Imageview
                break;
            case MotionEvent.ACTION_MOVE:
                paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                windowManager.updateViewLayout(this, paramsF);
                break;
        }
        return false; // returning false otherwise any touch event on the imageView wont work.
    }

    private class GestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {// When there is a touch event on the imageView
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) { // perform Double tap on the ImageView
            Toast.makeText(ctx, "You Double Tapped", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) { // perform single tap on the ImageView
            Toast.makeText(ctx, "Openings Logs", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ctx, LogsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) { // perform long press on the ImageView
            Toast.makeText(ctx, "You Long Pressed", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * return [Remove the FloatingImage from the windowManager]
     */
    public void destroy() {
        if (windowManager != null) { // if the image still exists on the WindowManager release it.
            try {
                windowManager.removeView(this); // remove the ImageView
            } catch (Exception ex) {

            }
        }
    }
}