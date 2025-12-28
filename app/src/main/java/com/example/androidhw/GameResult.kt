package com.example.androidhw

data class GameResult(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val settings: GameSettings,
    val timestamp: Long
) {
    fun toLine(): String {
        return listOf(
            timestamp.toString(),
            settings.taskType.name,
            settings.difficulty.name,
            totalQuestions.toString(),
            correctAnswers.toString()
        ).joinToString("|")
    }

    companion object {
        fun fromLine(line: String): GameResult? {
            val parts = line.split('|')
            if (parts.size < 5) return null
            val timestamp = parts[0].toLongOrNull() ?: return null
            val taskType = TaskType.fromName(parts[1])
            val difficulty = Difficulty.fromName(parts[2])
            val totalQuestions = parts[3].toIntOrNull() ?: return null
            val correctAnswers = parts[4].toIntOrNull() ?: return null
            val settings = GameSettings(taskType, difficulty, totalQuestions)
            return GameResult(correctAnswers, totalQuestions, settings, timestamp)
        }
    }
}
