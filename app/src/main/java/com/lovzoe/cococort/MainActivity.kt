package com.lovzoe.cococort

import android.os.Bundle
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.lovzoe.cococort.ui.theme.CococoRTTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CococoRTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(mainViewModel = MainViewModel(applicationContext))
                }
            }
        }
    }
}

@Composable
fun MainScreen(mainViewModel: MainViewModel) {
    Column(Modifier.fillMaxWidth()) {
        Button(onClick = {
            mainViewModel.connect()
        }) {
            Text(text = "connect")
        }

        Button(onClick = { mainViewModel.disconnect() }) {
            Text(text = "disconnect")
        }
        when (val state = mainViewModel.binderState.collectAsState().value) {
            is BinderState.Disconnected -> Text(text = "Disconnected")
            is BinderState.Connecting -> Text(text = "Connecting")
            is BinderState.Connected -> Text(text = "Connected")
            is BinderState.Error -> Text(text = "Error: ${state.errorMessage}")
        }
    }
}
