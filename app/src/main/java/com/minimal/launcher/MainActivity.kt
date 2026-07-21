package com.minimal.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = LauncherPagerAdapter(this)
        viewPager.offscreenPageLimit = 1

        promptSetDefaultLauncher()
    }

    private fun promptSetDefaultLauncher() {
        val prefs = getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean("setup_shown", false)) return
        prefs.edit().putBoolean("setup_shown", true).apply()

        if (isDefaultLauncher()) return

        AlertDialog.Builder(this)
            .setTitle("Set as Default Launcher")
            .setMessage("Would you like to set Minimal Launcher as your default home screen?")
            .setPositiveButton("Set as default") { _, _ ->
                openHomeSettings()
            }
            .setNegativeButton("Not now", null)
            .show()
    }

    private fun isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }
        val resolveInfo = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolveInfo?.activityInfo?.packageName == packageName
    }

    private fun openHomeSettings() {
        try {
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        } catch (_: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
            } catch (_: Exception) {
            }
        }
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
        when (prefs.getString(SettingsManager.KEY_THEME_MODE, SettingsManager.THEME_SYSTEM)) {
            SettingsManager.THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            SettingsManager.THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    data class AppInfo(
        val label: String,
        val icon: Drawable,
        val packageName: String,
        val className: String
    )
}
