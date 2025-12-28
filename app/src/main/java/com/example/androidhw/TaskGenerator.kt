package com.example.androidhw

import kotlin.random.Random

object TaskGenerator {
    private val random = Random(System.currentTimeMillis())

    fun createTask(settings: GameSettings, storage: TrainingStorage): GameTask {
        return when (settings.taskType) {
            TaskType.ARITHMETIC -> createArithmeticTask(settings.difficulty)
            TaskType.SYMBOLS -> createSymbolTask(storage)
        }
    }

    private fun createArithmeticTask(difficulty: Difficulty): GameTask {
        val ops = when (difficulty) {
            Difficulty.EASY -> listOf('+', '-')
            Difficulty.MEDIUM -> listOf('+', '-', '*')
            Difficulty.HARD -> listOf('+', '-', '*')
        }
        val range = when (difficulty) {
            Difficulty.EASY -> 1..20
            Difficulty.MEDIUM -> 5..50
            Difficulty.HARD -> 10..99
        }

        val op = ops[random.nextInt(ops.size)]
        return when (op) {
            '+' -> {
                val a = randomIn(range)
                val b = randomIn(range)
                GameTask("$a + $b", a + b)
            }
            '-' -> {
                var a = randomIn(range)
                var b = randomIn(range)
                if (b > a) {
                    val tmp = a
                    a = b
                    b = tmp
                }
                GameTask("$a - $b", a - b)
            }
            else -> {
                val multRange = when (difficulty) {
                    Difficulty.EASY -> 2..12
                    Difficulty.MEDIUM -> 3..15
                    Difficulty.HARD -> 5..20
                }
                val a = randomIn(multRange)
                val b = randomIn(multRange)
                GameTask("$a * $b", a * b)
            }
        }
    }

    private fun createSymbolTask(storage: TrainingStorage): GameTask {
        val puzzles = storage.loadPuzzles()
        val puzzle = puzzles[random.nextInt(puzzles.size)]
        return GameTask("Count symbols: ${puzzle.pattern}", puzzle.answer)
    }

    private fun randomIn(range: IntRange): Int {
        return random.nextInt(range.first, range.last + 1)
    }
}
