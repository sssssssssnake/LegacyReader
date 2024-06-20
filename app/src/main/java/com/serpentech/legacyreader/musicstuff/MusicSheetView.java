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


import com.serpentech.legacyreader.StaticStuff;
import com.serpentech.legacyreader.filemanagement.CustomFileManager;
import com.serpentech.legacyreader.filemanagement.xmlmanage.XmlGrab;
import com.serpentech.legacyreader.musicstuff.music.Song;

import java.util.ArrayList;
import java.util.List;


public class MusicSheetView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int viewWidth;
    private int viewHeight;
    public boolean goodToDrawMusic = false;
    Song song;
    MeasureDrawing measureDrawing;
//    MathStuff mathStuff = new MathStuff();
//    MusicObjectified musicObject = new MusicObjectified();
//    MusicObjectified.Measure testMeasure;
    XmlGrab xmlGrab = new XmlGrab();

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
        StaticStuff.MusicSpacing.densityOfPixels = getResources().getDisplayMetrics().density;
//        mathStuff.darkMode = isDarkTheme(getContext());
        StaticStuff.MusicSpacing.darkMode = StaticStuff.isDarkTheme(getContext());
        Log.d("MusicSheetView", "Dark mode: " + StaticStuff.MusicSpacing.darkMode);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Store the width and height of the view
        viewWidth = w;
        viewHeight = h;

        StaticStuff.MusicSpacing.densityOfPixels = getResources().getDisplayMetrics().density;

        Log.d("MusicSheetView", "Width: " + viewWidth + " Height: " + viewHeight);
        StaticStuff.musicSheetViewDimensionsPx = new int[] {viewWidth, viewHeight};


    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Use viewWidth and viewHeight here
        paint.setColor(Color.MAGENTA);
        paint.setStrokeWidth(5);

//        goodToDrawMusic = (StaticStuff.chosenFile != null) && (!StaticStuff.isAlreadyDrawn);
        goodToDrawMusic = (StaticStuff.chosenFile != null);
        Log.d("MusicSheetView", "goodToDrawMusic: " + goodToDrawMusic);
        if (goodToDrawMusic) {
            song = new Song(CustomFileManager.readFile(StaticStuff.chosenFile.filepath));
            System.gc();
            Log.d("MusicSheetView", "Song created");
            // start to implement logic for drawing the music!!
            measureDrawing = new MeasureDrawing(song, paint, canvas);
            measureDrawing.drawLines();
//            StaticStuff.isAlreadyDrawn = true;
        } else if (StaticStuff.isAlreadyDrawn && (StaticStuff.chosenFile != null)) {
            measureDrawing.drawLines();
        }

        // need to delete at some point, but will probably be useful
//        if(goodToDrawMusic){
//            musicObject = new MusicObjectified();
//            System.gc();
//            String fileContent = CustomFileManager.readFile(StaticStuff.chosenFile.filepath);
//            List<XmlGrab.XmlGroup> measures = xmlGrab.scanXMLForKeywordList(fileContent, "measure");
//            System.gc();
//            musicObject.measures = new ArrayList<>();
//            for (XmlGrab.XmlGroup measure : measures) {
//                musicObject.measures.add(musicObject.new Measure(measure.contents));
//            }
//            Log.d("MusicSheetView", "Number of measures: " + musicObject.measures.size());
//        }



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
