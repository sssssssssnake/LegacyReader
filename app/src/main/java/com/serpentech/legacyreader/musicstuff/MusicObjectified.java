package com.serpentech.legacyreader.musicstuff;

import com.serpentech.legacyreader.filemanagement.xmlmanage.XmlGrab;

import java.util.ArrayList;
import java.util.List;

public class MusicObjectified {
    public class Measure {
        public boolean hasAttributes;
        public int divisions;
        public int staves;
        public int measureNum;
        public int measureLength;
        public int[] measureType;

        public SimpleNote key;
        public List<Note> notes;

        public Measure (int measureNum, int measureLength, int[] measureType) {
            this.measureNum = measureNum;
            this.measureLength = measureLength;
            this.measureType = measureType;
            this.notes = new ArrayList<>();

        }
        public Measure(int measureNum, int measureLength, int[] measureType, List<Note> notes, int staves) {
            this.measureNum = measureNum;
            this.measureLength = measureLength;
            this.measureType = measureType;
            this.notes = notes;
            this.staves = staves;
        }

        public void addNote (Note note) {
            this.notes.add(note);
        }
        public void addKey (SimpleNote key) {
            this.key = key;
        }
        public Measure (String xmlMeasureContent) {
            int measureNum = 0;

            int measureLength;
            int[] measureType;
            XmlGrab xmlGrab = new XmlGrab();

            // we need to grab the contents from an xml parcel
            List<String[]> measureHeaders = xmlGrab.grabHeaders(xmlMeasureContent, "measure");
            for (String[] header : measureHeaders) {
                if (header[0].equals("number")) {
                    measureNum = Integer.parseInt(header[1]);
                }
            }
            String divisonsString = null;
            if (xmlMeasureContent.contains("divisions")) {
                divisonsString = xmlGrab.grabContents(xmlMeasureContent, "divisions");
                divisions = Integer.parseInt(divisonsString);
            } else {
                divisions = 0;
            }

            String measureLengthString;
            if (xmlMeasureContent.contains("beats")) {
                measureLengthString = xmlGrab.grabContents(xmlMeasureContent, "beats");
                measureLength = Integer.parseInt(measureLengthString);
            } else {
                measureLength = 0;
            }

            String measureTypeString;
            if (xmlMeasureContent.contains("beat-type")) {
                measureTypeString = xmlGrab.grabContents(xmlMeasureContent, "beat-type");
                measureType = new int[]{measureLength, Integer.parseInt(measureTypeString)};
            } else {
                measureType = new int[]{0, 0};
            }

            this.measureNum = measureNum;
            this.measureLength = measureLength;
            this.measureType = measureType;
            this.notes = new ArrayList<>();

            System.gc();
        }
    }

    public class Note{
        public int [] lengthFraction;
        SimpleNote pitch;
        public int positionFractionFromMeasureStart;
        int staveNumber;

        public Note (int[] lengthFraction, SimpleNote pitch, int positionFractionFromMeasureStart, int staveNumber) {
            this.lengthFraction = lengthFraction;
            this.pitch = pitch;
            this.positionFractionFromMeasureStart = positionFractionFromMeasureStart;
            this.staveNumber = staveNumber;
        }




    }
    public class SimpleNote {
        public char letterName;
        public int octave;
        public float alter;

        public SimpleNote (char letterName, int octave, int alter) {
            this.letterName = letterName;
            this.octave = octave;
            this.alter = alter;
        }


    }




}
