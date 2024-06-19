package com.serpentech.legacyreader;

import com.serpentech.legacyreader.chooseafile.MusicEntries;

public class StaticStuff {
    public static MusicEntries.MusicEntry chosenFile;
    public static int lastLineMeasureNumber = 0;
    public static boolean needNewLine = true;
    public static class MusicSpacing {
        public static int sideMargins = 5;
        public static int noteSpace = 5;
        public static int noteWidth = 10;
        public static int lineSpace = 10;
//        public static int partSpace = 30;
        public static int staffSpace = (lineSpace * 4) + 40;
        public static double densityOfPixels;
        public static boolean darkMode;
    }
    public static int[] musicSheetViewDimensionsPx = new int[2];

}
