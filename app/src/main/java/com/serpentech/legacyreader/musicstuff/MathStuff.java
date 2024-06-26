package com.serpentech.legacyreader.musicstuff;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import java.util.List;

@Deprecated
public class MathStuff {
    public int[] dimentions = new int[2];

    private List<MusicObjectified.Measure> song;

    // there is staff seperation, line seperation, part seperation and note seperation

    int noteSpace = 10;
    int lineSpace = 10;
    int partSpace = 30;
    int staffSpace = (lineSpace * 4) + 40;
    public double densityOfPixels;
    boolean darkMode;

    int linesDrawn = 0;


    /**
     * This is a method for testing that simpy draws the measures and lines on the screen.
     * @param canvas The canvas to draw on.
     * @param paint the paint passed through, probably will be altered
     * @param percentWidth The width of the measure to be drawn in percent of the screen
     * @param numberOfStaves The number of staves to be drawn on the screen.
     */
    public void drawSingleMeasurePercentage(Canvas canvas, Paint paint, float percentWidth, int numberOfStaves) {
        // starting variables
        Log.d("MathStuff", "Physical width: " + dimentions[0] + "px");
        int currentStaveY = 0;
        int currentLineY = 0;
        int stoppingPoint = Math.round(percentWidth * dimentions[0]);
        int lowestLine = 0;
        Log.d("MathStuff", "Stopping point: " + stoppingPoint);
        Log.d("MathStuff", "Ratio" + ((double) stoppingPoint) / ((double) dimentions[0]));

        //colours
        paint.setColor(0xFF000000);
        // if in dark mode, change the colour
        if (darkMode) {
            paint.setColor(0xFFAAAAAA);
        }

        paint.setStrokeWidth(dpToPx(2));

        // make a for loop for the staves
        for (int i = 0; i < numberOfStaves; i++) {
            // make a for loop for the 5 lines
            for (int j = 0; j < 5; j++) {
                canvas.drawLine(
                        0,
                        currentStaveY + currentLineY,
                        stoppingPoint,
                        currentStaveY + currentLineY,
                        paint
                );
                currentLineY += dpToPx(lineSpace);
            }
            if (currentStaveY + currentLineY > lowestLine) {
                lowestLine = currentStaveY + currentLineY - dpToPx(lineSpace);
            }
            currentStaveY += dpToPx(staffSpace);
            currentLineY = 0;
        }
        // draw the virtical line at the end of the measure
        canvas.drawLine(
                stoppingPoint,
                0,
                stoppingPoint,
                lowestLine,
                paint
        );
    }
    public void drawMovingMeasure(Canvas canvas, Paint paint, float[] startingCoordinates, MusicObjectified.Measure measure) {

    }
    public float[] estimateMeasureDimentions(MusicObjectified.Measure measure) {
        float[] dimentions = new float[2];
        dimentions[0] = 0;
        dimentions[1] = 0;
        int staveNumber = measure.staves;
        float height = 0;
        float width = 0;

        // find how many stave breaks there are

        height = (staveNumber-1) * dpToPx(staffSpace);
        height += 4 * dpToPx(lineSpace);

        width = measure.notes.size() * dpToPx(noteSpace);




        return new float[]{width, height};
    }

//    public void drawSingleMeasure(Canvas canvas, Paint paint, float percentWidth, int numberOfStaves) {



    public int dpToPx(double dp) {
        return (int) (dp * densityOfPixels);
    }


}

