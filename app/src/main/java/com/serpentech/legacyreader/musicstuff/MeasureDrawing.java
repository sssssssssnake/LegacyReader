package com.serpentech.legacyreader.musicstuff;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import com.serpentech.legacyreader.StaticStuff;
import com.serpentech.legacyreader.musicstuff.music.Song;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is to replace MathStuff with better classes to objectify music for the screen, not just theory
 */
public class MeasureDrawing {
    List<Song.Measure> measures;
    List<Line> lines;
    DrawingLogic drawingLogic;
    int linesOnScreen;
    int currentLine = 0;
    int currentMaxHeight;
    boolean keepDrawingLines = true;
    int margins = 5;

    public MeasureDrawing(Song song, Paint paint, Canvas canvas) {
        drawingLogic = new DrawingLogic(canvas, paint);
        Log.d("MeasureDrawing", "Number of measures: " + song.measures.size());
        measures = song.measures;
        lines = new ArrayList<>();
        while (StaticStuff.needNewLine) {
            lines.add(new Line(song));
        }
        Log.d("MeasureDrawing", "Number of lines: " + lines.size());
        int totalMeasures = 0;
        for (Line line : lines) {
            totalMeasures += line.measures.size();
        }
        Log.d("MeasureDrawing", "Total measures (test): " + totalMeasures);
//        currentMaxHeight = 0;
        System.gc();
    }

    /**
     * Draws the lines on the screen
     */
    public void drawLines() {
        Log.d("MeasureDrawing", "Drawing lines");
        currentLine = StaticStuff.lastLineDrawingNumber;
        goThroughLines: while (keepDrawingLines) {
            int[] startingCoordinates = new int[2];
            // starting y is however far down the screen we have drawn so far + margin
            startingCoordinates[1] = currentMaxHeight + margins;
            Line currentWorkingLine = lines.get(currentLine);
            // See if we can draw another line
            int newTotalHeight = addNewSpacing(currentWorkingLine, currentMaxHeight, margins);
            if (newTotalHeight == 0) {
                keepDrawingLines = false;
                break goThroughLines;
            } else {
                currentMaxHeight = newTotalHeight;
            }
            // starting x is 0 from initialization already
            goThroughMeasures: for (MeasureForDrawing measure : currentWorkingLine.measures) {
                drawingLogic.drawMeasure(measure, startingCoordinates);
                // update the starting coordinates for the next measure
                startingCoordinates[0] += (int) estimateMeasureDimensions(measure.measure)[0];
            }
            currentLine++;
            StaticStuff.lastLineDrawingNumber = currentLine;
        }
        Log.d("MeasureDrawing", "Current line: " + currentLine);

    }

    /**
     * Estimates the height of a line in pixels
     * @param line The line you are probably going to draw
     * @param previousLineHeights The height of the previous lines in physical pixels
     * @param margin The margin between the lines in physical pixels
     * @return The total height of the lines in physical pixels
     */
    public int addNewSpacing(Line line, int previousLineHeights, int margin) {
        int estimatedNewLineSpace = line.estimateLineHeight();
        int maxTotalHeight = StaticStuff.musicSheetViewDimensionsPx[1];
        int newTotalPreviousLineSpace = previousLineHeights + estimatedNewLineSpace + margin;
        if (newTotalPreviousLineSpace > maxTotalHeight) {
            return 0;
        } else {
            linesOnScreen++;
            return newTotalPreviousLineSpace;
        }
    }

    /**
     * Estimates the dimensions of a measure in pixels
     * @param measure The measure you are probably going to draw
     * @return The dimensions of the measure in physical pixels
     */
    public float[] estimateMeasureDimensions(Song.Measure measure) {
        MeasureForDrawing measureForDrawing = new MeasureForDrawing(measure);
        float height = 0;
        float width = 0;
        // each stave is 40 and the distance between is also 40
        height = (measureForDrawing.staves-1) * StaticStuff.MusicSpacing.staffSpace;
        height += StaticStuff.MusicSpacing.lineSpace * 4;
        // width is how many note groups or (chords) in the measure with their individual spacing
        width = (measureForDrawing.noteGroupsSize - 1) * StaticStuff.MusicSpacing.noteSpace;
        width += StaticStuff.MusicSpacing.noteWidth * measureForDrawing.noteGroupsSize;
        width += StaticStuff.MusicSpacing.sideMargins * 2;
        float[] dimensions = {dpToPx(width), dpToPx(height)};
        System.gc();
        return dimensions;
    }


    /**
     * This is the class that will represent a measure as it is drawn on the screen
     * it contains a measure (really a pointer) and a list of note groups
     * it is used for drawing the notes, specifically horizontally across the screen.
     */
    public class MeasureForDrawing {
        public Song.Measure measure;
        // note groups represents the subdivisions of a measure in terms of where a note lands in the measure
        public int noteGroupsSize;
        public List<int[]> noteGroups;
        public int staves;

        public MeasureForDrawing(Song.Measure measure) {
            this.measure = measure;
            staves = measure.staves;
            findNoteGroups();
            // log the number of note groups
            Log.d("MeasureForDrawing", "In measure " + measure.measureNumber + " there are " + noteGroupsSize + " note groups");
        }

        /**
         * Finds the note groups (how many times a group of notes are played) in the measure and sets the noteGroupsSize and noteGroups variables
         */
        public void findNoteGroups() {
            // TODO: fix this, its broken
            List<int[]> times = new ArrayList<>();
            noteScan: for (Song.Note note : measure.notes) {
                boolean foundInArray = false;
                // check if there is nothing in the array
//                Log.d("FindNoteGroups", "Is times empty1 " + times.isEmpty());
                if (times.isEmpty()) {
                    times.add(note.positionFromStart);
//                    Log.d("FindNoteGroups", "Is times empty " + times.isEmpty());
                } else {
                    for (int[] time : times) {
                        if ((note.positionFromStart[0] == time[0]) && (note.positionFromStart[1] == time[1])) {
                            foundInArray = true;
                            break noteScan;
                        }
                    }
                    if (!foundInArray) {
                        times.add(note.positionFromStart);
                    }
                }
                Log.d("Measure" + measure.measureNumber, "Note starts at " + note.positionFromStart[0] + " " + note.positionFromStart[1]);
            }

            noteGroupsSize = times.size();
            noteGroups = times;
//            Log.d("FindNoteGroups", "In measure " + measure.measureNumber + " there are " + noteGroupsSize + " note groups");
        }
    }

    /**
     * This is the class that will represent the Lines of the song as it is drawn on the screen
     */
    public class Line {
        int lineNumber;
        List<MeasureForDrawing> measures = new ArrayList<>();
        public List<Song.StaveClef> clefs;


        public Line(Song song) {
            boolean goThroughMeasures = true;
            measures = new ArrayList<>();
            boolean doublebar = false;
            boolean toobig = false;
            int currentMeasure = StaticStuff.lastLineMeasureNumber;
            // currentLineWidth in physical pixels
            int currentLineWidth = 0;
            // grab the width of MusicSheetView
            int screenWidth = StaticStuff.musicSheetViewDimensionsPx[0];
            // NOTE: the pixels are arranged game-like Positive x, negative y; mathematically
            int newLineWidth;
            while (goThroughMeasures) {
                // get the current measure and look at the width and then add that to the running width total
                if ((currentMeasure != song.measures.size()) && !toobig) {

                    newLineWidth = currentLineWidth + Math.round(estimateMeasureDimensions(song.measures.get(currentMeasure))[0]);
                    if (newLineWidth > screenWidth) {
                        toobig = true;
                    } else {
                        // add the measure to the list and move on
                        measures.add(new MeasureForDrawing(song.measures.get(currentMeasure)));
                        currentLineWidth = newLineWidth;
                        currentMeasure++;
                    }
                } else if ((currentMeasure != song.measures.size()) && toobig) {
                    // The line is now too big, and we can now stop and start a new one
                    doublebar = false;
                    StaticStuff.needNewLine = true;
                    goThroughMeasures = false;
                } else if ((currentMeasure == song.measures.size()) && !toobig) {
                    // the song is at its end and we need a double bar and we don't need any more lines for viewing the song.
                    doublebar = true;
                    StaticStuff.needNewLine = false;
                    goThroughMeasures = false;
                }
            }


            StaticStuff.lastLineMeasureNumber = currentMeasure;



        }

        /**
         * Estimates the height of the line in pixels
         * @return The height of the line in physical pixels
         */
        public int estimateLineHeight() {
            int maxHeight = 0;
            for (MeasureForDrawing measure : measures) {
                if (measure.staves > maxHeight) {
                    maxHeight = measure.staves;
                }
            }
            return dpToPx(
                    ((maxHeight - 1) * StaticStuff.MusicSpacing.staffSpace) + (StaticStuff.MusicSpacing.lineSpace * 4)
            );
        }

    }



    public class DrawingLogic{
        Canvas canvas;
        Paint paint;

        public DrawingLogic(Canvas canvas, Paint paint) {
            this.canvas = canvas;
            this.paint = paint;
        }

        public void drawMeasure(MeasureForDrawing measureForDrawing, int[] startingCoordinates) {
            Log.d("DrawingLogic", "Drawing measure " + measureForDrawing.measure.measureNumber);
            int numberOfStaves = measureForDrawing.staves;
            int width = ((int) estimateMeasureDimensions(measureForDrawing.measure)[0]);
            int[][] staveStartingCoordinates = new int[numberOfStaves][2];
            int stoppingX = startingCoordinates[0] + width;

            // draw the staves
            for (int i = 0; i < numberOfStaves; i++) {
                staveStartingCoordinates[i][0] = startingCoordinates[0];
                staveStartingCoordinates[i][1] = startingCoordinates[1] + (i * dpToPx(StaticStuff.MusicSpacing.staffSpace));
            }
            if ((StaticStuff.MusicSpacing.darkMode)) {
                paint.setColor(0xFFAAAAAA);
            } else {
                paint.setColor(0xFF000000);
            }
            for (int i = 0; i < numberOfStaves; i++) {
                drawStave(staveStartingCoordinates[i], stoppingX);
            }
            // draw the vertical line at the end of the measure
            int[] topRight = new int[2];
            topRight[0] = stoppingX;
            topRight[1] = startingCoordinates[1];
            Log.d("DrawingLogic", "realStoppingY: " + (startingCoordinates[1] + ((int) estimateMeasureDimensions(measureForDrawing.measure)[1])));
            drawVerticalLine(topRight, startingCoordinates[1] + ((int) estimateMeasureDimensions(measureForDrawing.measure)[1]));
            // draw the virtical line at the start of the measure
            int[] topLeft = new int[2];
            drawVerticalLine(startingCoordinates, startingCoordinates[1] + ((int) estimateMeasureDimensions(measureForDrawing.measure)[1]));
        }

        /**
         * Draws a stave on the screen
         * @param startingCoordinates The starting coordinates of the stave
         * @param endingX The ending x coordinate of the stave
         */
        public void drawStave(int[] startingCoordinates, int endingX) {
            // draw 4 lines
            for (int i = 0; i < 5; i++) {
                drawHorizontalLine(startingCoordinates, endingX);
                startingCoordinates[1] += dpToPx(StaticStuff.MusicSpacing.lineSpace);
            }
        }

        /**
         * Draws a horizontal line on the screen
         * @param startingCoordinates The starting coordinates of the line
         * @param endingX The ending x coordinate of the line
         */
        public void drawHorizontalLine(int[] startingCoordinates, int endingX) {
            Log.d("DrawingLogic", "Starting coordinates: " + startingCoordinates[0] + " x " + startingCoordinates[1]);
            canvas.drawLine(startingCoordinates[0], startingCoordinates[1], endingX, startingCoordinates[1], paint);
        }
        /**
         * Draws a vertical line on the screen
         * @param startingCoordinates The starting coordinates of the line in the form {x, y}
         * @param endingY The ending y coordinate of the line the bottom of the line
         */
        public void drawVerticalLine(int[] startingCoordinates, int endingY) {
            canvas.drawLine(startingCoordinates[0], startingCoordinates[1], startingCoordinates[0], endingY, paint);
        }

        /**
         * Changes the colour of the paint
         * @param colour takes in a value (0x[alpha][red][green][blue]) i.e. 0xFF000000 is black
         */
        public void changeColour(int colour) {
            paint.setColor(colour);
        }

    }
    /**
     * Converts dp to pixels
     * @param dp The dp value you want to convert
     * @return The physical pixels of the dp value
     */
    public int dpToPx(double dp) {
        return (int) (dp * StaticStuff.MusicSpacing.densityOfPixels);
    }
}
