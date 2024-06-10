package com.serpentech.legacyreader.musicstuff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

import com.serpentech.legacyreader.filemanagement.xmlmanage.XmlGrab;

import java.util.ArrayList;
import java.util.List;


public class MusicSheetView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int viewWidth;
    private int viewHeight;
    public boolean goodToDrawMusic = false;
    MathStuff mathStuff = new MathStuff();
    MusicObjectified musicObject = new MusicObjectified();
    MusicObjectified.Measure testMeasure;

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

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Use viewWidth and viewHeight here
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(5);

        if (!goodToDrawMusic) {
            // Example: Draw a line across the view for testing
            canvas.drawLine(0, 0, mathStuff.dimentions[0], mathStuff.dimentions[1], paint);
            // Continue drawing measures and notes here
            mathStuff.drawSingleMeasurePercentage(canvas, paint, 0.5f, 2);

            List<MusicObjectified.Note> notes = new ArrayList<>();
            notes.add(musicObject.new Note(
                    new int[] {1,4},
                    musicObject.new SimpleNote('b', 4, 0),
                    0,
                    1
            ));
            testMeasure = musicObject.new Measure(
                    1,
                    4,
                    new int[] {4,4,},
                    notes,
                    3
            );
            goodToDrawMusic = true;
        } else {
            float[] measureDimensions = mathStuff.estimateMeasureDimentions(testMeasure);

            // Log the measure dimensions
            Log.d("MusicSheetView", "Measure Dimensions: " + measureDimensions[0] + " x " + measureDimensions[1]);

            // Draw a vertical and horizontal line for testing
            canvas.drawLine(0, measureDimensions[1], mathStuff.dimentions[0], measureDimensions[1], paint);
            canvas.drawLine(measureDimensions[0], 0, measureDimensions[0], mathStuff.dimentions[1], paint);

            // Draw the measure
            mathStuff.drawSingleMeasurePercentage(canvas, paint, measureDimensions[0] / mathStuff.dimentions[0], testMeasure.staves);

        }


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
