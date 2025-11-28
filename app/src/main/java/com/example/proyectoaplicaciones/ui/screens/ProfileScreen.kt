package com.example.proyectoaplicaciones.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.example.proyectoaplicaciones.R
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

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> uri?.let { authViewModel.onProfileImageChange(it) } }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempImageUri?.let { authViewModel.onProfileImageChange(it) }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val uri = createImageUri(context)
                tempImageUri = uri
                cameraLauncher.launch(uri)
            }
        }
    )

    // -- Efecto de Redirección --
    LaunchedEffect(authState.isAuthenticated, authState.isGuest) {
        // Solo redirige si el estado es inválido (ni autenticado, ni invitado)
        if (!authState.isAuthenticated && !authState.isGuest) {
            navController.navigate(Screen.Welcome.route) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    // -- Diálogo para elegir fuente de imagen --
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Elige una foto de perfil") },
            text = { Text("¿Desde dónde quieres seleccionar la imagen?") },
            confirmButton = { TextButton(onClick = { showImageSourceDialog = false }) { Text("Cerrar") } },
            dismissButton = {
                Row(Modifier.padding(horizontal = 8.dp)){
                    Button(onClick = { 
                        showImageSourceDialog = false
                        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }) { Text("Galería") }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { 
                        showImageSourceDialog = false
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            val uri = createImageUri(context)
                            tempImageUri = uri
                            cameraLauncher.launch(uri)
                        } else {
                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) { Text("Cámara") }
                }
            }
        )
    }

    // --- Interfaz de Usuario ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // El contenido principal se alinea arriba
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- Sección de Imagen y Nombre ---
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { 
                        if(authState.isAuthenticated) {
                            showImageSourceDialog = true 
                        }
                    },
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
                        contentDescription = "Foto de perfil por defecto",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (authState.isAuthenticated) {
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
                Spacer(modifier = Modifier.height(32.dp))
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
            } else { // --- Tarjeta para el Invitado ---
                Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Únete a la Comunidad", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Crea una cuenta para publicar, comentar y guardar tus favoritos.", textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                authViewModel.logout() // Limpia el estado de invitado
                                navController.navigate(Screen.Login.route)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Iniciar Sesión") }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                authViewModel.logout() // Limpia el estado de invitado
                                navController.navigate(Screen.Register.route)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Crear Cuenta") }
                    }
                }
            }
        }

        // El botón de cerrar sesión se alinea abajo
        if (authState.isAuthenticated) {
            Button(
                onClick = { authViewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}

private fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        file
    )
}
