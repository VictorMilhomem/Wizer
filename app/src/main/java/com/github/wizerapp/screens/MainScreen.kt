package com.github.wizerapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.wizerapp.screens.CreateGroupScreen
import com.github.wizerapp.screens.CreateExerciseScreen
import com.github.wizerapp.screens.DoubtsScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false, // Atualize com base no estado de seleção
                    onClick = { navController.navigate("createGroup") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Criar Grupo") },
                    label = { Text("Criar Grupo") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("createExercise") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Criar Exercício") },
                    label = { Text("Criar Exercício") }
                )
                NavigationBarItem(
                    selected = false, // Atualize conforme o estado de seleção
                    onClick = { navController.navigate("doubts") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Dúvidas") },
                    label = { Text("Dúvidas") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "createGroup",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("createGroup") {
                // Sua tela de criação de grupo; você pode usar o CreateGroupScreen que já implementou
                CreateGroupScreen()
            }
            composable("createExercise") {
                CreateExerciseScreen()
            }
            composable("doubts") {
                DoubtsScreen()
            }
        }
    }
}
