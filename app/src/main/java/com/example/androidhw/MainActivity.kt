package com.example.androidhw

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {
    private lateinit var listView: ListView
    private lateinit var emptyView: TextView
    private lateinit var adapter: CarAdapter
    private val allCars: List<CarModel> by lazy { (application as CarApp).cars }

    private val searchLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val ids = result.data?.getIntArrayExtra(SearchActivity.EXTRA_MATCH_IDS)
            if (ids != null) {
                val filtered = allCars.filter { ids.contains(it.id) }
                updateList(filtered)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setActionBar(toolbar)
        title = getString(R.string.title_cars)

        listView = findViewById(R.id.list_cars)
        emptyView = findViewById(R.id.text_empty)
        listView.emptyView = emptyView

        updateList(allCars)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                searchLauncher.launch(Intent(this, SearchActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateList(cars: List<CarModel>) {
        adapter = CarAdapter(this, cars)
        listView.adapter = adapter
    }
}
