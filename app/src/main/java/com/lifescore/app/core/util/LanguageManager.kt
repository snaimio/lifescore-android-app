package com.lifescore.app.core.util

import android.content.Context
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val flagEmoji: String,
    val isRtl: Boolean = false
) {
    ENGLISH("en", "English", "🇺🇸", isRtl = false),
    SPANISH("es", "Español", "🇪🇸", isRtl = false),
    CHINESE("zh", "中文 (简体)", "🇨🇳", isRtl = false),
    ARABIC("ar", "العربية", "🇸🇦", isRtl = true),
    HINDI("hi", "हिन्दी", "🇮🇳", isRtl = false);

    companion object {
        fun fromCode(code: String): AppLanguage {
            val normalized = code.lowercase().take(2)
            return values().firstOrNull { it.code == normalized } ?: ENGLISH
        }
    }
}

object LanguageManager {

    private const val PREFS_NAME = "lifescore_language_prefs"
    private const val KEY_SELECTED_LANGUAGE = "selected_language_code"

    private var activeLanguage: AppLanguage = detectDeviceLanguage()

    fun detectDeviceLanguage(): AppLanguage {
        val deviceLang = Locale.getDefault().language
        return AppLanguage.fromCode(deviceLang)
    }

    fun getCurrentLanguage(): AppLanguage {
        return activeLanguage
    }

    fun setAppLanguage(language: AppLanguage) {
        activeLanguage = language
    }

    fun isRtl(): Boolean {
        return activeLanguage.isRtl
    }

    fun getSupportedLanguages(): List<AppLanguage> {
        return AppLanguage.values().toList()
    }
}
