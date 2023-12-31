package com.example.pptcomments.learningGroup

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GroupScreen(navController: NavController, viewModel: GroupViewModel) {
    val groups by viewModel.sortedGroups.collectAsState()
    val state = rememberScaffoldState()

    Scaffold(
        scaffoldState = state,
        topBar = { TopAppBar(title = { Text("课程小组") }) },
        content = {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { navController.navigate("createGroup") }) {
                        Text("创建小组")
                    }
                    Button(onClick = { navController.navigate("joinGroup") }) {
                        Text("加入小组")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("已加入的小组", style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                GroupList(groups, navController, viewModel)
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupList(groups: List<CourseGroup>, navController: NavController, viewModel: GroupViewModel) {
    LazyColumn {
        items(groups) { group ->
            ListItem(
                text = { Text(group.name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.updateGroupLastAccessedTime(group.id)
                        navController.navigate("groupDetail/${group.id}")
                    }
                    .padding(16.dp)
            )
            Divider()
        }
    }
}





