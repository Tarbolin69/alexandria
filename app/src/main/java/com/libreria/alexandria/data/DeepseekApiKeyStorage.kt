package com.libreria.alexandria.data

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepseekApiKeyStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)

    fun saveApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key).apply()
    }

    fun hasApiKey(): Boolean = !getApiKey().isNullOrBlank()

    companion object {
        private const val PREFS_NAME = "deepseek_prefs"
        private const val KEY_API_KEY = "api_key"
    }
}
