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
import com.example.pptcomments.uploadAndShare.SharePPTViewModel
import com.example.pptcomments.uploadAndShare.UploadPPTScreen

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
