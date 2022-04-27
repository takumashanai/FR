package com.example.myapplication

import android.util.Log
import java.lang.Exception

class ResponseErrorException(msg: String?) : Exception(msg) {
    companion object {
        private const val serialVersionUID = 1L
    }

    init {
        Log.d("ResponseErrorException", msg!!)
    }
}