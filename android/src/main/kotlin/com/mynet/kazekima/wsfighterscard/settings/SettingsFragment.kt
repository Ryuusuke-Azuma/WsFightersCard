/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.settings

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.mynet.kazekima.wsfighterscard.BuildConfig
import com.mynet.kazekima.wsfighterscard.R

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { importFile(it) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        setupDataSettings()
        setupDebugSettings()
    }

    private fun setupDataSettings() {
        findPreference<Preference>("pref_import")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val debugMode = findPreference<SwitchPreferenceCompat>("pref_debug_mode")?.isChecked ?: false
            if (debugMode) {
                importSampleFromAssets()
            } else {
                filePickerLauncher.launch(arrayOf("text/*", "application/octet-stream"))
            }
            true
        }
    }

    private fun setupDebugSettings() {
        val debugPref = findPreference<SwitchPreferenceCompat>("pref_debug_mode")
        val debugCategory = findPreference<PreferenceCategory>("cat_debug")

        if (!BuildConfig.DEBUG) {
            debugCategory?.let { preferenceScreen.removePreference(it) }
        } else {
            if (debugPref?.sharedPreferences?.contains("pref_debug_mode") == false) {
                debugPref.isChecked = true
            }
        }
    }

    private fun importSampleFromAssets() {
        try {
            val inputStream = requireContext().assets.open("sample_import.csv")
            viewModel.importFromStream(inputStream) { count ->
                Toast.makeText(requireContext(), "[DEBUG] Assetsから${count}件インポートしました", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "サンプルファイルの読み込みに失敗しました", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importFile(uri: Uri) {
        val context = requireContext()
        try {
            context.contentResolver.openInputStream(uri)?.let { inputStream ->
                viewModel.importFromStream(inputStream) { count ->
                    Toast.makeText(context, "${count}件のデータをインポートしました", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "インポートに失敗しました", Toast.LENGTH_SHORT).show()
        }
    }
}
