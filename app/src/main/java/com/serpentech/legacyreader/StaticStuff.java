package com.serpentech.legacyreader;

import android.content.Context;
import android.content.res.Configuration;

import com.serpentech.legacyreader.chooseafile.MusicEntries;
import com.serpentech.legacyreader.musicstuff.MusicSheetView;

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
    public static boolean isDarkTheme(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                // Dark theme is active
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
                // Dark theme is not active
                return false;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // We're not sure what the theme is, proceed with default
                return false;
        }
        return false; // Default to non-dark mode
    }

}
