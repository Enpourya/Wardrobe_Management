package com.pourya.wardrobe.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

object LocaleManager {

    const val PREF_LANGUAGE = "selected_language"
    const val LANG_PERSIAN = "fa"
    const val LANG_ENGLISH = "en"
    private const val PREFS_NAME = "wardrobe_prefs"

    fun setLocale(context: Context, languageCode: String): Context {
        saveLanguage(context, languageCode)
        return updateResources(context, languageCode)
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PREF_LANGUAGE, "") ?: ""
    }

    fun isLanguageSelected(context: Context): Boolean {
        return getLanguage(context).isNotEmpty()
    }

    fun isPersian(context: Context): Boolean {
        return getLanguage(context) == LANG_PERSIAN
    }

    private fun saveLanguage(context: Context, languageCode: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(PREF_LANGUAGE, languageCode).apply()
    }

    private fun updateResources(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun onAttach(context: Context): Context {
        val lang = getLanguage(context)
        return if (lang.isNotEmpty()) setLocale(context, lang) else context
    }
}
