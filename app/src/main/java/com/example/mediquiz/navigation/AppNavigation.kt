package com.example.mediquiz.navigation

import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // Ensure this import is present if using 'by' delegate
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mediquiz.HomeScreenMenu
import com.example.mediquiz.R
import com.example.mediquiz.Routes
import com.example.mediquiz.ui.main.MainViewModel
import com.example.mediquiz.ui.main.MakeQuizScreen
import com.example.mediquiz.ui.review.ReviewIncorrectDetailScreen
import com.example.mediquiz.ui.review.ReviewIncorrectListScreen
import com.example.mediquiz.ui.review.ReviewViewModel
import com.example.mediquiz.ui.statistics.StatisticsScreen
import com.example.mediquiz.ui.subjectfilter.SubjectFilterScreen
import com.example.mediquiz.ui.subjectfilter.SubjectFilterViewModel

fun exitAnimation(): ExitTransition {
    return slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))
}
fun enterAnimation(): EnterTransition {
    return slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))
}
@Composable
fun MediQuizNavHost(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    reviewViewModel: ReviewViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HomeScreenRoute,
        modifier = modifier
    ) {
        composable(Routes.HomeScreenRoute) {
            HomeScreenMenu(
                navController = navController,
                mainViewModel = mainViewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = Routes.QuizScreenRoute +
                    "?useAllSubjects={useAllSubjects}" +
                    "&${Routes.QuizScreenQuestionIdsArg}={${Routes.QuizScreenQuestionIdsArg}}",
            arguments = listOf(
                navArgument("useAllSubjects") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument(Routes.QuizScreenQuestionIdsArg) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = { enterAnimation() },
            exitTransition = { exitAnimation() },
            popEnterTransition = { enterAnimation() },
            popExitTransition = { exitAnimation() },
        ) { backStackEntry ->
            val useAllSubjects = backStackEntry.arguments?.getBoolean("useAllSubjects") ?: false
            val questionIdsString = backStackEntry.arguments?.getString(Routes.QuizScreenQuestionIdsArg)

            LaunchedEffect(key1 = questionIdsString, key2 = useAllSubjects) {
                Log.d("QuizScreenNav", "LaunchedEffect in NavHost: questionIdsString = $questionIdsString, useAllSubjects = $useAllSubjects")
                mainViewModel.prepareQuiz(questionIdsString, useAllSubjects)
            }

            MakeQuizScreen(
                viewModel = mainViewModel,
                modifier = Modifier.fillMaxSize(),
                onNavigateHome = {
                    navController.navigate(Routes.HomeScreenRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }
        composable(
            Routes.SubjectFilterScreenRoute,
            enterTransition = { enterAnimation() },
            exitTransition = { exitAnimation() },
            popEnterTransition = { enterAnimation() },
            popExitTransition = { exitAnimation() },
        ) {
            val subjectFilterViewModel: SubjectFilterViewModel = hiltViewModel()

            val currentSelectedExam = mainViewModel.selectedExam.collectAsState().value
            val currentSelectedCount = mainViewModel.selectedCount.collectAsState().value
            val initialFilters by mainViewModel.appliedSubjectFilters.collectAsState()

            SubjectFilterScreen(
                subjectFilterViewModel = subjectFilterViewModel,
                selectedExam = currentSelectedExam,
                onExamSelected = { newExam ->
                    mainViewModel.updateSelectedExam(newExam)
                },
                currentSelectedCount = currentSelectedCount,
                onCountChanged = { newCount ->
                    mainViewModel.updateSelectedCount(newCount)
                },
                initialSelectedFilters = initialFilters,
                onApplyFilters = { filters ->
                    mainViewModel.applySubjectFilters(filters)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(Routes.StatisticsScreenRoute,
                enterTransition = { enterAnimation() },
            exitTransition = { exitAnimation() },
            popEnterTransition = { enterAnimation() },
            popExitTransition = { exitAnimation() }
        )
        {
            StatisticsScreen(modifier = Modifier.fillMaxSize())
        }
        composable(Routes.ReviewListScreenRoute,
            enterTransition = { enterAnimation() },
            exitTransition = { exitAnimation() },
            popEnterTransition = { enterAnimation() },
            popExitTransition = { exitAnimation() }
        )
        {
            ReviewIncorrectListScreen(
                reviewViewModel = reviewViewModel,
                onNavigateToDetail = { questionId ->
                    navController.navigate(Routes.reviewDetailNavigationRoute(questionId))
                },
                onStartReviewQuiz = { questionIds ->
                    val idsString = questionIds.joinToString(",")
                    navController.navigate(
                        "${Routes.QuizScreenRoute}?useAllSubjects=false&${Routes.QuizScreenQuestionIdsArg}=${idsString}"
                    )
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        composable(
            route = "${Routes.ReviewDetailScreenRoute}/{${Routes.ReviewDetailScreenArg}}",
            arguments = listOf(navArgument(Routes.ReviewDetailScreenArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getInt(Routes.ReviewDetailScreenArg)
            requireNotNull(questionId) { stringResource(id = R.string.error_question_id_missing) }
            ReviewIncorrectDetailScreen(
                questionId = questionId,
                reviewViewModel = reviewViewModel,
                onNavigateBack = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
