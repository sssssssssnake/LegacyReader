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

    public MeasureDrawing(Song song, Paint paint, Canvas canvas) {
        drawingLogic = new DrawingLogic(canvas, paint);
        measures = song.measures;
        lines = new ArrayList<>();
        while (StaticStuff.needNewLine) {
            lines.add(new Line(song));
        }
        System.gc();
    }

    public void drawLines() {
        for (Line line : lines) {
            for (MeasureForDrawing measure : line.measures) {
                //TODO: draw the measures and apply fun logic, ~yay~
            }
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

        float[] dimensions = new float[] {dpToPx(width), dpToPx(height)};
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
            Log.d("MeasureDrawing", "In measure " + measure.measureNumber + " there are " + noteGroupsSize + " note groups");
        }

        /**
         * Finds the note groups (how many times a group of notes are played) in the measure and sets the noteGroupsSize and noteGroups variables
         */
        public void findNoteGroups() {
            List<int[]> times = new ArrayList<>();
            for (Song.Note note : measure.notes) {
                boolean found = false;
                if (times.isEmpty()) {
                    times.add(note.positionFromStart);
                } else {
                    for (int[] time : times) {
                        if (note.positionFromStart == time) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        times.add(note.positionFromStart);
                    }
                }
            }
            noteGroupsSize = times.size();
            noteGroups = times;
        }
    }

    /**
     * This is the class that will represent the Lines of the song as it is drawn on the screen
     */
    public class Line {
        int lineNumber;
        List<MeasureForDrawing> measures;
        public List<Song.StaveClef> clefs;


        public Line(Song song) {
            boolean doublebar = false;
            boolean toobig = false;
            int currentMeasure = StaticStuff.lastLineMeasureNumber;
            // currentLineWidth in physical pixels
            int currentLineWidth = 0;
            // grab the width of MusicSheetView
            int screenWidth = StaticStuff.musicSheetViewDimensionsPx[0];
            // NOTE: the pixels are arranged game-like Positive x, negative y; mathematically

            // get the current measure and look at the width and then add that to the running width total
            if ((currentMeasure != measures.size()) && !toobig) {
                int newLineWidth;
                newLineWidth = currentLineWidth + Math.round(estimateMeasureDimensions(song.measures.get(currentMeasure))[0]);
                if (newLineWidth > screenWidth) {
                    toobig = true;
                } else {
                    // add the measure to the list and move on
                    measures.add(new MeasureForDrawing(song.measures.get(currentMeasure)));
                    currentLineWidth = newLineWidth;
                    currentMeasure++;
                }
            } else if ((currentMeasure == measures.size()) && !toobig) {
                // the song is at its end and we need a double bar and we don't need any more lines for viewing the song.
                doublebar = true;
                StaticStuff.needNewLine = false;
            } else if ((currentMeasure != measures.size()) && toobig) {
                // The line is now too big, and we can now stop and start a new one
                doublebar = false;
                StaticStuff.needNewLine = true;
            }

            StaticStuff.lastLineMeasureNumber = currentMeasure;


        }

    }
    public int dpToPx(double dp) {
        return (int) (dp * StaticStuff.MusicSpacing.densityOfPixels);
    }

    public class DrawingLogic{
        Canvas canvas;
        Paint paint;

        public DrawingLogic(Canvas canvas, Paint paint) {
            this.canvas = canvas;
            this.paint = paint;
        }

        public void drawMeasure(MeasureForDrawing measureForDrawing, int[] startingCoordinates) {
            int numberOfStaves = measureForDrawing.staves;
            int width = ((int) estimateMeasureDimensions(measureForDrawing.measure)[0]);
            int[][] staveStartingCoordinates = new int[numberOfStaves][2];
            int stoppingX = startingCoordinates[0] + width;

            // draw the staves
            for (int i = 0; i < numberOfStaves; i++) {
                staveStartingCoordinates[i][0] = startingCoordinates[0];
                staveStartingCoordinates[i][1] = startingCoordinates[1] + (i * StaticStuff.MusicSpacing.staffSpace);
            }
            if ((StaticStuff.MusicSpacing.darkMode)) {
                paint.setColor(0xFFAAAAAA);
            } else {
                paint.setColor(0xFF000000);
            }
            for (int i = 0; i < numberOfStaves; i++) {
                drawStave(staveStartingCoordinates[i], stoppingX);
            }
            // draw the virtical line at the end of the measure

            int[] topRight = new int[2];
            topRight[0] = stoppingX;
            topRight[1] = startingCoordinates[1];
            drawVerticalLine(topRight, startingCoordinates[1] + ((numberOfStaves -1) * StaticStuff.MusicSpacing.staffSpace) + (StaticStuff.MusicSpacing.lineSpace * 4));

        }

        /**
         * Draws a stave on the screen
         * @param startingCoordinates The starting coordinates of the stave
         * @param endingX The ending x coordinate of the stave
         */
        public void drawStave(int[] startingCoordinates, int endingX) {
            // draw 4 lines
            for (int i = 0; i < 4; i++) {
                drawHorizontalLine(startingCoordinates, endingX);
                startingCoordinates[1] += StaticStuff.MusicSpacing.lineSpace;
            }
        }

        /**
         * Draws a horizontal line on the screen
         * @param startingCoordinates The starting coordinates of the line
         * @param endingX The ending x coordinate of the line
         */
        public void drawHorizontalLine(int[] startingCoordinates, int endingX) {
            canvas.drawLine(startingCoordinates[0], startingCoordinates[1], endingX, startingCoordinates[1], paint);
        }
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
}
