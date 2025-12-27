package com.example.androidhw

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidhw.ui.theme.AndroidHWTheme

class MainActivity : ComponentActivity() {
    private var onCreateTime = 0L
    private var onStartTime = 0L
    private var onResumeTime = 0L
    private var onPauseTime = 0L
    private var onStopTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val now = System.currentTimeMillis()
        onCreateTime = now
        Log.d(TAG, "onCreate at $now")
        enableEdgeToEdge()
        setContent {
            AndroidHWTheme {
                val context = LocalContext.current
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        onOpenUserGuess = {
                            Log.d(TAG, "User guess button clicked")
                            context.startActivity(
                                Intent(context, UserGuessActivity::class.java)
                            )
                        },
                        onOpenAndroidGuess = {
                            Log.d(TAG, "Android guess button clicked")
                            context.startActivity(
                                Intent(context, AndroidGuessActivity::class.java)
                            )
                        },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val now = System.currentTimeMillis()
        onStartTime = now
        val delta = if (onCreateTime != 0L) now - onCreateTime else -1L
        Log.d(TAG, "onStart (delta from onCreate: ${delta}ms)")
    }

    override fun onResume() {
        super.onResume()
        val now = System.currentTimeMillis()
        onResumeTime = now
        val delta = if (onStartTime != 0L) now - onStartTime else -1L
        Log.d(TAG, "onResume (delta from onStart: ${delta}ms)")
    }

    override fun onPause() {
        val now = System.currentTimeMillis()
        onPauseTime = now
        val delta = if (onResumeTime != 0L) now - onResumeTime else -1L
        Log.d(TAG, "onPause (delta from onResume: ${delta}ms)")
        super.onPause()
    }

    override fun onStop() {
        val now = System.currentTimeMillis()
        onStopTime = now
        val delta = if (onPauseTime != 0L) now - onPauseTime else -1L
        Log.d(TAG, "onStop (delta from onPause: ${delta}ms)")
        super.onStop()
    }

    override fun onDestroy() {
        val now = System.currentTimeMillis()
        val delta = if (onStopTime != 0L) now - onStopTime else -1L
        Log.d(TAG, "onDestroy (delta from onStop: ${delta}ms)")
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

@Composable
fun HomeScreen(
    onOpenUserGuess: () -> Unit,
    onOpenAndroidGuess: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Guess the Number", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Choose a mode to start the game.")
        Button(onClick = onOpenUserGuess) {
            Text(text = "I guess the number")
        }
        Button(onClick = onOpenAndroidGuess) {
            Text(text = "Android guesses the number")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AndroidHWTheme {
        HomeScreen(onOpenUserGuess = {}, onOpenAndroidGuess = {})
    }
}
