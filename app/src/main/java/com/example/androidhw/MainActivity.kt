package com.example.androidhw

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidhw.ui.theme.AndroidHWTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var storage: TrainingStorage

    private val settingsState = mutableStateOf(GameSettings.DEFAULT)
    private val lastResultState = mutableStateOf<GameResult?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsRepository = SettingsRepository(this)
        storage = TrainingStorage(this)
        settingsState.value = settingsRepository.load()
        lastResultState.value = storage.readLastResult()

        setContent {
            val settings by settingsState
            val lastResult by lastResultState

            AndroidHWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BrainTrainingScreen(
                        settings = settings,
                        lastResult = lastResult,
                        storage = storage,
                        onResultSaved = { lastResultState.value = it },
                        onOpenSettings = {
                            startActivity(Intent(this, SettingsActivity::class.java))
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        settingsState.value = settingsRepository.load()
        lastResultState.value = storage.readLastResult()
    }
}

@Composable
private fun BrainTrainingScreen(
    settings: GameSettings,
    lastResult: GameResult?,
    storage: TrainingStorage,
    onResultSaved: (GameResult) -> Unit,
    onOpenSettings: () -> Unit
) {
    var questionIndex by remember(settings) { mutableIntStateOf(1) }
    var correctCount by remember(settings) { mutableIntStateOf(0) }
    var answerText by remember(settings) { mutableStateOf("") }
    var feedback by remember(settings) { mutableStateOf("") }
    var answered by remember(settings) { mutableStateOf(false) }
    var finished by remember(settings) { mutableStateOf(false) }
    var currentTask by remember(settings) {
        mutableStateOf(TaskGenerator.createTask(settings, storage))
    }

    fun resetGame() {
        questionIndex = 1
        correctCount = 0
        answerText = ""
        feedback = ""
        answered = false
        finished = false
        currentTask = TaskGenerator.createTask(settings, storage)
    }

    LaunchedEffect(finished) {
        if (finished) {
            val result = GameResult(
                correctAnswers = correctCount,
                totalQuestions = settings.questionCount,
                settings = settings,
                timestamp = System.currentTimeMillis()
            )
            onResultSaved(result)
            withContext(Dispatchers.IO) {
                storage.saveLastResult(result)
                storage.appendHistory(result)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val invalidMessage = stringResource(R.string.message_invalid)
        val correctMessage = stringResource(R.string.message_correct)
        val wrongMessage = stringResource(R.string.message_wrong)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.brain_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onOpenSettings) {
                Text(text = stringResource(R.string.button_settings))
            }
        }

        Text(text = "${stringResource(R.string.label_question)}: $questionIndex / ${settings.questionCount}")
        Text(text = "${stringResource(R.string.label_score)}: $correctCount")

        lastResult?.let {
            Text(text = "${stringResource(R.string.label_last_result)}: ${formatResult(it)}")
        }
        Text(text = "${stringResource(R.string.label_history_path)}: ${storage.historyPath()}")
        Text(text = stringResource(R.string.label_internal_data))

        if (finished) {
            Text(text = stringResource(R.string.message_finished))
            Text(text = "${stringResource(R.string.label_score)}: $correctCount / ${settings.questionCount}")
            Button(onClick = { resetGame() }) {
                Text(text = stringResource(R.string.button_restart))
            }
        } else {
            Text(text = currentTask.prompt)

            OutlinedTextField(
                value = answerText,
                onValueChange = { answerText = it },
                label = { Text(text = stringResource(R.string.label_answer)) },
                placeholder = { Text(text = stringResource(R.string.hint_answer)) }
            )

            if (feedback.isNotEmpty()) {
                Text(text = feedback)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        if (!answered) {
                            val userAnswer = answerText.trim().toIntOrNull()
                            when (userAnswer) {
                                null -> feedback = invalidMessage
                                currentTask.answer -> {
                                    correctCount += 1
                                    feedback = correctMessage
                                    answered = true
                                }
                                else -> {
                                    feedback = "$wrongMessage ${currentTask.answer}"
                                    answered = true
                                }
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.button_check))
                }
                Button(
                    onClick = {
                        if (answered) {
                            if (questionIndex >= settings.questionCount) {
                                finished = true
                            } else {
                                questionIndex += 1
                                currentTask = TaskGenerator.createTask(settings, storage)
                                answerText = ""
                                feedback = ""
                                answered = false
                            }
                        }
                    }
                ) {
                    Text(text = stringResource(R.string.button_next))
                }
            }
        }
    }
}

private fun formatResult(result: GameResult): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    val date = format.format(Date(result.timestamp))
    return "${result.correctAnswers}/${result.totalQuestions} ${result.settings.taskType.name} " +
        "${result.settings.difficulty.name} $date"
}
