package com.minimal.launcher

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minimal.launcher.SettingsManager.Companion.KEY_ICON_COLORS
import com.minimal.launcher.SettingsManager.Companion.KEY_SHOW_DATE
import com.minimal.launcher.SettingsManager.Companion.KEY_UTC_CLOCK
import org.json.JSONArray

class HomeFragment : Fragment() {

    private lateinit var shortcutsRecycler: RecyclerView
    private lateinit var shortcutApps: MutableList<MainActivity.AppInfo>
    private var shortcutAdapter: AppAdapter? = null
    private lateinit var dateText: TextView
    private var utcClock: View? = null
    private var gmtLabel: View? = null
    private lateinit var settingsManager: SettingsManager

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            KEY_UTC_CLOCK -> applyUtcClockVisibility()
            KEY_SHOW_DATE -> applyDateVisibility()
            KEY_ICON_COLORS -> applyIconColors()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        dateText = view.findViewById(R.id.dateText)
        dateText.text = java.text.SimpleDateFormat("EEEE, MMMM d", java.util.Locale.getDefault())
            .format(java.util.Date())

        utcClock = view.findViewById(R.id.utcClock)
        gmtLabel = view.findViewById(R.id.gmtLabel)
        settingsManager = SettingsManager(requireContext())

        shortcutsRecycler = view.findViewById(R.id.shortcutsGrid)
        shortcutApps = mutableListOf()
        setupShortcutsGrid()

        view.findViewById<View>(R.id.settingsButton).setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.settingsContainer, SettingsFragment())
                .addToBackStack("settings")
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadShortcuts()
        applySettings()
        requireContext().getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onPause() {
        super.onPause()
        requireContext().getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
            .unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    private fun applySettings() {
        applyUtcClockVisibility()
        applyDateVisibility()
        applyIconColors()
    }

    private fun applyUtcClockVisibility() {
        val visible = if (settingsManager.showUtcClock) View.VISIBLE else View.GONE
        utcClock?.visibility = visible
        gmtLabel?.visibility = visible
    }

    private fun applyDateVisibility() {
        dateText.visibility = if (settingsManager.showDate) View.VISIBLE else View.GONE
    }

    private fun applyIconColors() {
        shortcutAdapter?.iconColorsEnabled = settingsManager.iconColorsEnabled
        shortcutAdapter?.notifyDataSetChanged()
    }

    private fun setupShortcutsGrid() {
        val displayMetrics = resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        val targetWidthDp = 90f
        val columns = maxOf(3, (screenWidthDp / targetWidthDp).toInt())

        val iconSizeDp = minOf(maxOf(48f, screenWidthDp / columns * 0.5f), 64f)
        val iconSizePx = android.util.TypedValue.applyDimension(
            android.util.TypedValue.COMPLEX_UNIT_DIP, iconSizeDp, displayMetrics
        ).toInt()

        shortcutsRecycler.layoutManager = GridLayoutManager(requireContext(), columns)
        shortcutAdapter = AppAdapter(
            context = requireContext(),
            apps = shortcutApps,
            iconSizePx = iconSizePx,
            iconColorsEnabled = settingsManager.iconColorsEnabled,
            onLongClick = { _, app ->
                removeShortcut(app)
                true
            }
        )
        shortcutsRecycler.adapter = shortcutAdapter
    }

    private fun loadShortcuts() {
        val prefs = requireContext().getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("shortcuts", "[]") ?: "[]"
        val packageNames = mutableListOf<String>()
        val arr = JSONArray(json)
        for (i in 0 until arr.length()) {
            packageNames.add(arr.getString(i))
        }

        shortcutApps.clear()
        val pm = requireContext().packageManager
        for (pkg in packageNames) {
            try {
                val info = pm.getApplicationInfo(pkg, 0)
                shortcutApps.add(
                    MainActivity.AppInfo(
                        label = pm.getApplicationLabel(info).toString(),
                        icon = pm.getApplicationIcon(info),
                        packageName = pkg,
                        className = ""
                    )
                )
            } catch (_: PackageManager.NameNotFoundException) {
            }
        }
        shortcutAdapter?.notifyDataSetChanged()
    }

    private fun removeShortcut(app: MainActivity.AppInfo) {
        val prefs = requireContext().getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("shortcuts", "[]") ?: "[]"
        val arr = JSONArray(json)
        val newArr = JSONArray()
        for (i in 0 until arr.length()) {
            if (arr.getString(i) != app.packageName) {
                newArr.put(arr.getString(i))
            }
        }
        prefs.edit().putString("shortcuts", newArr.toString()).apply()
        loadShortcuts()
        Toast.makeText(requireContext(), "Removed ${app.label}", Toast.LENGTH_SHORT).show()
    }
}
