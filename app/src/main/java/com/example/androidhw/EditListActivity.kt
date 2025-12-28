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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

class EditListActivity : ComponentActivity() {
    private val repository by lazy { ShoppingRepository(this) }
    private var nameState by mutableStateOf("")
    private var descriptionState by mutableStateOf("")
    private var errorState by mutableStateOf("")
    private var listId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listId = intent.getLongExtra(EXTRA_LIST_ID, -1)
        if (listId > 0) {
            loadList()
        }
        setContent {
            AndroidHWTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EditListScreen(
                        name = nameState,
                        description = descriptionState,
                        errorMessage = errorState,
                        onNameChange = { nameState = it },
                        onDescriptionChange = { descriptionState = it },
                        onSave = { saveList() },
                        onCancel = { finish() }
                    )
                }
            }
        }
    }

    private fun loadList() {
        lifecycleScope.launch(Dispatchers.IO) {
            val list = repository.getList(listId)
            withContext(Dispatchers.Main) {
                if (list != null) {
                    nameState = list.name
                    descriptionState = list.description.orEmpty()
                }
            }
        }
    }

    private fun saveList() {
        if (nameState.isBlank()) {
            errorState = getString(R.string.error_name_required)
            return
        }
        val description = descriptionState.trim().ifEmpty { null }
        lifecycleScope.launch(Dispatchers.IO) {
            if (listId > 0) {
                repository.updateList(listId, nameState.trim(), description)
            } else {
                repository.insertList(nameState.trim(), description)
            }
            withContext(Dispatchers.Main) {
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_LIST_ID = "extra_list_id"
    }
}

@Composable
private fun EditListScreen(
    name: String,
    description: String,
    errorMessage: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.title_edit_list),
            style = MaterialTheme.typography.headlineSmall
        )
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(text = stringResource(R.string.label_list_name)) }
        )
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(text = stringResource(R.string.label_list_description)) }
        )
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
