package com.example.pptcomments.learningGroup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CreateGroupScreen(viewModel: GroupViewModel, navController: NavController) {
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    var creating by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Group") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                TextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    enabled = !creating
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = groupDescription,
                    onValueChange = { groupDescription = it },
                    label = { Text("Group Description") },
                    enabled = !creating
                )
                errorMsg?.let {
                    Text(it, color = MaterialTheme.colors.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        creating = true
                        errorMsg = null
                        viewModel.createGroup(
                            groupName,
                            groupDescription,
                            onSuccess = {
                                creating = false
                                navController.popBackStack()
                            },
                            onError = { e ->
                                creating = false
                                errorMsg = e.localizedMessage ?: "Error creating group"
                            }
                        )
                    },
                    enabled = groupName.isNotBlank() && groupDescription.isNotBlank() && !creating
                ) {
                    if (creating) {
                        Text("Creating...")
                    } else {
                        Text("Create Group")
                    }
                }
            }
        }
    )
}