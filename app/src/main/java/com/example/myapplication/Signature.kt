package com.example.myapplication

import android.content.Context
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class Signature(){
    companion object{
        fun getAccessToken(context: Context):String?{
            val assetManager = context.resources.assets
            val inputStream = assetManager.open("config.json")
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val str: String = bufferedReader.readText()
            return try {
                val jsonObject = JSONObject(str)
                jsonObject.getString("access_token")
            } catch (e: JSONException) {
                e.printStackTrace()
                null
            }
        }
    }
}
