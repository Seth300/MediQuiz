package com.example.mediquiz

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldState
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldValue
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mediquiz.navigation.MediQuizNavHost
import com.example.mediquiz.ui.main.MainViewModel
import com.example.mediquiz.ui.review.ReviewViewModel
import com.example.mediquiz.ui.theme.MediQuizTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

// TODO:Spostare
object Routes {
    const val HomeScreenRoute = "home_screen"
    const val QuizScreenRoute = "quiz_screen"
    const val SubjectFilterScreenRoute = "subject_filter_screen"
    const val StatisticsScreenRoute = "statistics_screen"

    // Routes for Review Feature
    const val ReviewListScreenRoute = "review_list_screen"
    const val ReviewDetailScreenRoute = "review_detail_screen"
    const val ReviewDetailScreenArg = "questionId"

    const val QuizScreenQuestionIdsArg = "questionIds" // TODO:Spostare
    fun reviewDetailNavigationRoute(questionId: Int) = "$ReviewDetailScreenRoute/$questionId"
}

// Gestisce la bottom bar
enum class AppDestinations(
    @StringRes val label: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
) {
    HOME(R.string.destination_home_label, Icons.Default.Home, Routes.HomeScreenRoute),
    START_QUIZ(R.string.destination_quiz_label, Icons.Filled.Quiz, Routes.QuizScreenRoute + "?useAllSubjects=false"), // Utilizza i filtri selezionati
    REVIEW(R.string.destination_review_label, Icons.Filled.RateReview, Routes.ReviewListScreenRoute),
    STATISTICS(R.string.destination_statistics_label, Icons.Default.Assessment, Routes.StatisticsScreenRoute),
}

private const val NAV_DEBUG_TAG = "NavigationDebug"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediQuizTheme {
                MediQuizApp()
            }
        }
    }
}

@Composable
fun MediQuizApp(navController: NavHostController = rememberNavController()) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val reviewViewModel: ReviewViewModel = hiltViewModel()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestinationRoute = currentBackStackEntry?.destination?.route

    LaunchedEffect(currentDestinationRoute) {
        Log.d(NAV_DEBUG_TAG, "MediQuizApp: Current destination route = $currentDestinationRoute")
    }

    val routesWithoutBottomBar = listOf(
        Routes.QuizScreenRoute,
        "${Routes.ReviewDetailScreenRoute}/{${Routes.ReviewDetailScreenArg}}",
        Routes.SubjectFilterScreenRoute
    )

    val showBottomBar = currentDestinationRoute != null &&
            routesWithoutBottomBar.none { routeToHide ->
                if (routeToHide == Routes.QuizScreenRoute) {
                    currentDestinationRoute.startsWith(routeToHide)
                } else {
                    currentDestinationRoute == routeToHide
                }
            }
val state: NavigationSuiteScaffoldState= rememberNavigationSuiteScaffoldState()
    LaunchedEffect(showBottomBar) {
        if (showBottomBar) {
            state.show()
        } else {
            state.hide()
        }
    }
    NavigationSuiteScaffold(
        state=state,
        navigationSuiteColors = NavigationSuiteDefaults.colors(),
        navigationSuiteItems = {
            if (showBottomBar) {
                AppDestinations.entries.forEach { destination ->
                    val selected = currentDestinationRoute == destination.route ||
                            (destination.route.startsWith(Routes.QuizScreenRoute) &&
                                    currentDestinationRoute?.startsWith(Routes.QuizScreenRoute) == true) ||
                            (destination.route == Routes.ReviewListScreenRoute &&
                                    currentDestinationRoute?.startsWith(Routes.ReviewDetailScreenRoute) == true)

                    item(
                        icon = { Icon(destination.icon, contentDescription = stringResource(id = destination.label)) },
                        label = { Text(stringResource(id = destination.label)) },
                        selected = selected,
                        onClick = {
                            Log.d(NAV_DEBUG_TAG, " Current route: $currentDestinationRoute. Navigating to: ${destination.route}")
                            if (currentDestinationRoute != destination.route) {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            } else {

            }
        }
    ) {
        MediQuizNavHost(
            navController = navController,
            mainViewModel = mainViewModel,
            reviewViewModel = reviewViewModel,
            modifier = Modifier
                .padding(0.dp)
                .fillMaxSize()
        )
    }
}


@Composable
fun HomeScreenMenu( //TODO:spostare in un nuovo file per future modifiche estetiche
    navController: NavController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isSyncing by mainViewModel.isSyncing.collectAsState()
    val syncStatusMessage by mainViewModel.syncStatusMessage.collectAsState()

    LaunchedEffect(syncStatusMessage) {
        val syncingText = context.getString(R.string.home_sync_button_syncing_text)
        if (syncStatusMessage != null && syncStatusMessage != syncingText) {
            delay(3000)
            mainViewModel.clearSyncStatusMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                navController.navigate(Routes.QuizScreenRoute + "?useAllSubjects=false")
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Quiz, contentDescription = stringResource(id = R.string.home_start_quiz_filtered_button))
                    Spacer(Modifier.width(8.dp))
                    Text(text=stringResource(id = R.string.home_start_quiz_filtered_button),
                        textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                navController.navigate(Routes.QuizScreenRoute + "?useAllSubjects=true")
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Quiz, contentDescription = stringResource(id = R.string.home_start_quiz_all_subjects_button))
                    Spacer(Modifier.width(8.dp))
                    Text(text= stringResource(id = R.string.home_start_quiz_all_subjects_button),
                        textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                navController.navigate(Routes.SubjectFilterScreenRoute)
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = stringResource(id = R.string.home_filter_subjects_icon_desc))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.home_filter_subjects_button))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                navController.navigate(Routes.ReviewListScreenRoute)
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.RateReview, contentDescription = stringResource(id = R.string.home_review_answers_icon_desc))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.home_review_answers_button))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                navController.navigate(Routes.StatisticsScreenRoute)
            }) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Assessment, contentDescription = stringResource(id = R.string.home_view_statistics_button))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(id = R.string.home_view_statistics_button))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            syncStatusMessage?.let { message ->
                Text(message)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    mainViewModel.clearSyncStatusMessage()
                    mainViewModel.syncDatabase()
                },
                enabled = !isSyncing
            ) {
                if (isSyncing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Sync, contentDescription = stringResource(id = R.string.home_sync_button_syncing_desc))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(id = R.string.home_sync_button_syncing_text))
                    }
                } else {
                    Text(stringResource(id = R.string.home_sync_button_text))
                }
            }
        }
    }
}
