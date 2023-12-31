package com.example.pptcomments

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.pptcomments.learningGroup.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val viewModel: GroupViewModel = viewModel()

    NavHost(navController = navController, startDestination = "groupScreen") {
        composable("groupScreen") { GroupScreen(navController, viewModel) }
        composable("createGroup") { CreateGroupScreen(viewModel) }
        composable("joinGroup") { JoinGroupScreen(viewModel) }
        // 为其他目标添加路由和逻辑
    }
}
