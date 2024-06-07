package com.serpentech.legacyreader.musicstuff;

import android.content.Context;
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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Store the width and height of the view
        viewWidth = w;
        viewHeight = h;
        Log.d("MusicSheetView", "Width: " + viewWidth + " Height: " + viewHeight);
        MathStuff.dimentions= new int[] {viewWidth, viewHeight};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Use viewWidth and viewHeight here
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);

        // Example: Draw a line across the view for testing
        canvas.drawLine(0, 0, viewWidth, viewHeight, paint);
        // Continue drawing measures and notes here


    }

    public void redraw(){
        invalidate();
    }
}
