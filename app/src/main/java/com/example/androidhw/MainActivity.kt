package com.example.androidhw

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import java.text.DecimalFormat

class MainActivity : ComponentActivity() {
    private lateinit var display: TextView
    private val formatter = DecimalFormat("0.##########")

    private var currentInput = ""
    private var accumulator: Double? = null
    private var pendingOp: Char? = null
    private var justEvaluated = false
    private var errorState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.text_display)
        display.text = getString(R.string.display_zero)

        val digits = mapOf(
            R.id.button_0 to getString(R.string.button_0),
            R.id.button_1 to getString(R.string.button_1),
            R.id.button_2 to getString(R.string.button_2),
            R.id.button_3 to getString(R.string.button_3),
            R.id.button_4 to getString(R.string.button_4),
            R.id.button_5 to getString(R.string.button_5),
            R.id.button_6 to getString(R.string.button_6),
            R.id.button_7 to getString(R.string.button_7),
            R.id.button_8 to getString(R.string.button_8),
            R.id.button_9 to getString(R.string.button_9)
        )

        for ((id, digit) in digits) {
            findViewById<Button>(id).setOnClickListener { appendDigit(digit) }
        }

        findViewById<Button>(R.id.button_decimal).setOnClickListener {
            appendDecimal(getString(R.string.button_decimal))
        }
        findViewById<Button>(R.id.button_clear).setOnClickListener { clearAll() }

        val operators = mapOf(
            R.id.button_add to getString(R.string.button_add).first(),
            R.id.button_subtract to getString(R.string.button_subtract).first(),
            R.id.button_multiply to getString(R.string.button_multiply).first(),
            R.id.button_divide to getString(R.string.button_divide).first()
        )

        for ((id, op) in operators) {
            findViewById<Button>(id).setOnClickListener { setOperator(op) }
        }

        findViewById<Button>(R.id.button_equals).setOnClickListener { evaluate() }
    }

    private fun appendDigit(digit: String) {
        if (errorState) {
            clearAll()
        }
        if (justEvaluated && pendingOp == null) {
            currentInput = ""
            accumulator = null
            justEvaluated = false
        }
        currentInput = if (currentInput == getString(R.string.display_zero)) {
            digit
        } else {
            currentInput + digit
        }
        updateDisplay(currentInput)
    }

    private fun appendDecimal(decimal: String) {
        if (errorState) {
            clearAll()
        }
        if (justEvaluated && pendingOp == null) {
            currentInput = ""
            accumulator = null
            justEvaluated = false
        }
        if (currentInput.isEmpty()) {
            currentInput = getString(R.string.display_zero) + decimal
        } else if (!currentInput.contains(decimal)) {
            currentInput += decimal
        }
        updateDisplay(currentInput)
    }

    private fun setOperator(op: Char) {
        if (errorState) {
            return
        }
        val value = currentInput.toDoubleOrNull()
        if (value != null) {
            accumulator = if (accumulator == null || pendingOp == null) {
                value
            } else {
                applyOperation(accumulator!!, value, pendingOp!!)
            }
            if (accumulator == null) {
                showError()
                return
            }
            updateDisplay(formatValue(accumulator!!))
            currentInput = ""
        } else if (accumulator == null) {
            accumulator = 0.0
        }
        pendingOp = op
        justEvaluated = false
    }

    private fun evaluate() {
        if (errorState) {
            return
        }
        val value = currentInput.toDoubleOrNull()
        if (pendingOp != null && value != null && accumulator != null) {
            val result = applyOperation(accumulator!!, value, pendingOp!!)
            if (result == null) {
                showError()
                return
            }
            display.text = formatValue(result)
            accumulator = result
            pendingOp = null
            currentInput = ""
            justEvaluated = true
        } else if (value != null) {
            display.text = formatValue(value)
            accumulator = value
            pendingOp = null
            currentInput = ""
            justEvaluated = true
        }
    }

    private fun applyOperation(left: Double, right: Double, op: Char): Double? {
        return when (op) {
            '+' -> left + right
            '-' -> left - right
            '*' -> left * right
            '/' -> if (right == 0.0) null else left / right
            else -> null
        }
    }

    private fun formatValue(value: Double): String {
        return formatter.format(value)
    }

    private fun updateDisplay(text: String) {
        display.text = if (text.isEmpty()) getString(R.string.display_zero) else text
    }

    private fun clearAll() {
        currentInput = ""
        accumulator = null
        pendingOp = null
        justEvaluated = false
        errorState = false
        display.text = getString(R.string.display_zero)
    }

    private fun showError() {
        display.text = getString(R.string.display_error)
        currentInput = ""
        accumulator = null
        pendingOp = null
        justEvaluated = false
        errorState = true
    }
}
