package com.serpentech.legacyreader.musicstuff;

import com.serpentech.legacyreader.musicstuff.music.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MeasureDrawing {


    public class MeasureForDrawing {
        public Song.Measure measure;
        // note groups represents the subdivisions of a measure in terms of where a note lands in the measure
        public int noteGroups;
        public int staves;

        public void findNoteGroups() {
            List<int[]> times =  new ArrayList<int[]>();
            for (Song.Note note : measure.notes) {
                boolean newNoteTime = false;
                if (times.size() == 0) {
                    times.add(note.positionFromStart);
                }
            }
        }
    }
    public class Line {
        List<MeasureForDrawing> measures;
        public List<MusicObjectified.StaveClef> clefs;
    }
}
