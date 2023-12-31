package com.example.pptcomments

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.pptcomments.learningGroup.*
import com.example.pptcomments.uploadAndShare.SharePPTViewModel
import com.example.pptcomments.uploadAndShare.UploadPPTScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = Firebase.auth

        setContent {
            val isLoggingIn = remember { mutableStateOf(true) }
            val loginFailed = remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                auth.signInAnonymously().addOnCompleteListener { task ->
                    isLoggingIn.value = false
                    loginFailed.value = !task.isSuccessful
                }
            }

            if (isLoggingIn.value) {
                CircularProgressIndicator()
            } else if (loginFailed.value) {
                LoginFailedScreen {
                    isLoggingIn.value = true
                    loginFailed.value = false
                }
            } else {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val groupViewModel: GroupViewModel = viewModel()
    val sharePPTViewModel: SharePPTViewModel = viewModel()

    NavHost(navController = navController, startDestination = "groupScreen") {
        composable("groupScreen") { GroupScreen(navController, groupViewModel) }
        composable("createGroup") { CreateGroupScreen(groupViewModel) }
        composable("joinGroup") { JoinGroupScreen(groupViewModel) }
        composable("groupDetail/{groupId}") { backStackEntry ->
            GroupDetailScreen(
                navController = navController,
                viewModel = groupViewModel,
                groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            )
        }
        composable("UploadPPTScreen/{groupId}") { backStackEntry ->
            UploadPPTScreen(
                viewModel = sharePPTViewModel,
                groupViewModel = groupViewModel,
                defaultGroupId = backStackEntry.arguments?.getString("groupId")
            )
        }
        // 添加其他必要的路由
    }
}

@Composable
fun LoadingScreen() {
    // 显示一个加载动画或信息
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text("Logging in...")
    }
}

@Composable
fun LoginFailedScreen(retryLogin: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Login failed. Please check your network connection.")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = retryLogin) {
            Text("Retry")
        }
    }
}
