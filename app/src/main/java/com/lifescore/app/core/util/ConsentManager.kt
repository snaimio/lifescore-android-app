package com.lifescore.app.core.util

import android.content.Context
import android.content.SharedPreferences

object ConsentManager {

    private const val PREFS_NAME = "lifescore_consent_prefs"
    private const val KEY_CONSENT_DECIDED = "key_consent_decided"
    private const val KEY_CONSENT_ACCEPTED = "key_consent_accepted"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isConsentDecided(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_CONSENT_DECIDED, false)
    }

    fun isConsentAccepted(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_CONSENT_ACCEPTED, true)
    }

    fun setConsent(context: Context, accepted: Boolean) {
        getPrefs(context).edit()
            .putBoolean(KEY_CONSENT_DECIDED, true)
            .putBoolean(KEY_CONSENT_ACCEPTED, accepted)
            .apply()
    }

    fun revokeConsent(context: Context) {
        getPrefs(context).edit()
            .putBoolean(KEY_CONSENT_DECIDED, true)
            .putBoolean(KEY_CONSENT_ACCEPTED, false)
            .apply()
    }

    fun isCloudSyncEnabled(context: Context): Boolean {
        return isConsentAccepted(context)
    }

    fun isAiCoachEnabled(context: Context): Boolean {
        return isConsentAccepted(context)
    }
}
