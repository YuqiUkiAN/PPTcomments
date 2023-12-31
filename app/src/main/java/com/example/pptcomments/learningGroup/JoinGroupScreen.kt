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
fun JoinGroupScreen(viewModel: GroupViewModel) {
    var groupId by remember { mutableStateOf("") }
    var groupName by remember { mutableStateOf("") }
    var groupLink by remember { mutableStateOf("") }
    var joining by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = groupId,
            onValueChange = { groupId = it },
            label = { Text("小组ID") },
            enabled = !joining
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = groupName,
            onValueChange = { groupName = it },
            label = { Text("小组名称") },
            enabled = !joining
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = groupLink,
            onValueChange = { groupLink = it },
            label = { Text("小组链接") },
            enabled = !joining
        )
        errorMsg?.let {
            Text(it, color = MaterialTheme.colors.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                joining = true
                errorMsg = null
                viewModel.joinGroup(groupId, groupName, groupLink,
                    onSuccess = {
                        joining = false
                        // 你可能想在这里添加一些导航逻辑，比如返回小组列表
                    },
                    onError = { e ->
                        joining = false
                        errorMsg = e.localizedMessage ?: "Error joining group"
                    }
                )
            },
            enabled = groupId.isNotBlank() && groupName.isNotBlank() && groupLink.isNotBlank() && !joining
        ) {
            if (joining) {
                Text("Joining...")
            } else {
                Text("加入小组")
            }
        }
    }
}
