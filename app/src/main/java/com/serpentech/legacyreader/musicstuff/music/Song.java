package com.serpentech.legacyreader.musicstuff.music;


import android.util.Log;

import com.serpentech.legacyreader.filemanagement.xmlmanage.XmlGrab;

import java.util.ArrayList;
import java.util.List;

/** Song.java
 * This class represents a song. It contains a list of measures, which contain a list of notes.
 * It is specifically for the purpose of representing a song in the context of a music notation program.
 * It is also designed according to the MusicXML format.
 *
 */
public class Song {
    public List<Measure> measures;

    public Song(String xmlContent) {
        XmlGrab xmlGrab = new XmlGrab();
        List<XmlGrab.XmlGroup> measureContents = xmlGrab.scanXMLForKeywordList(xmlContent, "measure");
        Log.d("Song", "Number of measures: " + measureContents.size());
        measures = new ArrayList<Measure>();
        measureContents.forEach(measureContent -> measures.add(new Measure(measureContent.contents, xmlGrab)));

        // Make sure that all the measures have attributes
        int counter = 0;
        while (counter < measures.size()) {
            if (measures.get(counter).hasAttributes) {
                counter++;
            } else {
                measures.get(counter).updateMeasureAttributes(measures.get(counter - 1).divisions, measures.get(counter - 1).staves, measures.get(counter - 1).timeSignature, measures.get(counter - 1).customKeySignature, measures.get(counter - 1).keySignature, measures.get(counter - 1).customKeySignatureList);
                ++counter;
            }
        }
        // TODO: update the measure attributes it doesn't work
        Log.d("Song", "Updated measure attributes");

        // update the notes length
        for (Measure measure : measures) {
            measure.updateNotesLength();
        }
        Log.d("Song", "Updated notes length");
    }

    public Pitch circleOfFiths(int index) {
        Pitch pitchToBeReturned;
        switch (index) {
            case 0:
                pitchToBeReturned = new Pitch('C', 0);
                break;
            case 1:
                pitchToBeReturned = new Pitch('G', 0);
                break;
            case 2:
                pitchToBeReturned = new Pitch('D', 0);
                break;
            case 3:
                pitchToBeReturned = new Pitch('A', 0);
                break;
            case 4:
                pitchToBeReturned = new Pitch('E', 0);
                break;
            case 5:
                pitchToBeReturned = new Pitch('B', 0);
                break;
            case 6:
                pitchToBeReturned = new Pitch('F', 1);
                break;
            case 7:
                pitchToBeReturned = new Pitch('C', 1);
                break;
            case -1:
                pitchToBeReturned = new Pitch('F', 0);
                break;
            case -2:
                pitchToBeReturned = new Pitch('B', -1);
                break;
            case -3:
                pitchToBeReturned = new Pitch('E', -1);
                break;
            case -4:
                pitchToBeReturned = new Pitch('A', -1);
                break;
            case -5:
                pitchToBeReturned = new Pitch('D', -1);
                break;
            case -6:
                pitchToBeReturned = new Pitch('G', -1);
                break;
            case -7:
                pitchToBeReturned = new Pitch('C', -1);
                break;

            default:
                pitchToBeReturned = null;
                break;

        }
        return pitchToBeReturned;
    }


    public enum NoteType {
        PITCHED,
        UNPITCHED,
        REST
    }

    public class Measure {
        public boolean hasAttributes;
        public int divisions;
        public int staves;
        public int measureNumber;
        public int[] timeSignature;
        public boolean customKeySignature;
        public BasicNote keySignature;
        public BasicNote[] customKeySignatureList;
        public List<Note> notes;
        public List<StaveClef> staveClefs = new ArrayList<StaveClef>();
        public int totalLength;


        public Measure(String xmlContent, XmlGrab xmlGrab) {
            this.notes = new ArrayList<Note>();

            // look for arttributes
            // if there are attributes, get the divisions, staves, time signature, and key signature
            if (xmlContent.contains("<attributes")) {

                this.hasAttributes = true;
                if (xmlContent.contains("divisions")) {
                    this.divisions = Integer.parseInt(xmlGrab.grabContents(xmlContent, "divisions"));
                } else {
                    this.divisions = 0;
                }
                if (xmlContent.contains("clef")) {
                    List<XmlGrab.XmlGroup> clefs = xmlGrab.scanXMLForKeywordList(xmlContent, "clef");
                    this.staves = clefs.size();
                    for (XmlGrab.XmlGroup clef : clefs) {
                        int staveNum = 0;
                        Clef clefType;
                        // look at headers for stave number
                        List<String[]> clefHeaders = xmlGrab.grabHeaders(clef.contents, "clef");
                        for (String[] header : clefHeaders) {
                            if (header[0].equals("number")) {
                                staveNum = Integer.parseInt(header[1]);
                            }
                        }
                        // look at contents for clef type
                        String clefString = xmlGrab.grabContents(clef.contents, "sign");
                        int lineNum = Integer.parseInt(xmlGrab.grabContents(clef.contents, "line"));

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
                } else {
                    this.staves = 0;
                }
                List<String[]> measureAttributes = xmlGrab.grabHeaders(xmlContent, "measure");
                for (String[] attribute : measureAttributes) {
                    if (attribute[0].equals("number")) {
                        this.measureNumber = Integer.parseInt(attribute[1]);
                    }
                }
                if (xmlContent.contains("time")) {
                    this.timeSignature = new int[2];
                    this.timeSignature[0] = Integer.parseInt(xmlGrab.grabContents(xmlContent, "beats"));
                    this.timeSignature[1] = Integer.parseInt(xmlGrab.grabContents(xmlContent, "beat-type"));
                } else {
                    this.timeSignature = new int[2];
                }

                if (xmlContent.contains("key")) {
                    String keyContent = xmlGrab.grabContents(xmlContent, "key");
                    // initailize the pitch object

                    Pitch pitch = new Pitch('C', 0);
                    if (xmlContent.contains("fifths")) {
                        pitch.letterName = circleOfFiths(Integer.parseInt(xmlGrab.grabContents(keyContent, "fifths"))).letterName;
                        pitch.alter = circleOfFiths(Integer.parseInt(xmlGrab.grabContents(keyContent, "fifths"))).alter;
                        customKeySignature = false;
                        keySignature = new BasicNote(pitch.letterName, 0, pitch.alter);
                    } else {
                        customKeySignature = true;
                        List<XmlGrab.XmlGroup> keyList = xmlGrab.scanXMLForKeywordList(keyContent, "key-step");
                        List<XmlGrab.XmlGroup> keyAlterList = xmlGrab.scanXMLForKeywordList(keyContent, "key-alter");
                        customKeySignatureList = new BasicNote[keyList.size()];
                        for (int i = 0; i < keyList.size(); i++) {
                            pitch.letterName = keyList.get(i).contents.charAt(0);
                            pitch.alter = Float.parseFloat(keyAlterList.get(i).contents);
                            customKeySignatureList[i] = new BasicNote(pitch.letterName, 0, pitch.alter);
                        }
                    }


                }
                System.gc();
            } else {
                this.hasAttributes = false;
            }

            // get the notes in the measure
            List<XmlGrab.XmlGroup> noteContents = xmlGrab.scanXMLForKeywordList(xmlContent, "note");
            for (XmlGrab.XmlGroup noteContent : noteContents) {
                notes.add(new Note(noteContent.contents, xmlGrab));
            }
            // Organize the notes by voice and update the note placement
            List<Integer> voices = new ArrayList<Integer>();
            for (Note note : notes) {
                if (!voices.contains(note.voice)) {
                    voices.add(note.voice);
                }
            }
            int[] voiceLengths = new int[voices.size()];
            for (int i = 0; i < voices.size(); i++) {
                voiceLengths[i] = 0;
            }
            for (Note note : notes) {
                note.positionFromStart = new int[2];
                note.positionFromStart[0] = voiceLengths[voices.indexOf(note.voice)];
                voiceLengths[voices.indexOf(note.voice)] += note.lengthFraction[0];
            }
            // see if the divisions are defined
            if (this.hasAttributes && this.divisions != 0) {
                for (Note note : notes) {
                    note.lengthFraction[1] = this.divisions;
                    note.positionFromStart[1] = this.divisions;
                }
            }
            // End of the Measure constructor
        }

        public void updateNotesLength() {
            for (Note note : notes) {
                note.lengthFraction[1] = this.divisions;
                note.positionFromStart[1] = this.divisions;
            }
        }

        public void updateMeasureAttributes(int divisions, int staves, int[] timeSignature, boolean customKeySignature, BasicNote keySignature, BasicNote[] customKeySignatureList) {
            this.divisions = divisions;
            this.staves = staves;
            this.timeSignature = timeSignature;
            this.customKeySignature = customKeySignature;
            this.keySignature = keySignature;
            this.customKeySignatureList = customKeySignatureList;
        }

    }

    public class Note {
        public boolean isChord;
        public NoteType noteType;
        public int voice;
        public int[] lengthFraction;
        public BasicNote pitch;
        public int staveNumber;
        public int[] positionFromStart;

        public Note(NoteType noteType, int voice, int[] lengthFraction, BasicNote pitch, int staveNumber, int[] positionFromStart) {
            this.noteType = noteType;
            this.voice = voice;
            this.lengthFraction = lengthFraction;
            this.pitch = pitch;
            this.staveNumber = staveNumber;
            this.positionFromStart = positionFromStart;
        }

        public Note(String xmlContent, XmlGrab xmlGrab) {
            // see if the note has is pitched, unpitched, or a rest
            // Account for the tags that could be in the pitch tags
            if (xmlContent.contains("<rest")) {
                this.noteType = NoteType.REST;
            } else if (xmlContent.contains("<pitch")) {
                this.noteType = NoteType.PITCHED;
            } else if (xmlContent.contains("<unpitched")) {
                this.noteType = NoteType.UNPITCHED;
            } else {
                throw new IllegalArgumentException("Note type not recognized");
            }
            // see if the note is a is a chord
            this.isChord = xmlContent.contains("<chord");

            // if the note is pitched, get the pitch
            this.pitch = new BasicNote('C', 0, 0);
            if (this.noteType == NoteType.PITCHED) {
                this.pitch.letterName = xmlGrab.grabContents(xmlContent, "step").charAt(0);
                this.pitch.octave = Integer.parseInt(xmlGrab.grabContents(xmlContent, "octave"));
                if (xmlContent.contains("<alter>")) {
                    this.pitch.alter = Float.parseFloat(xmlGrab.grabContents(xmlContent, "alter"));
                } else {
                    this.pitch.alter = 0;
                }
            } else {
                this.pitch = null;
            }

            // get the voice of the note and stave number
            this.voice = Integer.parseInt(xmlGrab.grabContents(xmlContent, "voice"));
            this.staveNumber = Integer.parseInt(xmlGrab.grabContents(xmlContent, "staff"));

            this.lengthFraction = new int[2];
            this.lengthFraction[0] = Integer.parseInt(xmlGrab.grabContents(xmlContent, "duration"));

            // position from start will be calculated later
            this.positionFromStart = new int[2];

            System.gc();
        }


    }

    public class BasicNote {
        char letterName;
        int octave;
        float alter;

        public BasicNote(char letterName, int octave, float alter) {
            this.letterName = letterName;
            this.octave = octave;
            this.alter = alter;
        }
    }

    public class Pitch {
        char letterName;
        float alter;

        public Pitch(char letterName, float alter) {
            this.letterName = letterName;
            this.alter = alter;
        }
    }

    public enum Clef {
        treble,
        bass,
        alto,
        tenor,
        none
    }

    public class StaveClef {
        public Clef clef;
        public int staveNumber;
        public int lineNumber;

        public StaveClef (Clef clef, int staveNumber, int lineNumber) {
            this.clef = clef;
            this.staveNumber = staveNumber;
            this.lineNumber = lineNumber;
        }

    }


}