package com.serpentech.legacyreader.chooseafile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class MusicEntries {

    /**
     * An array of sample (chooseafile) items.
     */
    public static final List<MusicEntry> ITEMS = new ArrayList<MusicEntry>();

    /**
     * A map of sample (chooseafile) items, by ID.
     */
    public static final Map<String, MusicEntry> ITEM_MAP = new HashMap<String, MusicEntry>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createPlaceholderItem(i));
        }
    }

    private static void addItem(MusicEntry item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static MusicEntry createPlaceholderItem(int position) {
        return new MusicEntry(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A chooseafile item representing a piece of content.
     */
    public static class MusicEntry {
        public String id;
        public String filename;
        public String filepath;

        public MusicEntry(String id, String filename, String filepath) {
            this.id = id;
            this.filename = filename;
            this.filepath = filepath;
        }

        public String getId() {
            return id;
        }

        public String getFilename() {
            return filename;
        }

        public String getFilepath() {
            return filepath;
        }

        @Override
        public String toString() {
            return filename; // Display the filename in your list
        }
    }

}