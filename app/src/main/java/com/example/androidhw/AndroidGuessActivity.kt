package com.example.androidhw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidhw.ui.theme.AndroidHWTheme

class AndroidGuessActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidHWTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AndroidGuessScreen(
                        onBack = { finish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AndroidGuessScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var low by rememberSaveable { mutableIntStateOf(1) }
    var high by rememberSaveable { mutableIntStateOf(100) }
    var guess by rememberSaveable { mutableIntStateOf(50) }
    var steps by rememberSaveable { mutableIntStateOf(1) }
    var status by rememberSaveable { mutableStateOf("Is your number 50?") }
    var finished by rememberSaveable { mutableStateOf(false) }

    val resetGame = {
        low = 1
        high = 100
        guess = 50
        steps = 1
        status = "Is your number 50?"
        finished = false
    }

    val makeGuess = { newLow: Int, newHigh: Int ->
        if (newLow > newHigh) {
            status = "Hints are inconsistent. Reset the game."
            finished = true
        } else {
            low = newLow
            high = newHigh
            guess = (newLow + newHigh) / 2
            steps += 1
            status = "Is your number $guess?"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Android guesses the number", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Think of a number between 1 and 100, then answer honestly.")
        Text(text = "Range: $low..$high")
        Text(text = status, style = MaterialTheme.typography.bodyLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { makeGuess(low, guess - 1) },
                enabled = !finished
            ) {
                Text(text = "Lower")
            }
            Button(
                onClick = { makeGuess(guess + 1, high) },
                enabled = !finished
            ) {
                Text(text = "Higher")
            }
            Button(
                onClick = {
                    status = "Guessed in $steps steps!"
                    finished = true
                },
                enabled = !finished
            ) {
                Text(text = "Correct")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = resetGame) {
                Text(text = "Reset")
            }
            Button(onClick = onBack) {
                Text(text = "Back")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AndroidGuessScreenPreview() {
    AndroidHWTheme {
        AndroidGuessScreen(onBack = {})
    }
}
