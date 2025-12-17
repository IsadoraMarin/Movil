package com.example.proyectoaplicaciones.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectoaplicaciones.R
import com.example.proyectoaplicaciones.viewModel.AuthViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- INICIO DE LA LÓGICA PARA LA FOTO DE PERFIL ---

    var showDialog by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para la Galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                authViewModel.onProfileImageChange(uri)
            }
        }
    )

    // Launcher para la Cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageUri?.let { authViewModel.onProfileImageChange(it) }
            }
        }
    )

    // Launcher para pedir el permiso de la Cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // Si se concede el permiso, se lanza la cámara
                val newUri = createTempUri(context)
                tempImageUri = newUri
                cameraLauncher.launch(newUri)
            } else {
                // Opcional: Mostrar un mensaje al usuario explicando por qué se necesita el permiso.
            }
        }
    )

    // --- FIN DE LA LÓGICA PARA LA FOTO DE PERFIL ---

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(uiState.user, uiState.email) {
        username = uiState.user?.username ?: ""
        email = uiState.email
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- INICIO DE LA UI PARA LA FOTO DE PERFIL ---
            AsyncImage(
                model = uiState.profileImageUri ?: R.drawable.ic_launcher_foreground,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { showDialog = true }, // Al hacer clic, se muestra el diálogo
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_launcher_foreground)
            )
            Spacer(modifier = Modifier.height(16.dp))
            // --- FIN DE LA UI PARA LA FOTO DE PERFIL ---

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { /* El email no es editable */ },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { /* TODO: Lógica para guardar cambios de username */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }
        }
    }

    // --- INICIO DEL DIÁLOGO DE SELECCIÓN ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Cambiar foto de perfil") },
            text = { Text("Elige una opción") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        // Comprobar si ya tenemos permiso para la cámara
                        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                            PackageManager.PERMISSION_GRANTED -> {
                                val newUri = createTempUri(context)
                                tempImageUri = newUri
                                cameraLauncher.launch(newUri)
                            }
                            else -> {
                                // Si no, pedir permiso
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }
                ) {
                    Text("Cámara")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        galleryLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text("Galería")
                }
            }
        )
    }
    // --- FIN DEL DIÁLOGO DE SELECCIÓN ---
}

// Función helper para crear un archivo temporal y obtener su URI
private fun createTempUri(context: Context): Uri {
    val file = File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}
