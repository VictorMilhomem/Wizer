package com.github.wizerapp.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.wizerapp.ui.theme.WizerAppTheme
import com.github.wizerapp.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(initialQuizId: String = "") {
    WizerAppTheme {
        val authViewModel: AuthViewModel = viewModel()
        var isLoggedIn by remember { mutableStateOf(false) }

        // Verificar estado de autenticação
        LaunchedEffect(Unit) {
            authViewModel.checkAuthState()
            isLoggedIn = authViewModel.currentUser != null
        }

        // Observar mudanças no usuário atual
        LaunchedEffect(authViewModel.currentUser) {
            isLoggedIn = authViewModel.currentUser != null
        }

        if (!isLoggedIn) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { isLoggedIn = true }
            )
        } else {
            val navController = rememberNavController()
            val screens = listOf(
                Screen.CreateGroup,
                Screen.CreateExercise,
                Screen.CreateQuiz,
                Screen.Doubts
            )

            LaunchedEffect(initialQuizId) {
                if (initialQuizId.isNotBlank()) {
                    navController.navigate("studentQuiz/$initialQuizId")
                }
            }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "WizerApp",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        actions = {
                            IconButton(onClick = {
                                authViewModel.signOut()
                                isLoggedIn = false
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.ExitToApp,
                                    contentDescription = "Sair",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination

                        screens.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selected) screen.selectedIcon else screen.icon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(screen.title) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        // Pop up to the start destination of the graph to
                                        // avoid building up a large stack of destinations
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // Avoid multiple copies of the same destination
                                        launchSingleTop = true
                                        // Restore state when reselecting a previously selected item
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        }
                    }
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.CreateGroup.route,
                    modifier = Modifier.padding(padding)
                ) {
                    composable(Screen.CreateGroup.route) { CreateGroupScreen() }
                    composable(Screen.CreateExercise.route) { CreateExerciseScreen() }
                    composable(Screen.CreateQuiz.route) { CreateQuizScreen() }
                    composable(Screen.Doubts.route) { DoubtsScreen() }
                    composable("studentQuiz/{quizId}") { backStackEntry ->
                        val quizId = backStackEntry.arguments?.getString("quizId") ?: ""
                        StudentQuizScreen(quizId = quizId)
                    }

                }
            }
        }
    }
}

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object CreateGroup : Screen(
        route = "createGroup",
        title = "Grupos",
        icon = Icons.Outlined.Groups,
        selectedIcon = Icons.Filled.Groups
    )

    object CreateExercise : Screen(
        route = "createExercise",
        title = "Exercícios",
        icon = Icons.Outlined.Create,
        selectedIcon = Icons.Filled.Create
    )

    object CreateQuiz : Screen(
        route = "createQuiz",
        title = "Quiz",
        icon = Icons.Outlined.Quiz,
        selectedIcon = Icons.Filled.Quiz
    )

    object Doubts : Screen(
        route = "doubts",
        title = "Dúvidas",
        icon = Icons.Outlined.QuestionAnswer,
        selectedIcon = Icons.Filled.QuestionAnswer
    )
}