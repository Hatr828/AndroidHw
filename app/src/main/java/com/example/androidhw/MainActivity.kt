package com.example.androidhw

import android.os.AsyncTask
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidhw.ui.theme.AndroidHWTheme

class MainActivity : ComponentActivity() {
    private var iterationsInput by mutableStateOf("100")
    private var delayInput by mutableStateOf("300")
    private var statusLabel by mutableStateOf("")
    private var progressPercent by mutableStateOf(0)
    private var logMessages by mutableStateOf(listOf<String>())
    private var isRunning by mutableStateOf(false)
    private var isPaused by mutableStateOf(false)

    private var task: DemoAsyncTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statusLabel = getString(R.string.status_pending)
        setContent {
            AndroidHWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AsyncTaskScreen(
                        iterationsInput = iterationsInput,
                        delayInput = delayInput,
                        statusLabel = statusLabel,
                        progressPercent = progressPercent,
                        logMessages = logMessages,
                        isRunning = isRunning,
                        isPaused = isPaused,
                        onIterationsChange = { iterationsInput = it },
                        onDelayChange = { delayInput = it },
                        onStartStop = { onStartStop() },
                        onPauseResume = { onPauseResume() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        task?.cancel(true)
        task?.resumeTask()
    }

    private fun onStartStop() {
        if (isRunning) {
            stopTask()
        } else {
            startTask()
        }
    }

    private fun startTask() {
        val iterations = iterationsInput.toIntOrNull()?.takeIf { it > 0 } ?: DEFAULT_ITERATIONS
        val delayMs = delayInput.toLongOrNull()?.takeIf { it > 0 } ?: DEFAULT_DELAY_MS
        progressPercent = 0
        isRunning = true
        isPaused = false
        statusLabel = getString(R.string.status_pending)
        val newTask = DemoAsyncTask(
            iterations = iterations,
            delayMs = delayMs,
            onStatusChange = { statusLabel = statusToLabel(it) },
            onProgress = { progressPercent = it },
            onFinished = { canceled ->
                isRunning = false
                isPaused = false
                task = null
                addMessage(
                    if (canceled) getString(R.string.message_task_cancelled)
                    else getString(R.string.message_task_finished)
                )
            }
        )
        task = newTask
        addMessage(getString(R.string.message_task_started))
        newTask.execute()
    }

    private fun stopTask() {
        val current = task ?: return
        current.cancel(true)
        current.resumeTask()
    }

    private fun onPauseResume() {
        val current = task ?: return
        if (isPaused) {
            current.resumeTask()
            isPaused = false
            addMessage(getString(R.string.message_task_resumed))
        } else {
            current.pauseTask()
            isPaused = true
            addMessage(getString(R.string.message_task_paused))
        }
    }

    private fun statusToLabel(status: AsyncTask.Status): String {
        return when (status) {
            AsyncTask.Status.PENDING -> getString(R.string.status_pending)
            AsyncTask.Status.RUNNING -> getString(R.string.status_running)
            AsyncTask.Status.FINISHED -> getString(R.string.status_finished)
        }
    }

    private fun addMessage(message: String) {
        logMessages = (logMessages + message).takeLast(MAX_LOG_LINES)
    }

    private class DemoAsyncTask(
        private val iterations: Int,
        private val delayMs: Long,
        private val onStatusChange: (AsyncTask.Status) -> Unit,
        private val onProgress: (Int) -> Unit,
        private val onFinished: (Boolean) -> Unit
    ) : AsyncTask<Unit, Int, Unit>() {

        private val pauseLock = Object()
        @Volatile private var paused = false

        fun pauseTask() {
            paused = true
        }

        fun resumeTask() {
            synchronized(pauseLock) {
                paused = false
                pauseLock.notifyAll()
            }
        }

        override fun onPreExecute() {
            onStatusChange(AsyncTask.Status.RUNNING)
        }

        override fun doInBackground(vararg params: Unit?) {
            for (i in 1..iterations) {
                if (isCancelled) return
                synchronized(pauseLock) {
                    while (paused && !isCancelled) {
                        try {
                            pauseLock.wait()
                        } catch (_: InterruptedException) {
                            return
                        }
                    }
                }
                if (isCancelled) return
                try {
                    Thread.sleep(delayMs)
                } catch (_: InterruptedException) {
                    return
                }
                val progress = (i * 100) / iterations
                publishProgress(progress)
            }
        }

        override fun onProgressUpdate(vararg values: Int?) {
            val value = values.firstOrNull() ?: return
            onProgress(value)
        }

        override fun onPostExecute(result: Unit?) {
            onStatusChange(AsyncTask.Status.FINISHED)
            onFinished(false)
        }

        override fun onCancelled(result: Unit?) {
            onStatusChange(AsyncTask.Status.FINISHED)
            onFinished(true)
        }
    }

    companion object {
        private const val DEFAULT_ITERATIONS = 100
        private const val DEFAULT_DELAY_MS = 300L
        private const val MAX_LOG_LINES = 8
    }
}

@Composable
private fun AsyncTaskScreen(
    iterationsInput: String,
    delayInput: String,
    statusLabel: String,
    progressPercent: Int,
    logMessages: List<String>,
    isRunning: Boolean,
    isPaused: Boolean,
    onIterationsChange: (String) -> Unit,
    onDelayChange: (String) -> Unit,
    onStartStop: () -> Unit,
    onPauseResume: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = iterationsInput,
            onValueChange = onIterationsChange,
            label = { Text(text = stringResource(R.string.label_iterations)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isRunning,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = delayInput,
            onValueChange = onDelayChange,
            label = { Text(text = stringResource(R.string.label_delay_ms)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isRunning,
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Text(text = "${stringResource(R.string.label_status)}: $statusLabel")
        Text(text = "${stringResource(R.string.label_progress)}: ${progressPercent.coerceIn(0, 100)}%")
        LinearProgressIndicator(
            progress = progressPercent.coerceIn(0, 100) / 100f,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val mainLabel = if (isRunning) R.string.button_stop else R.string.button_start
            Button(onClick = onStartStop) {
                Text(text = stringResource(mainLabel))
            }
            if (isRunning) {
                val cancelLabel = if (isPaused) R.string.button_resume else R.string.button_cancel
                Button(onClick = onPauseResume) {
                    Text(text = stringResource(cancelLabel))
                }
            }
        }
        Text(
            text = stringResource(R.string.label_log),
            style = MaterialTheme.typography.titleMedium
        )
        val logText = if (logMessages.isEmpty()) "-" else logMessages.joinToString("\n")
        Text(text = logText)
    }
}

@Preview(showBackground = true)
@Composable
private fun AsyncTaskPreview() {
    AndroidHWTheme {
        AsyncTaskScreen(
            iterationsInput = "100",
            delayInput = "300",
            statusLabel = "Pending",
            progressPercent = 25,
            logMessages = listOf("Task started", "Task paused"),
            isRunning = true,
            isPaused = true,
            onIterationsChange = {},
            onDelayChange = {},
            onStartStop = {},
            onPauseResume = {}
        )
    }
}
