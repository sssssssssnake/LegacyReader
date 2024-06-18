package com.serpentech.legacyreader.musicstuff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MeasureDrawing {


    public class MeasureForDrawing {
        public MusicObjectified.Measure measure;
        // note groups represents the subdivisions of a measure in terms of where a note lands in the measure
        public int noteGroups;
        public int staves;

//        public void findNoteGroups() {
//            List<int[]> times =  new ArrayList<int[]>();
//            for (MusicObjectified.Note note : measure.notes) {
//                boolean newNoteTime = false;
//                if (Arrays.equals(note.positionFractionFromMeasureStart, new int[]{0, 0})) {
//                    measure.
//                }
//                for (int[] time : times) {
//                    if (note.positionFractionFromMeasureStart == time) {
//
//                    }
//                }
//            }
//        }
    }
    public class Line {
        List<MeasureForDrawing> measures;
        public List<MusicObjectified.StaveClef> clefs;
    }
}
