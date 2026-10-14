// HighScoreStore.java
// Keeps a local ranking of the best scores using SharedPreferences.
// [BONUS] item 9 "Ranking Local (Highscores)".
package com.pedrou.cannongame;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HighScoreStore {

    private static final String PREFS_NAME = "cannon_game_highscores";
    private static final String KEY_ENTRIES = "entries";
    private static final String FIELD_SEPARATOR = ";";
    private static final String ENTRY_SEPARATOR = "\\|";
    private static final int MAX_ENTRIES = 5;

    // a single line of the ranking
    public static class Entry implements Comparable<Entry> {
        public final int score;
        public final int level;
        public final long timestamp;

        Entry(int score, int level, long timestamp) {
            this.score = score;
            this.level = level;
            this.timestamp = timestamp;
        }

        @Override
        public int compareTo(Entry other) {
            // highest score first
            return Integer.compare(other.score, score);
        }
    }

    private final SharedPreferences preferences;

    public HighScoreStore(Context context) {
        preferences = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // returns the best score ever recorded
    public int getHighScore() {
        List<Entry> entries = getEntries();
        return entries.isEmpty() ? 0 : entries.get(0).score;
    }

    // true if "score" would beat the current best score
    public boolean isHighScore(int score) {
        return score > 0 && score > getHighScore();
    }

    // adds a score to the ranking, keeping only the best MAX_ENTRIES
    public void add(int score, int level) {
        if (score <= 0) {
            return;
        }

        List<Entry> entries = getEntries();
        entries.add(new Entry(score, level, System.currentTimeMillis()));
        Collections.sort(entries);

        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }

        save(entries);
    }

    // returns the ranking, best score first
    public List<Entry> getEntries() {
        List<Entry> entries = new ArrayList<>();
        String stored = preferences.getString(KEY_ENTRIES, "");

        if (stored == null || stored.isEmpty()) {
            return entries;
        }

        String[] parts = stored.split(ENTRY_SEPARATOR);
        for (String part : parts) {
            String[] fields = part.split(FIELD_SEPARATOR);
            if (fields.length == 3) {
                try {
                    entries.add(new Entry(Integer.parseInt(fields[0]),
                            Integer.parseInt(fields[1]),
                            Long.parseLong(fields[2])));
                } catch (NumberFormatException ignored) {
                    // skip corrupted entries instead of crashing the game
                }
            }
        }

        Collections.sort(entries);
        return entries;
    }

    // returns the ranking as a readable multi-line String for the dialog
    public String getRankingText() {
        List<Entry> entries = getEntries();
        if (entries.isEmpty()) {
            return "-";
        }

        SimpleDateFormat format =
                new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            if (i > 0) {
                builder.append('\n');
            }
            builder.append(i + 1).append(". ")
                    .append(entry.score).append("  ")
                    .append("Nv ").append(entry.level).append("  ")
                    .append(format.format(new Date(entry.timestamp)));
        }
        return builder.toString();
    }

    private void save(List<Entry> entries) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < entries.size(); i++) {
            Entry entry = entries.get(i);
            if (i > 0) {
                builder.append('|');
            }
            builder.append(entry.score).append(FIELD_SEPARATOR)
                    .append(entry.level).append(FIELD_SEPARATOR)
                    .append(entry.timestamp);
        }

        preferences.edit().putString(KEY_ENTRIES, builder.toString()).apply();
    }
}
