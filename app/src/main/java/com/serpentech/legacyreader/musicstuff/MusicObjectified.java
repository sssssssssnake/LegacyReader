package com.serpentech.legacyreader.musicstuff;

import java.util.ArrayList;
import java.util.List;

public class MusicObjectified {
    public class Measure {
        public int staves;
        public int measureNum;
        public int measureLength;
        public int[] measureType;

        public List<SimpleNote> key;
        public List<Note> notes;

        public Measure (int measureNum, int measureLength, int[] measureType) {
            this.measureNum = measureNum;
            this.measureLength = measureLength;
            this.measureType = measureType;
            this.notes = new ArrayList<>();

        }

        public void addNote (Note note) {
            this.notes.add(note);
        }
    }

    public class Note{
        public int [] lengthFraction;
        public char letterName;
        public float alter;
        public int octave;
        public int positionFractionFromMeasureStart;
        public int centOffset;






    }
    public class SimpleNote {
        public char letterName;
        public int octave;
        public int sharpCount;
        public int flatCount;

        public SimpleNote (char letterName, int octave, int sharp, int flat) {
            this.letterName = letterName;
            this.octave = octave;
            this.sharpCount = sharp;
            this.flatCount = flat;
        }

    }

}
