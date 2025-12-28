package com.example.androidhw

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private lateinit var editFullName: EditText
    private lateinit var editAge: EditText
    private lateinit var seekSalary: SeekBar
    private lateinit var textSalaryValue: TextView
    private lateinit var textSalaryRequirement: TextView
    private lateinit var buttonSubmit: Button
    private lateinit var textResult: TextView
    private lateinit var contactsLayout: LinearLayout
    private lateinit var groupQuestion1: RadioGroup
    private lateinit var groupQuestion2: RadioGroup
    private lateinit var groupQuestion3: RadioGroup
    private lateinit var groupQuestion4: RadioGroup
    private lateinit var groupQuestion5: RadioGroup
    private lateinit var checkExperience: CheckBox
    private lateinit var checkTeamwork: CheckBox
    private lateinit var checkTesting: CheckBox
    private lateinit var checkTrips: CheckBox

    private val minAge = 21
    private val maxAge = 40
    private val salaryMin = 500
    private val salaryMax = 5000
    private val requirementMinSalary = 1500
    private val requirementMaxSalary = 3500
    private val pointsPerQuestion = 2
    private val passScore = 10
    private val experiencePoints = 2
    private val testingPoints = 1
    private val tripsPoints = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editFullName = findViewById(R.id.edit_full_name)
        editAge = findViewById(R.id.edit_age)
        seekSalary = findViewById(R.id.seek_salary)
        textSalaryValue = findViewById(R.id.text_salary_value)
        textSalaryRequirement = findViewById(R.id.text_salary_requirement)
        buttonSubmit = findViewById(R.id.button_submit)
        textResult = findViewById(R.id.text_result)
        contactsLayout = findViewById(R.id.layout_contacts)
        groupQuestion1 = findViewById(R.id.group_question_1)
        groupQuestion2 = findViewById(R.id.group_question_2)
        groupQuestion3 = findViewById(R.id.group_question_3)
        groupQuestion4 = findViewById(R.id.group_question_4)
        groupQuestion5 = findViewById(R.id.group_question_5)
        checkExperience = findViewById(R.id.check_experience)
        checkTeamwork = findViewById(R.id.check_teamwork)
        checkTesting = findViewById(R.id.check_testing)
        checkTrips = findViewById(R.id.check_trips)

        setupSalary()
        setupValidation()

        buttonSubmit.setOnClickListener { evaluateCandidate() }
    }

    private fun setupSalary() {
        seekSalary.max = salaryMax - salaryMin
        seekSalary.progress = (requirementMinSalary - salaryMin).coerceIn(0, seekSalary.max)
        textSalaryRequirement.text = getString(
            R.string.salary_requirement,
            requirementMinSalary,
            requirementMaxSalary
        )
        updateSalaryText(salaryMin + seekSalary.progress)

        seekSalary.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateSalaryText(salaryMin + progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                updateSubmitEnabled()
            }
        }
        editFullName.addTextChangedListener(watcher)
        editAge.addTextChangedListener(watcher)
        updateSubmitEnabled()
    }

    private fun updateSubmitEnabled() {
        val isNameValid = validateFullName(showError = editFullName.text.isNotBlank())
        val isAgeValid = validateAge(showError = editAge.text.isNotBlank())
        buttonSubmit.isEnabled = isNameValid && isAgeValid
    }

    private fun validateFullName(showError: Boolean): Boolean {
        val name = editFullName.text.toString().trim()
        val parts = name.split(Regex("\\s+")).filter { it.isNotBlank() }
        val valid = parts.size >= 2 && parts.all { part ->
            part.length >= 2 && part.matches(Regex("[\\p{L}'-]+"))
        }
        editFullName.error = if (!showError) null else if (valid) null else {
            getString(R.string.error_full_name)
        }
        return valid
    }

    private fun validateAge(showError: Boolean): Boolean {
        val ageText = editAge.text.toString()
        val age = ageText.toIntOrNull()
        val valid = age != null && age in minAge..maxAge
        editAge.error = if (!showError) null else if (valid) null else {
            getString(R.string.error_age_range, minAge, maxAge)
        }
        return valid
    }

    private fun updateSalaryText(value: Int) {
        textSalaryValue.text = getString(R.string.salary_value_format, value)
    }

    private fun evaluateCandidate() {
        val reasons = mutableListOf<String>()
        val age = editAge.text.toString().toIntOrNull()
        if (age == null || age !in minAge..maxAge) {
            reasons.add(getString(R.string.requirement_age, minAge, maxAge))
        }
        val salary = salaryMin + seekSalary.progress
        if (salary !in requirementMinSalary..requirementMaxSalary) {
            reasons.add(getString(R.string.requirement_salary, requirementMinSalary, requirementMaxSalary))
        }

        if (reasons.isNotEmpty()) {
            showResult(false, getString(R.string.result_failed_requirements, reasons.joinToString("; ")))
            return
        }

        val score = calculateScore()
        if (score >= passScore) {
            showResult(true, getString(R.string.result_passed_format, score))
        } else {
            showResult(false, getString(R.string.result_failed_score_format, score, passScore))
        }
    }

    private fun calculateScore(): Int {
        var score = 0
        if (groupQuestion1.checkedRadioButtonId == R.id.answer_1_a) {
            score += pointsPerQuestion
        }
        if (groupQuestion2.checkedRadioButtonId == R.id.answer_2_a) {
            score += pointsPerQuestion
        }
        if (groupQuestion3.checkedRadioButtonId == R.id.answer_3_b) {
            score += pointsPerQuestion
        }
        if (groupQuestion4.checkedRadioButtonId == R.id.answer_4_a) {
            score += pointsPerQuestion
        }
        if (groupQuestion5.checkedRadioButtonId == R.id.answer_5_b) {
            score += pointsPerQuestion
        }
        if (checkExperience.isChecked) {
            score += experiencePoints
        }
        if (checkTesting.isChecked) {
            score += testingPoints
        }
        if (checkTrips.isChecked) {
            score += tripsPoints
        }
        return score
    }

    private fun showResult(passed: Boolean, message: String) {
        textResult.text = message
        textResult.visibility = View.VISIBLE
        val colorId = if (passed) R.color.result_success else R.color.result_failure
        textResult.setTextColor(ContextCompat.getColor(this, colorId))
        contactsLayout.visibility = if (passed) View.VISIBLE else View.GONE
    }
}
