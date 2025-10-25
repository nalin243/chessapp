package com.example.chessapp;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeHelper {

    private static final String PREF_NAME = "theme_pref";
    private static final String KEY_THEME = "selected_theme";

    /**
     * Apply the saved theme before setContentView() is called.
     */
    public static void applyTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String theme = prefs.getString(KEY_THEME, "mystic"); // Default theme

        switch (theme.toLowerCase()) {
            case "amber":
                context.setTheme(R.style.AppTheme_Amber);
                break;

            case "mystic":
            default:
                context.setTheme(R.style.AppTheme_Mystic);
                break;
        }
    }

    /**
     * Save selected theme name into SharedPreferences.
     */
    public static void setTheme(Context context, String theme) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_THEME, theme)
                .apply();
    }

    /**
     * Retrieve current theme name (for UI components like toggle switch).
     */
    public static String getCurrentTheme(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getString(KEY_THEME, "mystic");
    }
}
