package com.minimal.launcher

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minimal.launcher.SettingsManager.Companion.KEY_SORT_ORDER
import com.minimal.launcher.SettingsManager.Companion.SORT_INSTALL
import org.json.JSONArray

class AppListFragment : Fragment() {

    private lateinit var appRecycler: RecyclerView
    private lateinit var allApps: MutableList<MainActivity.AppInfo>
    private var textAdapter: TextAppAdapter? = null
    private lateinit var settingsManager: SettingsManager
    private var installTimes: Map<String, Long> = emptyMap()

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == KEY_SORT_ORDER) {
            sortApps()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_app_list, container, false)

        appRecycler = view.findViewById(R.id.appGrid)
        allApps = mutableListOf()
        settingsManager = SettingsManager(requireContext())
        loadAllApps()
        setupAppGrid()

        return view
    }

    override fun onResume() {
        super.onResume()
        requireContext().getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener(prefsListener)
    }

    override fun onPause() {
        super.onPause()
        requireContext().getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
            .unregisterOnSharedPreferenceChangeListener(prefsListener)
    }

    @Suppress("DEPRECATION")
    private fun loadAllApps() {
        val pm = requireContext().packageManager
        val mainActivities = pm.queryIntentActivities(
            Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            },
            PackageManager.MATCH_ALL
        )
        val seen = mutableSetOf<String>()
        val launchable = mutableListOf<MainActivity.AppInfo>()

        for (info in mainActivities) {
            if (seen.add(info.activityInfo.packageName)) {
                launchable.add(
                    MainActivity.AppInfo(
                        label = info.loadLabel(pm).toString(),
                        icon = info.loadIcon(pm),
                        packageName = info.activityInfo.packageName,
                        className = info.activityInfo.name
                    )
                )
            }
        }

        installTimes = try {
            launchable.associate { app ->
                app.packageName to pm.getPackageInfo(app.packageName, 0).firstInstallTime
            }
        } catch (_: Exception) {
            emptyMap()
        }

        allApps.clear()
        allApps.addAll(launchable)
        sortApps()
    }

    private fun sortApps() {
        val sorted = when (settingsManager.sortOrder) {
            SORT_INSTALL -> allApps.sortedByDescending { installTimes[it.packageName] ?: 0L }
            else -> allApps.sortedBy { it.label.lowercase() }
        }
        allApps.clear()
        allApps.addAll(sorted)
        textAdapter?.notifyDataSetChanged()
    }

    private fun setupAppGrid() {
        appRecycler.setHasFixedSize(true)
        appRecycler.layoutManager = LinearLayoutManager(requireContext())
        textAdapter = TextAppAdapter(
            apps = allApps,
            onClick = { app ->
                val intent = requireContext().packageManager
                    .getLaunchIntentForPackage(app.packageName)
                if (intent != null) startActivity(intent)
            },
            onLongClick = { view, app ->
                showAppMenu(view, app)
            }
        )
        appRecycler.adapter = textAdapter
    }

    private fun showAppMenu(view: View, app: MainActivity.AppInfo) {
        val popup = PopupMenu(requireContext(), view)
        popup.menu.add(0, 1, 0, "Add to Home")
        popup.menu.add(0, 2, 0, "App Info")
        popup.menu.add(0, 3, 0, "Uninstall")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                1 -> addShortcut(app)
                2 -> openAppInfo(app)
                3 -> uninstallApp(app)
            }
            true
        }
        popup.show()
    }

    private fun addShortcut(app: MainActivity.AppInfo) {
        val prefs = requireContext().getSharedPreferences(SettingsManager.PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString("shortcuts", "[]") ?: "[]"
        val arr = JSONArray(json)

        for (i in 0 until arr.length()) {
            if (arr.getString(i) == app.packageName) {
                Toast.makeText(requireContext(), "Already added", Toast.LENGTH_SHORT).show()
                return
            }
        }

        arr.put(app.packageName)
        prefs.edit().putString("shortcuts", arr.toString()).apply()
        Toast.makeText(requireContext(), "Added ${app.label}", Toast.LENGTH_SHORT).show()
    }

    private fun openAppInfo(app: MainActivity.AppInfo) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", app.packageName, null)
        }
        startActivity(intent)
    }

    @Suppress("DEPRECATION")
    private fun uninstallApp(app: MainActivity.AppInfo) {
        val pm = requireContext().packageManager
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
            data = Uri.parse("package:${app.packageName}")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (intent.resolveActivity(pm) != null) {
            startActivity(intent)
        } else {
            val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", app.packageName, null)
            }
            startActivity(settingsIntent)
            Toast.makeText(requireContext(), "Use the system menu to uninstall", Toast.LENGTH_LONG).show()
        }
    }
}
