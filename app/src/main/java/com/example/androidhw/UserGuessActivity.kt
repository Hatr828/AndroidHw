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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidhw.ui.theme.AndroidHWTheme
import kotlin.random.Random

class UserGuessActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidHWTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    UserGuessScreen(
                        onBack = { finish() },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun UserGuessScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var secret by rememberSaveable { mutableIntStateOf(Random.nextInt(1, 101)) }
    var input by rememberSaveable { mutableStateOf("") }
    var message by rememberSaveable { mutableStateOf("Enter a number between 1 and 100.") }
    var attempts by rememberSaveable { mutableIntStateOf(0) }

    val resetGame = {
        secret = Random.nextInt(1, 101)
        attempts = 0
        input = ""
        message = "Enter a number between 1 and 100."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "You guess the number", style = MaterialTheme.typography.headlineSmall)
        Text(text = "Try to find the secret number.")
        OutlinedTextField(
            value = input,
            onValueChange = { value ->
                input = value.filter { it.isDigit() }
            },
            label = { Text(text = "Your guess") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Button(onClick = {
            val guess = input.toIntOrNull()
            if (guess == null) {
                message = "Enter a valid number from 1 to 100."
                return@Button
            }
            if (guess !in 1..100) {
                message = "Enter a number between 1 and 100."
                return@Button
            }
            attempts += 1
            message = when {
                guess < secret -> "Too low."
                guess > secret -> "Too high."
                else -> "Correct! Attempts: $attempts"
            }
        }) {
            Text(text = "Check")
        }
        Text(text = message)
        Text(text = "Attempts: $attempts")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = resetGame) {
                Text(text = "New game")
            }
            Button(onClick = onBack) {
                Text(text = "Back")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserGuessScreenPreview() {
    AndroidHWTheme {
        UserGuessScreen(onBack = {})
    }
}
