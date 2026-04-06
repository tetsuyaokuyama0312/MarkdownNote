package com.to.markdownnote.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.to.markdownnote.feature.memo.editor.MemoEditorScreen
import com.to.markdownnote.feature.memo.list.MemoListScreen
import kotlinx.serialization.Serializable

@Serializable
object MemoListRoute

@Serializable
data class MemoEditorRoute(val memoId: Int = -1)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MemoListRoute,
    ) {
        composable<MemoListRoute> {
            MemoListScreen(
                onMemoClick = { memo ->
                    navController.navigate(MemoEditorRoute(memoId = memo.id))
                },
                onNewMemo = {
                    navController.navigate(MemoEditorRoute())
                },
            )
        }

        composable<MemoEditorRoute> { backStackEntry ->
            val route: MemoEditorRoute = backStackEntry.toRoute()
            MemoEditorScreen(
                memoId = route.memoId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
