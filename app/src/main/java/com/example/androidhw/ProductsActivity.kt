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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.androidhw.ui.theme.AndroidHWTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.Composable

class ProductsActivity : ComponentActivity() {
    private val repository by lazy { ShoppingRepository(this) }
    private var listTitle by mutableStateOf("")
    private var products by mutableStateOf<List<ProductItem>>(emptyList())
    private var listId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listId = intent.getLongExtra(EXTRA_LIST_ID, -1)
        if (listId == -1L) {
            finish()
            return
        }
        setContent {
            AndroidHWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProductsScreen(
                        listTitle = listTitle,
                        items = products,
                        onAdd = { openEditProduct(null) },
                        onEdit = { openEditProduct(it) },
                        onDelete = { deleteProduct(it) },
                        onToggleBought = { id, bought -> updateBought(id, bought) }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = repository.getList(listId)
            val items = repository.getProducts(listId)
            withContext(Dispatchers.Main) {
                listTitle = list?.name ?: ""
                products = items
            }
        }
    }

    private fun openEditProduct(productId: Long?) {
        val intent = Intent(this, EditProductActivity::class.java).apply {
            putExtra(EditProductActivity.EXTRA_LIST_ID, listId)
            if (productId != null) {
                putExtra(EditProductActivity.EXTRA_PRODUCT_ID, productId)
            }
        }
        startActivity(intent)
    }

    private fun deleteProduct(productId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            repository.deleteProduct(productId)
            val items = repository.getProducts(listId)
            withContext(Dispatchers.Main) {
                products = items
            }
        }
    }

    private fun updateBought(productId: Long, bought: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            repository.updateProductBought(productId, bought)
            val items = repository.getProducts(listId)
            withContext(Dispatchers.Main) {
                products = items
            }
        }
    }

    companion object {
        const val EXTRA_LIST_ID = "extra_list_id"
    }
}

@Composable
private fun ProductsScreen(
    listTitle: String,
    items: List<ProductItem>,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onToggleBought: (Long, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = if (listTitle.isNotBlank()) listTitle else stringResource(R.string.products_title),
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = onAdd) {
                Text(text = stringResource(R.string.button_add_product))
            }
        }

        if (items.isEmpty()) {
            Text(text = stringResource(R.string.message_no_products))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items) { item ->
                    ProductRow(item, onEdit, onDelete, onToggleBought)
                }
            }
        }
    }
}

@Composable
private fun ProductRow(
    item: ProductItem,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    onToggleBought: (Long, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = item.name, style = MaterialTheme.typography.titleMedium)
        Text(text = "${stringResource(R.string.label_quantity)}: ${formatQuantity(item)}")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Checkbox(
                checked = item.isBought,
                onCheckedChange = { onToggleBought(item.id, it) }
            )
            Text(text = stringResource(R.string.label_bought_status))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onEdit(item.id) }) {
                Text(text = stringResource(R.string.button_edit))
            }
            Button(onClick = { onDelete(item.id) }) {
                Text(text = stringResource(R.string.button_delete))
            }
        }
    }
}

private fun formatQuantity(item: ProductItem): String {
    val quantity = item.quantity
    if (quantity == null) return "-"
    val formatted = if (item.type.rule == "int") {
        quantity.toInt().toString()
    } else {
        "%.2f".format(quantity)
    }
    return "$formatted ${item.type.label}"
}
