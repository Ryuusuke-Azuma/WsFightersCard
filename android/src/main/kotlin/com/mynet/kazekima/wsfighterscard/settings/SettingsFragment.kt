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
import androidx.preference.PreferenceFragmentCompat
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

        // インポート項目のクリックイベントを設定
        findPreference<Preference>("pref_import")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            filePickerLauncher.launch(arrayOf("text/*", "application/octet-stream"))
            true
        }
    }

    private fun importFile(uri: Uri) {
        val context = requireContext()
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                viewModel.importFromStream(inputStream) { count ->
                    Toast.makeText(context, "${count}件のデータをインポートしました", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "インポートに失敗しました", Toast.LENGTH_SHORT).show()
        }
    }
}
