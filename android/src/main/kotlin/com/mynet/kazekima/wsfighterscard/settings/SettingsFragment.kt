/*
 * Copyright (c) 2026 Ryuusuke Azuma All Rights Reserved.
 */

package com.mynet.kazekima.wsfighterscard.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.mynet.kazekima.wsfighterscard.BuildConfig
import com.mynet.kazekima.wsfighterscard.R
import java.io.InputStream
import java.io.OutputStream

class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by viewModels()

    private val importJsonLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri -> handleImportJsonUri(uri) }
        }
    }

    private val exportScheduleLauncher = registerForActivityResult(CreateDocumentContract("application/json")) { uri ->
        uri?.let { handleExportScheduleUri(it) }
    }

    private val exportProfileLauncher = registerForActivityResult(CreateDocumentContract("application/json")) { uri ->
        uri?.let { handleExportProfileUri(it) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        if (!BuildConfig.DEBUG) {
            findPreference<PreferenceCategory>("cat_debug")?.let {
                preferenceScreen.removePreference(it)
            }
        }

        findPreference<Preference>("pref_import")?.setOnPreferenceClickListener {
            showImportDialog()
            true
        }

        findPreference<Preference>("pref_export")?.setOnPreferenceClickListener {
            showExportDialog()
            true
        }

        findPreference<Preference>("pref_clear_all")?.setOnPreferenceClickListener {
            showClearAllDialog()
            true
        }

        findPreference<Preference>("pref_help")?.setOnPreferenceClickListener {
            showHelpFragment()
            true
        }

        findPreference<Preference>("pref_about")?.setOnPreferenceClickListener {
            showAboutDialog()
            true
        }
    }

    private fun showImportDialog() {
        val items = arrayOf(
            getString(R.string.pref_data_type_schedule),
            getString(R.string.pref_data_type_profile)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pref_title_import)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> startImportSchedule()
                    1 -> startImportProfile()
                }
            }
            .show()
    }

    private fun showExportDialog() {
        val items = arrayOf(
            getString(R.string.pref_data_type_schedule),
            getString(R.string.pref_data_type_profile)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pref_title_export)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> exportScheduleLauncher.launch("ws_fighters_schedule.json")
                    1 -> exportProfileLauncher.launch("ws_fighters_profile.json")
                }
            }
            .show()
    }

    private fun showClearAllDialog() {
        val items = arrayOf(
            getString(R.string.pref_data_type_schedule),
            getString(R.string.pref_data_type_profile)
        )
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pref_title_clear_all)
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showClearScheduleConfirmDialog()
                    1 -> showClearProfileConfirmDialog()
                }
            }
            .show()
    }

    private fun showClearScheduleConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_clear_all_confirm_title)
            .setMessage(R.string.dialog_clear_schedule_confirm_message)
            .setPositiveButton(R.string.dialog_delete_ok) { _, _ ->
                viewModel.clearScheduleData {
                    Toast.makeText(requireContext(), R.string.pref_clear_all_success, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun showClearProfileConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.dialog_clear_all_confirm_title)
            .setMessage(R.string.dialog_clear_profile_confirm_message)
            .setPositiveButton(R.string.dialog_delete_ok) { _, _ ->
                viewModel.clearProfileData {
                    Toast.makeText(requireContext(), R.string.pref_clear_all_success, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.dialog_delete_cancel, null)
            .show()
    }

    private fun showHelpFragment() {
        val fragmentManager = parentFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.nav_host_main, HelpFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showAboutDialog() {
        val version = BuildConfig.VERSION_NAME
        val message = getString(R.string.pref_about_version, version) + "\n" +
                getString(R.string.pref_about_developer)

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pref_title_about)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun startImportSchedule() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isDebugMode = sharedPreferences.getBoolean("pref_debug_mode", false)

        if (isDebugMode) {
            viewModel.importScheduleFromSample(requireContext()) { count ->
                Toast.makeText(requireContext(), getString(R.string.pref_import_success, count), Toast.LENGTH_SHORT).show()
            }
        } else {
            startImportJsonIntent()
        }
    }

    private fun startImportProfile() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isDebugMode = sharedPreferences.getBoolean("pref_debug_mode", false)

        if (isDebugMode) {
            viewModel.importProfileFromSample(requireContext()) { count ->
                Toast.makeText(requireContext(), getString(R.string.pref_import_success, count), Toast.LENGTH_SHORT).show()
            }
        } else {
            startImportJsonIntent()
        }
    }

    private fun startImportJsonIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/json"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        importJsonLauncher.launch(intent)
    }

    private fun handleImportJsonUri(uri: Uri) {
        runCatching {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri)
            if (inputStream != null) {
                viewModel.importAppDataFromJson(inputStream) { count ->
                    Toast.makeText(requireContext(), getString(R.string.pref_import_success, count), Toast.LENGTH_SHORT).show()
                }
            }
        }.onFailure {
            Toast.makeText(requireContext(), getString(R.string.pref_import_failed, it.message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleExportScheduleUri(uri: Uri) {
        runCatching {
            val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                viewModel.exportScheduleToJson(outputStream) {
                    Toast.makeText(requireContext(), getString(R.string.pref_export_success), Toast.LENGTH_SHORT).show()
                }
            }
        }.onFailure {
            Toast.makeText(requireContext(), getString(R.string.pref_export_failed, it.message), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleExportProfileUri(uri: Uri) {
        runCatching {
            val outputStream: OutputStream? = requireContext().contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                viewModel.exportProfileToJson(outputStream) {
                    Toast.makeText(requireContext(), getString(R.string.pref_export_success), Toast.LENGTH_SHORT).show()
                }
            }
        }.onFailure {
            Toast.makeText(requireContext(), getString(R.string.pref_export_failed, it.message), Toast.LENGTH_SHORT).show()
        }
    }

    class CreateDocumentContract(private val mimeType: String) : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String): Intent {
            return Intent(Intent.ACTION_CREATE_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)
                .setType(mimeType)
                .putExtra(Intent.EXTRA_TITLE, input)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK) intent?.data else null
        }
    }
}
