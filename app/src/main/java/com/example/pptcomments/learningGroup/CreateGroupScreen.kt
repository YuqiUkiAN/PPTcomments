package com.example.pptcomments.learningGroup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateGroupScreen(viewModel: GroupViewModel) {
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    var creating by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("小组名称") },
            enabled = !creating
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = groupDescription,
            onValueChange = { groupDescription = it },
            label = { Text("小组描述") },
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
                    onSuccess = { creating = false },
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
                Text("创建小组")
            }
        }
    }
}

