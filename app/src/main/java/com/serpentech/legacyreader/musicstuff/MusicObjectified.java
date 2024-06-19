package com.serpentech.legacyreader.musicstuff;

import com.serpentech.legacyreader.filemanagement.xmlmanage.XmlGrab;

import java.util.ArrayList;
import java.util.List;

public class MusicObjectified {
    List<Measure> measures;

    public enum Clef {
        treble,
        bass,
        alto,
        tenor,
        none
    }

    public class Measure {
        public boolean hasAttributes;
        public int divisions;
        public int staves;
        public int measureNum;
        public int measureLength;
        public int[] measureType;
        public List<StaveClef> staveClefs;

        public SimpleNote key;
        public List<Note> notes;

        public Measure(int measureNum, int measureLength, int[] measureType) {
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

        public Measure(String xmlMeasureContent) {
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

            // add clefs
            if (xmlMeasureContent.contains("clef")) {
                // grab the headers for the stave clef
                List<XmlGrab.XmlGroup> clef = xmlGrab.scanXMLForKeywordList(xmlMeasureContent, "clef");

                for (XmlGrab.XmlGroup clefGroup : clef) {
                    int staveNum = 0;
                    Clef clefType;
                    // look at headers for stave number
                    List<String[]> clefHeaders = xmlGrab.grabHeaders(clefGroup.contents, "clef");
                    for (String[] header : clefHeaders) {
                        if (header[0].equals("number")) {
                            staveNum = Integer.parseInt(header[1]);
                        }
                    }
                    // look at contents for clef type
                    String clefString = xmlGrab.grabContents(clefGroup.contents, "sign");
                    int lineNum = Integer.parseInt(xmlGrab.grabContents(clefGroup.contents, "line"));

                    switch (clefString.charAt(0)) {
                        case 'T':
                            clefType = Clef.treble;
                            break;
                        case 'B':
                            clefType = Clef.bass;
                            break;
                        case 'C':
                            if (lineNum == 3) {
                                clefType = Clef.alto;
                            } else {
                                clefType = Clef.tenor;
                            }
                            break;
                        default:
                            clefType = Clef.none;
                            break;
                    }
                    this.staveClefs.add(new StaveClef(clefType, staveNum, lineNum));
                }
            }

            // add the notes
            List<XmlGrab.XmlGroup> notes = xmlGrab.scanXMLForKeywordList(xmlMeasureContent, "note");
            List<Note> noteList = new ArrayList<>();
            if (notes.size() > 0) {
                for (XmlGrab.XmlGroup note : notes) {

                    if (note.contents.contains("pitch") && note.contents.contains("alter")) {
                        noteList.add(new Note(
                                new int[]{0, 0},
                                new SimpleNote(
                                        xmlGrab.grabContents(note.contents, "step").charAt(0),
                                        Integer.parseInt(xmlGrab.grabContents(note.contents, "octave")),
                                        Double.parseDouble(xmlGrab.grabContents(note.contents, "alter"))),
                                new int[]{0, 0},
                                0)
                        );
                    } else if (note.contents.contains("pitch")) {
                        noteList.add(new Note(
                                new int[]{0, 0},
                                new SimpleNote(
                                        xmlGrab.grabContents(note.contents, "step").charAt(0),
                                        Integer.parseInt(xmlGrab.grabContents(note.contents, "octave")),
                                        0),
                                new int[]{0, 0},
                                Integer.parseInt(xmlGrab.grabContents(note.contents, "staff")))
                        );
                    }
                }
            }


            this.measureNum = measureNum;
            this.measureLength = measureLength;
            this.measureType = measureType;
            this.notes = noteList;

            System.gc();
        }

        public void addNote(Note note) {
            this.notes.add(note);
        }

        public void addKey(SimpleNote key) {
            this.key = key;
        }
    }

    public class Note {
        public int[] lengthFraction;
        public int[] positionFractionFromMeasureStart;
        SimpleNote pitch;
        int staveNumber;

        public Note(int[] lengthFraction, SimpleNote pitch, int[] positionFractionFromMeasureStart, int staveNumber) {
            this.lengthFraction = lengthFraction;
            this.pitch = pitch;
            this.positionFractionFromMeasureStart = positionFractionFromMeasureStart;
            this.staveNumber = staveNumber;
        }


    }

    public class SimpleNote {
        public char letterName;
        public int octave;
        public double alter;

        public SimpleNote(char letterName, int octave, double alter) {
            this.letterName = letterName;
            this.octave = octave;
            this.alter = alter;
        }


    }

    public class StaveClef {
        public Clef clef;
        public int staveNumber;
        public int lineNumber;

        public StaveClef(Clef clef, int staveNumber, int lineNumber) {
            this.clef = clef;
            this.staveNumber = staveNumber;
            this.lineNumber = lineNumber;
        }

    }


}
