package com.example.collabmefrontend.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStorage by preferencesDataStore(name = "auth")

class TokenStorage(
    private val context: Context
) {
    companion object{
        private val TOKEN_KEY = stringPreferencesKey("access_token")
    }
    suspend fun saveToken(token: String){
        context.dataStorage.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    suspend fun getToken() : String? {
        val prefs = context.dataStorage.data.first()
        return prefs[TOKEN_KEY]
    }

    suspend fun clear(){
        context.dataStorage.edit { prefs->
            prefs.remove(TOKEN_KEY)
        }
    }
}