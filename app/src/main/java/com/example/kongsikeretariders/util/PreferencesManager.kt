package com.example.kongsikeretariders.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {
    private var sharedPreferences: SharedPreferences? = null

    fun init(context: Context){
        if(sharedPreferences == null){
            sharedPreferences = context.getSharedPreferences("user_SharedPreferences", Context.MODE_PRIVATE)
        }
    }

    private fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences ?: throw IllegalStateException("PreferencesManager Not Initialized")
    }

    fun putPreference(key:String, value: Boolean){
        val editor = getSharedPreferences().edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun putPreference(key:String, value: String){
        val editor = getSharedPreferences().edit()
        editor.putString(key,value)
        editor.apply()
    }

    fun getBooleanPreference(key: String): Boolean{
        return getSharedPreferences().getBoolean(key,false)
    }

    fun getStringPreference(key: String): String{
        return getSharedPreferences().getString(key,"") ?: ""
    }

    fun clearPreferences(){
        getSharedPreferences().edit().clear().apply()
    }
}