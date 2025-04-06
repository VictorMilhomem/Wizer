package com.github.wizerapp.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.wizerapp.viewmodels.CreateExerciseViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseScreen(
    viewModel: CreateExerciseViewModel = viewModel(),
    onExerciseCreated: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Coletar eventos de UI do ViewModel
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CreateExerciseViewModel.UiEvent.Success -> {
                    snackbarHostState.showSnackbar(event.message)
                    onExerciseCreated()
                }
                is CreateExerciseViewModel.UiEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is CreateExerciseViewModel.UiEvent.VideoUploaded -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(padding)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabeçalho
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.Create,
                        contentDescription = "Criar Exercício",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Criar Novo Exercício",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Informações básicas do exercício
            SectionCard(
                title = "Informações Básicas",
                icon = Icons.Filled.Info
            ) {
                // Título do exercício
                CustomTextField(
                    value = viewModel.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = "Título do Exercício",
                    leadingIcon = Icons.Filled.Title,
                    placeholder = "Ex: Equação do 2° Grau"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Descrição do exercício
                CustomTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = "Descrição do Exercício",
                    leadingIcon = Icons.Filled.Description,
                    placeholder = "Ex: Calcular as raízes da equação x²+5x+6=0",
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Matéria
                CustomTextField(
                    value = viewModel.subject,
                    onValueChange = { viewModel.updateSubject(it) },
                    label = "Matéria",
                    leadingIcon = Icons.Filled.School,
                    placeholder = "Ex: Matemática, Física, etc."
                )
            }

            // Opções de resposta
            SectionCard(
                title = "Opções de Resposta",
                icon = Icons.Filled.List
            ) {
                // Opções separadas por vírgula
                CustomTextField(
                    value = viewModel.optionsText,
                    onValueChange = { viewModel.updateOptionsText(it) },
                    label = "Opções (separadas por vírgula)",
                    leadingIcon = Icons.Filled.RadioButtonChecked,
                    placeholder = "Ex: x = -2 e x = -3, x = 2 e x = 3, x = -2 e x = 3, x = 2 e x = -3",
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Índice da opção correta
                CustomTextField(
                    value = viewModel.correctText,
                    onValueChange = { viewModel.updateCorrectText(it) },
                    label = "Índice da Opção Correta (começando em 0)",
                    leadingIcon = Icons.Filled.CheckCircle,
                    placeholder = "Ex: 0"
                )
            }

            // Resolução do exercício
            SectionCard(
                title = "Resolução do Exercício",
                icon = Icons.Filled.Lightbulb
            ) {
                // Texto da resolução
                CustomTextField(
                    value = viewModel.resolutionText,
                    onValueChange = { viewModel.updateResolutionText(it) },
                    label = "Explicação da Resolução",
                    leadingIcon = Icons.Filled.TextFields,
                    placeholder = "Ex: Para resolver essa equação, primeiro calculamos o discriminante...",
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Upload de vídeo
                Text(
                    text = "Vídeo de Resolução (opcional)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Componente de selecionar vídeo
                VideoPicker { uri ->
                    viewModel.updateSelectedVideo(uri)
                }

                // Se um vídeo for selecionado, mostra botão de upload
                viewModel.selectedVideoUri?.let { uri ->
                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.uploadVideo() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !viewModel.isUploading
                    ) {
                        if (viewModel.isUploading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.VideoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fazer Upload do Vídeo")
                        }
                    }
                }

                // Exibir URL do vídeo caso tenha sido enviado
                viewModel.resolutionVideoUrl?.let { url ->
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Vídeo enviado com sucesso!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Botão de criar exercício
            Button(
                onClick = { viewModel.createExercise() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !viewModel.isCreatingExercise
            ) {
                if (viewModel.isCreatingExercise) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Criar Exercício")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título da seção
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Conteúdo da seção
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    placeholder: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = singleLine,
        minLines = if (!singleLine) 3 else 1
    )
}

@Composable
fun VideoPicker(onVideoSelected: (Uri?) -> Unit) {
    // Cria um launcher para o seletor de arquivos filtrando por vídeos
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onVideoSelected(uri)
    }

    // Botão para selecionar vídeo
    Button(
        onClick = { launcher.launch("video/*") },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.VideoLibrary,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Selecionar Vídeo")
    }
}