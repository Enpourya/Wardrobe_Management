package com.pourya.wardrobe.ui.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.pourya.wardrobe.R
import com.pourya.wardrobe.databinding.FragmentSettingsBinding
import com.pourya.wardrobe.utils.AIService
import com.pourya.wardrobe.utils.BackupManager
import com.pourya.wardrobe.utils.LocaleManager
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val isFarsi by lazy { LocaleManager.isPersian(requireContext()) }

    private val createBackupLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let { doBackup(it) }
    }

    private val openBackupLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { doRestore(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load current API key (masked)
        val currentKey = AIService.getApiKey(requireContext())
        if (currentKey.isNotEmpty()) {
            binding.tvApiKeyStatus.text = if (isFarsi) "✅ کلید API تنظیم شده" else "✅ API key is set"
        }

        binding.btnSaveApiKey.setOnClickListener {
            val key = binding.etApiKey.text?.toString()?.trim() ?: ""
            if (key.isNotEmpty()) {
                AIService.saveApiKey(requireContext(), key)
                binding.tvApiKeyStatus.text = if (isFarsi) "✅ کلید API ذخیره شد" else "✅ API key saved"
                binding.etApiKey.text?.clear()
                showToast(if (isFarsi) "کلید API ذخیره شد" else "API key saved")
            }
        }

        binding.btnBackup.setOnClickListener {
            createBackupLauncher.launch(BackupManager.getBackupFileName())
        }

        binding.btnRestore.setOnClickListener {
            showRestoreWarning()
        }

        binding.btnChangeLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.btnGithub.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Enpourya/Wardrobe_Management"))
            startActivity(intent)
        }

        binding.btnAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun doBackup(uri: Uri) {
        binding.progressBackup.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = BackupManager.createBackup(requireContext(), uri)
            binding.progressBackup.visibility = View.GONE
            if (result.isSuccess) {
                showSnackbar(if (isFarsi) "✅ پشتیبان‌گیری موفق" else "✅ Backup created successfully")
            } else {
                showSnackbar(if (isFarsi) "❌ خطا در پشتیبان‌گیری" else "❌ Backup failed")
            }
        }
    }

    private fun showRestoreWarning() {
        val msg = if (isFarsi)
            "بازیابی فایل پشتیبان، داده‌های فعلی را جایگزین می‌کند. ادامه می‌دهید؟"
        else
            "Restoring a backup will replace all current data. Continue?"

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(msg)
            .setPositiveButton(if (isFarsi) "بله" else "Yes") { _, _ ->
                openBackupLauncher.launch(arrayOf("application/octet-stream", "*/*"))
            }
            .setNegativeButton(if (isFarsi) "انصراف" else "Cancel", null)
            .show()
    }

    private fun doRestore(uri: Uri) {
        binding.progressBackup.visibility = View.VISIBLE
        lifecycleScope.launch {
            val result = BackupManager.restoreBackup(requireContext(), uri)
            binding.progressBackup.visibility = View.GONE
            if (result.isSuccess) {
                showSnackbar(if (isFarsi) "✅ بازیابی موفق — اپلیکیشن را دوباره باز کنید" else "✅ Restore successful — please restart the app")
            } else {
                showSnackbar(if (isFarsi) "❌ خطا در بازیابی" else "❌ Restore failed")
            }
        }
    }

    private fun showLanguageDialog() {
        val opts = arrayOf("🇮🇷 فارسی", "🇬🇧 English")
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (isFarsi) "انتخاب زبان" else "Select Language")
            .setItems(opts) { _, which ->
                val lang = if (which == 0) LocaleManager.LANG_PERSIAN else LocaleManager.LANG_ENGLISH
                LocaleManager.setLocale(requireContext(), lang)
                requireActivity().recreate()
            }
            .show()
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (isFarsi) "درباره برنامه" else "About")
            .setMessage(
                if (isFarsi) """
🗃️ مدیریت کمد لباس
نسخه ۱.۰.۰

مدیریت هوشمند کمد و کشوهای خانه
با پشتیبانی از هوش مصنوعی برای پیشنهاد لباس

کد منبع:
github.com/Enpourya/Wardrobe_Management
                """.trimIndent()
                else """
🗃️ Wardrobe Manager
Version 1.0.0

Smart management of your closets & drawers
with AI-powered outfit suggestions

Source code:
github.com/Enpourya/Wardrobe_Management
                """.trimIndent()
            )
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showToast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    private fun showSnackbar(msg: String) = view?.let { Snackbar.make(it, msg, Snackbar.LENGTH_LONG).show() }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
