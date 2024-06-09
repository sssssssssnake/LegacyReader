package com.serpentech.legacyreader.musicstuff;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;


public class MusicSheetView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int viewWidth;
    private int viewHeight;
    MathStuff mathStuff = new MathStuff();

    // Constructors
    public MusicSheetView(Context context) {
        super(context);
        init();
    }

    public MusicSheetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MusicSheetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialization code here
        mathStuff.densityOfPixels = getResources().getDisplayMetrics().density;
        mathStuff.darkMode = isDarkTheme(getContext());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Store the width and height of the view
        viewWidth = w;
        viewHeight = h;

        mathStuff.densityOfPixels = getResources().getDisplayMetrics().density;

        Log.d("MusicSheetView", "Width: " + viewWidth + " Height: " + viewHeight);
        mathStuff.dimentions= new int[] {viewWidth, viewHeight};

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Use viewWidth and viewHeight here
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(5);

        // Example: Draw a line across the view for testing
        canvas.drawLine(0, 0, mathStuff.dimentions[0], mathStuff.dimentions[1], paint);
        // Continue drawing measures and notes here
        mathStuff.drawSingleMeasurePercentage(canvas, paint, 0.5f, 2);


    }

    public void redraw(){
        invalidate();
    }
    public boolean isDarkTheme(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                // Dark theme is active
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
                // Dark theme is not active
                return false;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // We're not sure what the theme is, proceed with default
                return false;
        }
        return false; // Default to non-dark mode
    }
}
