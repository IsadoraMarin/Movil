package com.example.proyectoaplicaciones.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.example.proyectoaplicaciones.navigation.Screen
import com.example.proyectoaplicaciones.viewModel.AuthViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempImageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para obtener una imagen de la galería
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                authViewModel.onProfileImageChange(uri)
            }
        }
    )

    // Launcher para tomar una foto con la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageUri?.let { authViewModel.onProfileImageChange(it) }
            }
        }
    )

    // Launcher para pedir permiso de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val uri = createImageUri(context)
                tempImageUri = uri
                cameraLauncher.launch(uri)
            } else {
                // Opcional: Mostrar un snackbar o mensaje si el permiso es denegado
            }
        }
    )

    // --- Efecto de Redirección ---
    LaunchedEffect(authState.isAuthenticated, authState.isGuest) {
        if (authViewModel.uiState.value.user == null && !authState.isGuest && authState.isAuthenticated) {
            return@LaunchedEffect
        }
        if (!authState.isAuthenticated && !authState.isGuest) {
            navController.navigate(Screen.Welcome.route) {
                navController.graph.findStartDestination().route?.let { route ->
                    popUpTo(route) { inclusive = true }
                }
                launchSingleTop = true
            }
        }
    }

    // --- Diálogo para elegir fuente de imagen ---
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Elige una foto de perfil") },
            text = { Text("¿Desde dónde quieres seleccionar la imagen?") },
            confirmButton = {
                TextButton(onClick = { showImageSourceDialog = false }) { Text("Cancelar") }
            },
            dismissButton = {
                Column(Modifier.padding(horizontal = 16.dp)){
                     Button(onClick = { 
                        showImageSourceDialog = false
                        galleryLauncher.launch(androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) { 
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.padding(end=8.dp))
                        Text("Galería") 
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { 
                        showImageSourceDialog = false
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                val uri = createImageUri(context)
                                tempImageUri = uri
                                cameraLauncher.launch(uri)
                            }
                            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(end=8.dp))
                        Text("Cámara") 
                     }
                }
            }
        )
    }


    // --- Interfaz de Usuario ---
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (!authState.isAuthenticated && !authState.isGuest) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Sección de Imagen y Nombre ---
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { showImageSourceDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (authState.profileImageUri != null) {
                        AsyncImage(
                            model = authState.profileImageUri,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Añadir foto de perfil",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                if(authState.isAuthenticated){
                     Text(
                        text = authState.user?.username ?: "Usuario", 
                        style = MaterialTheme.typography.headlineSmall, 
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = authState.email, 
                        style = MaterialTheme.typography.bodyLarge, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }else{
                     Text("Invitado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
               
                Spacer(modifier = Modifier.height(32.dp))

                // --- Sección de Botones de Acción ---
                if(authState.isAuthenticated){
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = { navController.navigate(Screen.EditProfile.route) }) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.padding(end=8.dp))
                            Text("Editar")
                        }
                        Button(onClick = { navController.navigate(Screen.Favorites.route) }) {
                            Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.padding(end=8.dp))
                            Text("Favoritos")
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f)) // Empuja el botón de cerrar sesión hacia abajo

                // --- Botón de Cerrar Sesión / Iniciar Sesión ---
                if (authState.isAuthenticated) {
                    Button(
                        onClick = { authViewModel.logout() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.padding(end=8.dp))
                        Text("Cerrar Sesión")
                    }
                } else {
                    Button(onClick = { authViewModel.logout() }) {
                        Text("Iniciar Sesión")
                    }
                }
                 Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// Función de utilidad para crear un archivo y su URI para la cámara
private fun createImageUri(context: Context): Uri {
    val file = File.createTempFile(
        "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}_",
        ".jpg",
        context.externalCacheDir
    )
    return FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        file
    )
}
