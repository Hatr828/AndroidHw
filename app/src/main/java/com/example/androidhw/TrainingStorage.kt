package com.example.androidhw

import android.content.Context
import java.io.File

data class SymbolPuzzle(
    val pattern: String,
    val answer: Int
)

class TrainingStorage(context: Context) {
    private val appContext = context.applicationContext
    private val puzzleFile = File(appContext.filesDir, "puzzles.txt")
    private val lastResultFile = File(appContext.filesDir, "last_result.txt")
    private val historyFile = appContext.getExternalFilesDir(null)?.let { File(it, "history.csv") }

    private val defaultPuzzles = listOf(
        SymbolPuzzle("###", 3),
        SymbolPuzzle("@@@@", 4),
        SymbolPuzzle("+++++", 5),
        SymbolPuzzle("**", 2),
        SymbolPuzzle("0000", 4),
        SymbolPuzzle("%%%%%%", 6)
    )

    init {
        ensurePuzzleBank()
    }

    fun ensurePuzzleBank() {
        if (!puzzleFile.exists()) {
            val data = defaultPuzzles.joinToString("\n") { "${it.pattern}|${it.answer}" }
            puzzleFile.writeText(data)
        }
    }

    fun loadPuzzles(): List<SymbolPuzzle> {
        if (!puzzleFile.exists()) return defaultPuzzles
        val parsed = puzzleFile.readLines()
            .mapNotNull { parsePuzzleLine(it) }
        return if (parsed.isNotEmpty()) parsed else defaultPuzzles
    }

    fun saveLastResult(result: GameResult) {
        lastResultFile.writeText(result.toLine())
    }

    fun readLastResult(): GameResult? {
        if (!lastResultFile.exists()) return null
        val line = lastResultFile.readText().trim()
        return if (line.isEmpty()) null else GameResult.fromLine(line)
    }

    fun appendHistory(result: GameResult) {
        historyFile?.appendText(result.toLine() + "\n")
    }

    fun historyPath(): String {
        return historyFile?.absolutePath ?: "External storage unavailable"
    }

    private fun parsePuzzleLine(line: String): SymbolPuzzle? {
        val parts = line.split('|')
        if (parts.size < 2) return null
        val pattern = parts[0].trim()
        val answer = parts[1].trim().toIntOrNull() ?: return null
        if (pattern.isEmpty()) return null
        return SymbolPuzzle(pattern, answer)
    }
}
