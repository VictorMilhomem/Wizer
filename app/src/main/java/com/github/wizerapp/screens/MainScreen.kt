package com.github.wizerapp.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false, // atualize com base no estado
                    onClick = { navController.navigate("createGroup") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Criar Grupo") },
                    label = { Text("Grupo") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("createExercise") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Criar Exercício") },
                    label = { Text("Exercício") }
                )
                // Exemplo de uma NavigationBarItem no MainScreen.kt:
                NavigationBarItem(
                    selected = false, // Atualize conforme o estado
                    onClick = { navController.navigate("createQuiz") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Gerar Quiz") },
                    label = { Text("Quiz") }
                )
                NavigationBarItem(
                    selected = false, // Atualize conforme o estado
                    onClick = { navController.navigate("doubt") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Dúvidas") },
                    label = { Text("Dúvidas") }
                )

                // Outras opções...
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "createGroup",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("createGroup") { CreateGroupScreen() }
            composable("createExercise") { CreateExerciseScreen() }
            composable("createQuiz") { CreateQuizScreen() }
            composable ("doubt") { DoubtsScreen() }

            // Outras rotas...
        }
    }
}
