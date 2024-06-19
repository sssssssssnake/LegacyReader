package com.serpentech.legacyreader.musicstuff;

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

    public MeasureDrawing(Song song) {
        measures = song.measures;
    }

    /**
     * Estimeates the dimensions of a measure in pixels
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
}
