package com.example.androidhw

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.androidhw.ui.theme.AndroidHWTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository by lazy { ShoppingRepository(this) }
    private var listState by mutableStateOf<List<ShoppingListSummary>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidHWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShoppingListsScreen(
                        lists = listState,
                        onAdd = { openEditList(null) },
                        onOpen = { openProducts(it) },
                        onEdit = { openEditList(it) },
                        onDelete = { deleteList(it) }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadLists()
    }

    private fun loadLists() {
        lifecycleScope.launch(Dispatchers.IO) {
            val data = repository.getListSummaries()
            withContext(Dispatchers.Main) {
                listState = data
            }
        }
    }

    private fun deleteList(listId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            repository.deleteList(listId)
            val data = repository.getListSummaries()
            withContext(Dispatchers.Main) {
                listState = data
            }
        }
    }

    private fun openProducts(listId: Long) {
        val intent = Intent(this, ProductsActivity::class.java).apply {
            putExtra(ProductsActivity.EXTRA_LIST_ID, listId)
        }
        startActivity(intent)
    }

    private fun openEditList(listId: Long?) {
        val intent = Intent(this, EditListActivity::class.java)
        if (listId != null) {
            intent.putExtra(EditListActivity.EXTRA_LIST_ID, listId)
        }
        startActivity(intent)
    }
}

@Composable
private fun ShoppingListsScreen(
    lists: List<ShoppingListSummary>,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.shopping_lists_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onAdd) {
                Text(text = stringResource(R.string.button_add_list))
            }
        }

        if (lists.isEmpty()) {
            Text(text = stringResource(R.string.message_no_lists))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(lists) { item ->
                    ShoppingListCard(item, onOpen, onEdit, onDelete)
                }
            }
        }
    }
}

@Composable
private fun ShoppingListCard(
    item: ShoppingListSummary,
    onOpen: (Long) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = item.name, style = MaterialTheme.typography.titleMedium)
        Text(text = "${stringResource(R.string.label_date)}: ${formatDate(item.date)}")
        if (!item.description.isNullOrBlank()) {
            Text(text = item.description.orEmpty())
        }
        Text(
            text = "${stringResource(R.string.label_total)}: ${item.totalCount} " +
                "${stringResource(R.string.label_bought)}: ${item.boughtCount}"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onOpen(item.id) }) {
                Text(text = stringResource(R.string.button_open))
            }
            Button(onClick = { onEdit(item.id) }) {
                Text(text = stringResource(R.string.button_edit))
            }
            Button(onClick = { onDelete(item.id) }) {
                Text(text = stringResource(R.string.button_delete))
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return format.format(Date(timestamp))
}
