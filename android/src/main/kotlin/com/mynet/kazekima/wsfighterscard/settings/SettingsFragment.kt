/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.mynet.kazekima.wsfighterscard.R
import java.io.InputStream
import java.io.OutputStream

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()

    private val importLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri -> handleImportUri(uri) }
        }
    }

    private val exportLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        uri?.let { handleExportUri(it) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<Preference>("pref_import")?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "text/*"
                addCategory(Intent.CATEGORY_OPENABLE)
            }
            importLauncher.launch(intent)
            true
        }

        findPreference<Preference>("pref_export")?.setOnPreferenceClickListener {
            // ここで指定する文字列がデフォルトのファイル名になります
            exportLauncher.launch("ws_fighters_card_export.csv")
            true
        }
    }

    private fun handleImportUri(uri: Uri) {
        runCatching {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            if (inputStream != null) {
                viewModel.importFromStream(inputStream) { count ->
                    Toast.makeText(requireContext(), "Imported $count games!", Toast.LENGTH_SHORT).show()
                }
            }
        }.onFailure {
            Toast.makeText(requireContext(), "Import failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleExportUri(uri: Uri) {
        runCatching {
            val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                viewModel.exportToStream(outputStream) {
                    Toast.makeText(requireContext(), "Exported successfully!", Toast.LENGTH_SHORT).show()
                }
            }
        }.onFailure {
            Toast.makeText(requireContext(), "Export failed: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
