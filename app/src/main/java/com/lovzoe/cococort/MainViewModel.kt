package com.lovzoe.cococort

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.launch

class MainViewModel(val context: Context) : ViewModel() {
    private val _binderState: MutableStateFlow<BinderState> =
        MutableStateFlow(BinderState.Disconnected)
    val binderState: StateFlow<BinderState>
        get() = _binderState

    private val connectionFlow = callbackFlow {
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                Log.d(TAG, "onServiceConnected: componentName = $name")
                trySend(BinderState.Connected)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.d(TAG, "onServiceDisconnected: componentName = $name")
                trySend(BinderState.Disconnected)
                close()
            }

            override fun onNullBinding(name: ComponentName?) {
                Log.d(TAG, "onNullBinding: componentName = $name")
                trySend(BinderState.Error("null binding: componentName = $name"))
                close()
            }

            override fun onBindingDied(name: ComponentName?) {
                Log.d(TAG, "onBindingDied: componentName = $name")
                trySend(BinderState.Error("binding died: componentName = $name"))
                close()
            }
        }
        var connected = false
        if (!context.bindService(constructIntent(), connection, Context.BIND_AUTO_CREATE)) {
            Log.d(TAG, "bindService: bind return false, bind failed")
            close()
        } else {
            connected = true
        }
        awaitClose {
            Log.d(TAG, "close: closing flow, connected = $connected")
            if (connected) context.unbindService(connection)
        }
    }

    fun connect() {
        viewModelScope.launch {
            Log.d(TAG, "connect: coroutine launched, Thread[ ${Thread.currentThread().name} ]")
            connectionFlow.collect {
                Log.d(TAG, "collect: state = $it")
                _binderState.value = it
            }
        }
    }

    private fun constructIntent() = Intent().apply {
        setPackage("com.lovzoe.service")
        component = ComponentName("com.lovzoe.service", "com.lovzoe.service.MyService")
    }


    fun disconnect() {
        Log.d(TAG, "disconnect")
    }

    companion object {
        const val TAG = "MainViewModel"
    }
}

sealed class BinderState {
    object Connected : BinderState()
    object Connecting : BinderState()
    object Disconnected : BinderState()
    class Error(val errorMessage: String) : BinderState()
}