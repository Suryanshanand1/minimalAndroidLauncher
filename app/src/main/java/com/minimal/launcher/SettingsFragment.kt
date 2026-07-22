package com.minimal.launcher

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.minimal.launcher.SettingsManager.Companion.SORT_ALPHA
import com.minimal.launcher.SettingsManager.Companion.SORT_INSTALL
import com.minimal.launcher.SettingsManager.Companion.THEME_DARK
import com.minimal.launcher.SettingsManager.Companion.THEME_LIGHT
import com.minimal.launcher.SettingsManager.Companion.THEME_SYSTEM

class SettingsFragment : Fragment() {

    private lateinit var settingsManager: SettingsManager
    private var pendingThemeRestart = false
    private var initialTheme: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        settingsManager = SettingsManager(requireContext())

        initialTheme = settingsManager.themeMode

        val themeGroup = view.findViewById<android.widget.RadioGroup>(R.id.themeGroup)
        when (settingsManager.themeMode) {
            THEME_LIGHT -> themeGroup.check(R.id.themeLight)
            THEME_DARK -> themeGroup.check(R.id.themeDark)
            else -> themeGroup.check(R.id.themeSystem)
        }
        themeGroup.setOnCheckedChangeListener { _, checkedId ->
            val (mode, themeValue) = when (checkedId) {
                R.id.themeLight -> AppCompatDelegate.MODE_NIGHT_NO to THEME_LIGHT
                R.id.themeDark -> AppCompatDelegate.MODE_NIGHT_YES to THEME_DARK
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM to THEME_SYSTEM
            }
            settingsManager.themeMode = themeValue
            AppCompatDelegate.setDefaultNightMode(mode)
            if (themeValue != initialTheme) {
                pendingThemeRestart = true
            }
        }

        val sortGroup = view.findViewById<android.widget.RadioGroup>(R.id.sortGroup)
        when (settingsManager.sortOrder) {
            SORT_INSTALL -> sortGroup.check(R.id.sortInstall)
            else -> sortGroup.check(R.id.sortAlpha)
        }
        sortGroup.setOnCheckedChangeListener { _, checkedId ->
            settingsManager.sortOrder = when (checkedId) {
                R.id.sortInstall -> SORT_INSTALL
                else -> SORT_ALPHA
            }
        }

        val utcSwitch = view.findViewById<android.widget.Switch>(R.id.utcClockSwitch)
        utcSwitch.isChecked = settingsManager.showUtcClock
        utcSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.showUtcClock = isChecked
        }

        val dateSwitch = view.findViewById<android.widget.Switch>(R.id.showDateSwitch)
        dateSwitch.isChecked = settingsManager.showDate
        dateSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.showDate = isChecked
        }

        val iconSwitch = view.findViewById<android.widget.Switch>(R.id.iconColorsSwitch)
        iconSwitch.isChecked = settingsManager.iconColorsEnabled
        iconSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsManager.iconColorsEnabled = isChecked
        }

        val timezoneSpinner = view.findViewById<android.widget.Spinner>(R.id.timezoneSpinner)
        val timezones = arrayOf(
            "UTC", "US/Eastern", "US/Central", "US/Mountain", "US/Pacific",
            "Europe/London", "Europe/Berlin", "Europe/Paris", "Europe/Moscow",
            "Asia/Dubai", "Asia/Kolkata", "Asia/Bangkok", "Asia/Shanghai",
            "Asia/Tokyo", "Asia/Seoul", "Australia/Sydney", "Pacific/Auckland"
        )
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, timezones)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timezoneSpinner.adapter = spinnerAdapter
        timezoneSpinner.setSelection(timezones.indexOf(settingsManager.timezone).coerceAtLeast(0))
        timezoneSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                settingsManager.timezone = timezones[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (pendingThemeRestart) {
            requireActivity().recreate()
        }
    }
}
