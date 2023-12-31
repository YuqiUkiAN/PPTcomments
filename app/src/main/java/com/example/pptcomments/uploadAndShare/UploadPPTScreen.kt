package com.example.pptcomments.uploadAndShare

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pptcomments.learningGroup.GroupViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UploadPPTScreen(viewModel: SharePPTViewModel, groupViewModel: GroupViewModel, defaultGroupId: String? = null) {
    var pptLink by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableStateOf(defaultGroupId) }
    var isSharing by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val groups by groupViewModel.sortedGroups.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedGroupName by remember { mutableStateOf(defaultGroupId?.let { "Select a Group" } ?: "No Group Selected") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = pptLink,
            onValueChange = { pptLink = it },
            label = { Text("PPT Link") },
            modifier = Modifier.fillMaxWidth()
        )

        // 小组选择器
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                readOnly = true,
                value = selectedGroupName,
                onValueChange = { },
                label = { Text("Group") },
                trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Expand") },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                groups.forEach { group ->
                    DropdownMenuItem(onClick = {
                        selectedGroupId = group.id
                        selectedGroupName = group.name
                        expanded = false
                    }) {
                        Text(group.name)
                    }
                }
            }
        }

        // 分享按钮
        Button(
            onClick = {
                if (selectedGroupId != null) {
                    isSharing = true
                    viewModel.sharePPT(pptLink, selectedGroupId!!) { success, errorMsg ->
                        isSharing = false
                        message = errorMsg ?: if (success) "Shared Successfully" else "Share Failed"
                    }
                } else {
                    message = "Please select a group"
                }
            },
            enabled = pptLink.isNotBlank() && selectedGroupId != null && !isSharing,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text(if (isSharing) "Sharing..." else "Share")
        }

        message?.let {
            Text(it, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
