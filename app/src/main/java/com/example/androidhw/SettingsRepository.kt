package com.example.androidhw

import android.content.Context

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): GameSettings {
        val taskType = TaskType.fromName(prefs.getString(KEY_TASK_TYPE, null))
        val difficulty = Difficulty.fromName(prefs.getString(KEY_DIFFICULTY, null))
        val count = prefs.getInt(KEY_QUESTION_COUNT, GameSettings.DEFAULT.questionCount)
        return GameSettings(taskType, difficulty, count)
    }

    fun save(settings: GameSettings) {
        prefs.edit()
            .putString(KEY_TASK_TYPE, settings.taskType.name)
            .putString(KEY_DIFFICULTY, settings.difficulty.name)
            .putInt(KEY_QUESTION_COUNT, settings.questionCount)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "brain_training_settings"
        private const val KEY_TASK_TYPE = "task_type"
        private const val KEY_DIFFICULTY = "difficulty"
        private const val KEY_QUESTION_COUNT = "question_count"
    }
}
