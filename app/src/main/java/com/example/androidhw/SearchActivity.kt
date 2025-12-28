package com.example.androidhw

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity

class SearchActivity : ComponentActivity() {
    private lateinit var autoBrand: AutoCompleteTextView
    private lateinit var autoModel: AutoCompleteTextView
    private lateinit var spinnerYearFrom: Spinner
    private lateinit var spinnerYearTo: Spinner
    private lateinit var spinnerCostFrom: Spinner
    private lateinit var spinnerCostTo: Spinner
    private lateinit var textMatches: TextView
    private lateinit var buttonMatches: Button

    private lateinit var yearValues: List<Int?>
    private lateinit var costValues: List<Int?>
    private var currentMatches: List<CarModel> = emptyList()

    private val cars: List<CarModel> by lazy { (application as CarApp).cars }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        autoBrand = findViewById(R.id.auto_brand)
        autoModel = findViewById(R.id.auto_model)
        spinnerYearFrom = findViewById(R.id.spinner_year_from)
        spinnerYearTo = findViewById(R.id.spinner_year_to)
        spinnerCostFrom = findViewById(R.id.spinner_cost_from)
        spinnerCostTo = findViewById(R.id.spinner_cost_to)
        textMatches = findViewById(R.id.text_matches)
        buttonMatches = findViewById(R.id.button_matches)

        setupAutoComplete()
        setupSpinners()
        setupListeners()
        updateMatches()

        buttonMatches.setOnClickListener {
            val ids = currentMatches.map { it.id }.toIntArray()
            val data = android.content.Intent().apply {
                putExtra(EXTRA_MATCH_IDS, ids)
            }
            setResult(Activity.RESULT_OK, data)
            finish()
        }
    }

    private fun setupAutoComplete() {
        val brands = cars.map { it.brand }.toSet().sorted()
        val models = cars.map { it.model }.toSet().sorted()

        autoBrand.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, brands.toList()))
        autoModel.setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, models.toList()))

        autoBrand.threshold = 1
        autoModel.threshold = 1
    }

    private fun setupSpinners() {
        val years = cars.map { it.year }.toSet().sorted()
        yearValues = listOf(null) + years
        val yearLabels = listOf(getString(R.string.option_any)) + years.map { it.toString() }
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearLabels).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerYearFrom.adapter = yearAdapter
        spinnerYearTo.adapter = yearAdapter

        costValues = listOf(null, 1000, 10000, 100000, 1000000)
        val costLabels = costValues.map { value ->
            if (value == null) getString(R.string.option_any) else getString(R.string.cost_option_format, value)
        }
        val costAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, costLabels).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCostFrom.adapter = costAdapter
        spinnerCostTo.adapter = costAdapter
    }

    private fun setupListeners() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                updateMatches()
            }
        }
        autoBrand.addTextChangedListener(watcher)
        autoModel.addTextChangedListener(watcher)

        val spinnerListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                updateMatches()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        spinnerYearFrom.onItemSelectedListener = spinnerListener
        spinnerYearTo.onItemSelectedListener = spinnerListener
        spinnerCostFrom.onItemSelectedListener = spinnerListener
        spinnerCostTo.onItemSelectedListener = spinnerListener
    }

    private fun updateMatches() {
        val brandQuery = autoBrand.text.toString().trim()
        val modelQuery = autoModel.text.toString().trim()
        val yearFrom = yearValues[spinnerYearFrom.selectedItemPosition]
        val yearTo = yearValues[spinnerYearTo.selectedItemPosition]
        val costFrom = costValues[spinnerCostFrom.selectedItemPosition]
        val costTo = costValues[spinnerCostTo.selectedItemPosition]

        val hasFilter = brandQuery.isNotEmpty() || modelQuery.isNotEmpty() ||
            yearFrom != null || yearTo != null || costFrom != null || costTo != null

        currentMatches = if (!hasFilter) {
            emptyList()
        } else if (yearFrom != null && yearTo != null && yearFrom > yearTo) {
            emptyList()
        } else if (costFrom != null && costTo != null && costFrom > costTo) {
            emptyList()
        } else {
            cars.filter { car ->
                (brandQuery.isEmpty() || car.brand.startsWith(brandQuery, ignoreCase = true)) &&
                    (modelQuery.isEmpty() || car.model.startsWith(modelQuery, ignoreCase = true)) &&
                    (yearFrom == null || car.year >= yearFrom) &&
                    (yearTo == null || car.year <= yearTo) &&
                    (costFrom == null || car.cost >= costFrom) &&
                    (costTo == null || car.cost <= costTo)
            }
        }

        textMatches.text = getString(R.string.matches_format, currentMatches.size)
        buttonMatches.isEnabled = hasFilter && currentMatches.isNotEmpty()
    }

    companion object {
        const val EXTRA_MATCH_IDS = "com.example.androidhw.EXTRA_MATCH_IDS"
    }
}
