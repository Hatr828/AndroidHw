package com.example.androidhw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidhw.ui.theme.AndroidHWTheme
import kotlin.math.roundToInt

class SettingsActivity : ComponentActivity() {
    private lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepository = SettingsRepository(this)
        val currentSettings = settingsRepository.load()

        setContent {
            AndroidHWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SettingsScreen(
                        initial = currentSettings,
                        onSave = { settings ->
                            settingsRepository.save(settings)
                            finish()
                        },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    initial: GameSettings,
    onSave: (GameSettings) -> Unit,
    onCancel: () -> Unit
) {
    var taskType by remember { mutableStateOf(initial.taskType) }
    var difficulty by remember { mutableStateOf(initial.difficulty) }
    var questionCount by remember { mutableIntStateOf(initial.questionCount) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineSmall
        )

        Text(text = stringResource(R.string.settings_task_type))
        RadioRow(
            selected = taskType == TaskType.ARITHMETIC,
            label = stringResource(R.string.task_type_arithmetic)
        ) { taskType = TaskType.ARITHMETIC }
        RadioRow(
            selected = taskType == TaskType.SYMBOLS,
            label = stringResource(R.string.task_type_symbols)
        ) { taskType = TaskType.SYMBOLS }

        Text(text = stringResource(R.string.settings_difficulty))
        RadioRow(
            selected = difficulty == Difficulty.EASY,
            label = stringResource(R.string.difficulty_easy)
        ) { difficulty = Difficulty.EASY }
        RadioRow(
            selected = difficulty == Difficulty.MEDIUM,
            label = stringResource(R.string.difficulty_medium)
        ) { difficulty = Difficulty.MEDIUM }
        RadioRow(
            selected = difficulty == Difficulty.HARD,
            label = stringResource(R.string.difficulty_hard)
        ) { difficulty = Difficulty.HARD }

        Text(text = "${stringResource(R.string.settings_questions)}: $questionCount")
        Slider(
            value = questionCount.toFloat(),
            onValueChange = { questionCount = it.roundToInt() },
            valueRange = 5f..20f,
            steps = 14
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = {
                onSave(GameSettings(taskType, difficulty, questionCount))
            }) {
                Text(text = stringResource(R.string.button_save))
            }
            OutlinedButton(onClick = onCancel) {
                Text(text = stringResource(R.string.button_cancel))
            }
        }
    }
}

@Composable
private fun RadioRow(
    selected: Boolean,
    label: String,
    onSelect: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(text = label)
    }
}
