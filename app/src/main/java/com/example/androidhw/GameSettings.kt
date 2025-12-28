package com.example.androidhw

enum class TaskType {
    ARITHMETIC,
    SYMBOLS;

    companion object {
        fun fromName(name: String?): TaskType {
            return values().firstOrNull { it.name == name } ?: ARITHMETIC
        }
    }
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD;

    companion object {
        fun fromName(name: String?): Difficulty {
            return values().firstOrNull { it.name == name } ?: EASY
        }
    }
}

data class GameSettings(
    val taskType: TaskType,
    val difficulty: Difficulty,
    val questionCount: Int
) {
    companion object {
        val DEFAULT = GameSettings(TaskType.ARITHMETIC, Difficulty.EASY, 10)
    }
}
