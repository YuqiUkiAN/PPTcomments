package com.example.pptcomments.learningGroup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupDetailScreen(navController: NavController, viewModel: GroupViewModel, groupId: String) {
    val group by viewModel.getGroupById(groupId).collectAsState(initial = null)
    val members by viewModel.getGroupMembers(groupId).collectAsState(initial = listOf())
    val currentUser = viewModel.currentUserId
    var showMembersDialog by remember { mutableStateOf(false) }
    val ppts by viewModel.getGroupPPTs(groupId).collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(group?.name ?: "Loading...") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("UploadPPTScreen/$groupId") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add PPT")
            }
        },
        content = { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
                Text("Group ID: ${group?.id ?: "Loading..."}", style = MaterialTheme.typography.h6)
                Text("Description: ${group?.description ?: "Loading..."}", style = MaterialTheme.typography.body1)

                Spacer(modifier = Modifier.height(16.dp))

                // 成员列表和管理
                Button(onClick = { showMembersDialog = true }) {
                    Icon(imageVector = Icons.Default.People, contentDescription = "View Members")
                    Spacer(Modifier.width(8.dp))
                    Text("View Members")
                }

                if (showMembersDialog) {
                    AlertDialog(
                        onDismissRequest = { showMembersDialog = false },
                        title = { Text("Group Members") },
                        text = {
                            Column {
                                members.forEach { memberId ->
                                    Text(memberId)
                                    if (group?.creator == currentUser && memberId != currentUser) {
                                        IconButton(onClick = { viewModel.removeMemberFromGroup(groupId, memberId) { _, _ -> } }) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove")
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showMembersDialog = false }) {
                                Text("Close")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 退出小组按钮
                Button(onClick = { viewModel.leaveGroup(groupId) { _, _ -> } }) {
                    Text("Exit Group")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 小组内 PPT 列表
                Text("Group PPTs", style = MaterialTheme.typography.h6)
                LazyColumn {
                    items(ppts.size) { index ->
                        val ppt = ppts[index]
                        ListItem(
                            text = { Text(ppt.link) },
                            secondaryText = { Text("Uploaded by: ${ppt.uploaderId}") },
                            modifier = Modifier.clickable {
                                /// TODO: 导航到 PPTViewerActivity 页面，传递 PPT 链接
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    )
}
