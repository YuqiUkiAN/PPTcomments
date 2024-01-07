package com.example.pptcomments.mainActivityUI



import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.pptcomments.commentS.PPTViewModel
import com.example.pptcomments.commentS.PPTViewerScreen
import com.example.pptcomments.learningGroup.CreateGroupScreen
import com.example.pptcomments.learningGroup.GroupDetailScreen
import com.example.pptcomments.learningGroup.GroupScreen
import com.example.pptcomments.learningGroup.GroupViewModel
import com.example.pptcomments.learningGroup.JoinGroupScreen
import com.example.pptcomments.uploadAndShare.SharePPTViewModel
import com.example.pptcomments.uploadAndShare.UploadPPTScreen

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val groupViewModel: GroupViewModel = viewModel()
    val sharePPTViewModel: SharePPTViewModel = viewModel()
    val pptViewModel: PPTViewModel = viewModel()

    NavHost(navController = navController, startDestination = "groupScreen") {
        composable("groupScreen") { GroupScreen(navController, groupViewModel) }
        composable("createGroup") { CreateGroupScreen(groupViewModel,navController) }
        composable("joinGroup") { JoinGroupScreen(groupViewModel,navController) }
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
                navController = navController,
                defaultGroupId = backStackEntry.arguments?.getString("groupId")
            )
        }
        // PPTViewerScreen 路由
        composable("pptViewer/{pptId}") { backStackEntry ->
            PPTViewerScreen(
                pptId = backStackEntry.arguments?.getString("pptId") ?: "",
                viewModel = pptViewModel
            )
        }

        // 添加其他必要的路由
    }
}