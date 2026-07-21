package com.minimal.launcher

import android.content.Context

class SettingsManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var showUtcClock: Boolean
        get() = prefs.getBoolean(KEY_UTC_CLOCK, true)
        set(value) = prefs.edit().putBoolean(KEY_UTC_CLOCK, value).apply()

    var showDate: Boolean
        get() = prefs.getBoolean(KEY_SHOW_DATE, true)
        set(value) = prefs.edit().putBoolean(KEY_SHOW_DATE, value).apply()

    var iconColorsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ICON_COLORS, false)
        set(value) = prefs.edit().putBoolean(KEY_ICON_COLORS, value).apply()

    var themeMode: String
        get() = prefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
        set(value) = prefs.edit().putString(KEY_THEME_MODE, value).apply()

    var sortOrder: String
        get() = prefs.getString(KEY_SORT_ORDER, SORT_ALPHA) ?: SORT_ALPHA
        set(value) = prefs.edit().putString(KEY_SORT_ORDER, value).apply()

    companion object {
        const val PREFS_NAME = "launcher_prefs"
        const val KEY_UTC_CLOCK = "show_utc_clock"
        const val KEY_SHOW_DATE = "show_date"
        const val KEY_ICON_COLORS = "icon_colors"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_SORT_ORDER = "sort_order"

        const val THEME_SYSTEM = "system"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"

        const val SORT_ALPHA = "alpha"
        const val SORT_INSTALL = "install"
    }
}
