package com.lovzoe.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.os.Parcel
import android.util.Log

class MyService : Service() {

    private val binder = object : Binder() {
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            Log.d(TAG, "onTransact: code = $code")
            return true
        }
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind: intent = $intent")
        return binder
    }

    companion object {
        const val TAG = "MyService"
    }
}