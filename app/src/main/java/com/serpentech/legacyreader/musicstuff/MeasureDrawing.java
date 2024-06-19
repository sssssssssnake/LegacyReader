package com.serpentech.legacyreader.musicstuff;

import com.serpentech.legacyreader.StaticStuff;
import com.serpentech.legacyreader.musicstuff.music.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * This class is to replace MathStuff with better classes to objectify music for the screen, not just theory
 */
public class MeasureDrawing {
    List<Song.Measure> measures;

    public MeasureDrawing(Song song) {
        measures = song.measures;
    }

    public float[] estimateMeasureDimentions(Song.Measure measure) {
        float[] dimentions = new float[2];
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

        dimentions = new float[] {width, height};
        System.gc();
        return dimentions;
    }


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
        }

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
        List<MeasureForDrawing> measures;
        public List<MusicObjectified.StaveClef> clefs;

    }
}
