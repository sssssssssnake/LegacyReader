package com.serpentech.legacyreader.musicstuff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.util.Log;

import com.caverock.androidsvg.PreserveAspectRatio;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.serpentech.legacyreader.R;
import com.serpentech.legacyreader.filemanagement.ConfigXmlJson;
import com.serpentech.legacyreader.filemanagement.CustomFileManager;
import com.serpentech.legacyreader.filemanagement.xmlmanage.XmlGrab;

import java.io.File;
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

//            List<MusicObjectified.Note> notes = new ArrayList<>();
//            notes.add(musicObject.new Note(
//                    new int[] {1,4},
//                    musicObject.new SimpleNote('b', 4, 0),
//                    0,
//                    1
//            ));
//            testMeasure = musicObject.new Measure(
//                    1,
//                    4,
//                    new int[] {4,4,},
//                    notes,
//                    3
//            );
            ConfigXmlJson.workingFileList = ConfigXmlJson.readConfigXml();
            Log.d("MusicSheetView", "Working File List: " + ConfigXmlJson.workingFileList.size());
            if (!ConfigXmlJson.workingFileList.isEmpty()) {
                String measurePath = ConfigXmlJson.workingFileList.get(0).fullPath;
                XmlGrab xmlGrab = new XmlGrab();
                Log.d("MusicSheetView", "Measure Path: " + measurePath);
                // read the xml file
                String xmlContent = CustomFileManager.readFile(measurePath);
                String measureString = xmlGrab.scanXMLForKeywordList(xmlContent, "measure").get(0).contents;
//                Log.d("MusicSheetView", "Measure String: " + measureString);
                testMeasure = musicObject.new Measure(measureString);
                Log.d("MusicSheetView", "Measure Notes: " + testMeasure.notes.size());
                goodToDrawMusic = true;
            } else {
                goodToDrawMusic = false;
            }
        } else {
            float[] measureDimensions = mathStuff.estimateMeasureDimentions(testMeasure);

            // Log the measure dimensions
            Log.d("MusicSheetView", "Measure Dimensions: " + measureDimensions[0] + " x " + measureDimensions[1]);

            Log.d("MusicSheetView", "Number of Notes " + testMeasure.notes.size());

            // Draw a vertical and horizontal line for testing
            canvas.drawLine(0, measureDimensions[1], mathStuff.dimentions[0], measureDimensions[1], paint);
            canvas.drawLine(measureDimensions[0], 0, measureDimensions[0], mathStuff.dimentions[1], paint);

            // Draw the measure
            mathStuff.drawSingleMeasurePercentage(canvas, paint, measureDimensions[0] / mathStuff.dimentions[0], testMeasure.staves);
            SVG svg = null;
            try {
                svg = SVG.getFromResource(getContext(), R.raw.firstnote);
                RectF viewBox = new RectF(0,0,10f,10f);

                // Set the SVG to fit within the bounding box
                svg.setDocumentViewBox(0, 0, .1f/viewBox.width(), .1f/viewBox.height());
                // Log the width and height of the SVG
                Log.d("MusicSheetView", "SVG Width: " + viewBox.width() + " Height: " + viewBox.height());
                svg.setDocumentPreserveAspectRatio(PreserveAspectRatio.LETTERBOX);


                // Now render the SVG to the canvas
                svg.renderToCanvas(canvas, viewBox);
            } catch (SVGParseException e) {
                throw new RuntimeException(e);
            }

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
