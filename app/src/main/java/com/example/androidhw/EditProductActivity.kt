package com.example.androidhw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.androidhw.ui.theme.AndroidHWTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.clickable
import androidx.compose.material3.DropdownMenu

class EditProductActivity : ComponentActivity() {
    private val repository by lazy { ShoppingRepository(this) }
    private var listId: Long = -1
    private var productId: Long = -1

    private var nameState by mutableStateOf("")
    private var quantityState by mutableStateOf("")
    private var isBoughtState by mutableStateOf(false)
    private var errorState by mutableStateOf("")
    private var typesState by mutableStateOf<List<TypeUnit>>(emptyList())
    private var selectedTypeIdState by mutableStateOf<Long?>(null)
    private var suggestionsState by mutableStateOf<List<String>>(emptyList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listId = intent.getLongExtra(EXTRA_LIST_ID, -1)
        productId = intent.getLongExtra(EXTRA_PRODUCT_ID, -1)
        if (listId == -1L) {
            finish()
            return
        }
        loadData()
        setContent {
            AndroidHWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EditProductScreen(
                        name = nameState,
                        quantity = quantityState,
                        isBought = isBoughtState,
                        types = typesState,
                        selectedTypeId = selectedTypeIdState,
                        suggestions = suggestionsState,
                        errorMessage = errorState,
                        onNameChange = { nameState = it },
                        onQuantityChange = { quantityState = it },
                        onTypeSelect = { selectedTypeIdState = it },
                        onBoughtChange = { isBoughtState = it },
                        onSave = { saveProduct() },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }

    private fun loadData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val types = repository.getTypes()
            val suggestions = repository.getSuggestions()
            val product = if (productId > 0) repository.getProduct(productId) else null
            withContext(Dispatchers.Main) {
                typesState = types
                suggestionsState = suggestions
                if (product != null) {
                    nameState = product.name
                    quantityState = product.quantity?.toString().orEmpty()
                    isBoughtState = product.isBought
                    selectedTypeIdState = product.type.id
                } else {
                    if (selectedTypeIdState == null && types.isNotEmpty()) {
                        selectedTypeIdState = types.first().id
                    }
                }
            }
        }
    }

    private fun saveProduct() {
        val name = nameState.trim()
        if (name.isBlank()) {
            errorState = getString(R.string.error_name_required)
            return
        }
        val typeId = selectedTypeIdState
        val type = typesState.firstOrNull { it.id == typeId }
        if (type == null) {
            errorState = getString(R.string.error_type_required)
            return
        }
        val quantityText = quantityState.trim()
        val quantityValue = if (quantityText.isEmpty()) {
            null
        } else {
            if (type.rule == "int") {
                quantityText.toIntOrNull()?.toDouble()
            } else {
                quantityText.toDoubleOrNull()
            }
        }
        if (quantityText.isNotEmpty() && quantityValue == null) {
            errorState = getString(R.string.error_quantity_invalid)
            return
        }
        lifecycleScope.launch(Dispatchers.IO) {
            if (productId > 0) {
                repository.updateProduct(productId, name, quantityValue, type.id, isBoughtState)
            } else {
                repository.insertProduct(listId, name, quantityValue, type.id, isBoughtState)
            }
            withContext(Dispatchers.Main) {
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_LIST_ID = "extra_list_id"
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}

@Composable
private fun EditProductScreen(
    name: String,
    quantity: String,
    isBought: Boolean,
    types: List<TypeUnit>,
    selectedTypeId: Long?,
    suggestions: List<String>,
    errorMessage: String,
    onNameChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onTypeSelect: (Long) -> Unit,
    onBoughtChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var nameExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    val filtered = if (name.isBlank()) {
        emptyList()
    } else {
        suggestions
            .filter { it.contains(name, ignoreCase = true) && it != name }
            .take(6)
    }
    val selectedType = types.firstOrNull { it.id == selectedTypeId }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.title_edit_product),
            style = MaterialTheme.typography.headlineSmall
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    onNameChange(it)
                    nameExpanded = true
                },
                label = { Text(text = stringResource(R.string.label_product_name)) },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = nameExpanded && filtered.isNotEmpty(),
                onDismissRequest = { nameExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                filtered.forEach { suggestion ->
                    DropdownMenuItem(
                        text = { Text(text = suggestion) },
                        onClick = {
                            onNameChange(suggestion)
                            nameExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = quantity,
            onValueChange = onQuantityChange,
            label = { Text(text = stringResource(R.string.label_quantity)) }
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedType?.label.orEmpty(),
                onValueChange = {},
                readOnly = true,
                label = { Text(text = stringResource(R.string.label_unit)) },
                modifier = Modifier.fillMaxWidth().clickable { typeExpanded = true }
            )
            DropdownMenu(
                expanded = typeExpanded,
                onDismissRequest = { typeExpanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                types.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(text = type.label) },
                        onClick = {
                            onTypeSelect(type.id)
                            typeExpanded = false
                        }
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Checkbox(checked = isBought, onCheckedChange = onBoughtChange)
            Text(text = stringResource(R.string.label_bought_status))
        }

        if (errorMessage.isNotBlank()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSave) {
                Text(text = stringResource(R.string.button_save))
            }
            Button(onClick = onCancel) {
                Text(text = stringResource(R.string.button_cancel))
            }
        }
    }
}
