package com.example.pptcomments.learningGroup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun JoinGroupScreen(viewModel: GroupViewModel, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var joining by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val searchResults by viewModel.searchResults.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search by ID or Name") },
                modifier = Modifier.weight(1f),
                enabled = !joining
            )
            IconButton(onClick = { viewModel.searchGroups(searchQuery) }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            }
        }

        errorMsg?.let {
            Text(it, color = MaterialTheme.colors.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn {
            items(searchResults.size) { index ->
                val group = searchResults[index]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${group.name} (ID: ${group.id}) - Created by ${group.creator}", modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            joining = true
                            viewModel.joinGroup(group.id, group.name, onSuccess = {
                                joining = false
                                // Handle successful join
                                navController.popBackStack()
                            }, onError = { e ->
                                joining = false
                                errorMsg = e.localizedMessage ?: "Error joining group"
                            })
                        },
                        enabled = !joining
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Join")
                    }
                }
            }
        }
    }
}



