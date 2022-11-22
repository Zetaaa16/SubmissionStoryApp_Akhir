package com.fadhil.submissionstoryapp_akhir.pref

import android.content.Context
import androidx.core.content.edit
import com.fadhil.submissionstoryapp_akhir.pref.ConstVal.LOGIN_INFO
import com.fadhil.submissionstoryapp_akhir.pref.ConstVal.NO_TOKEN
import com.fadhil.submissionstoryapp_akhir.pref.ConstVal.TOKEN_KEY


class PreferenceDataSource(context: Context) {

    private val preferences = context.getSharedPreferences(LOGIN_INFO, Context.MODE_PRIVATE)

    fun saveLoginInfo(token: String) {
        val editor = preferences.edit()
        editor.putString(TOKEN_KEY, token)
        editor.apply()
    }

    fun readLoginInfo(): String? {
        return preferences.getString(TOKEN_KEY, NO_TOKEN)
    }

    fun clearLoginInfo() {
        preferences.edit(commit = true) {
            clear()
        }
    }
}