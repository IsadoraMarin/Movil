package com.example.proyectoaplicaciones.ui.Screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.* 
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.proyectoaplicaciones.Navigation.Screen
import com.example.proyectoaplicaciones.viewmodel.AuthViewModel
import com.example.proyectoaplicaciones.viewmodel.AuthUiState

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()

    when {
        uiState.isAuthenticated -> {
            UserProfileScreen(navController, uiState, authViewModel)
        }
        uiState.isGuest -> {
            GuestProfileScreen(navController)
        }
        else -> {
            LaunchedEffect(Unit) {
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            }
        }
    }
}

@Composable
private fun GuestProfileScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil de Invitado", modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("¡Únete a la comunidad!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Crea una cuenta o inicia sesión para participar.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate(Screen.Login.route) }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Iniciar Sesión")
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { navController.navigate(Screen.Register.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("Crear Cuenta")
        }
    }
}

@Composable
private fun UserProfileScreen(
    navController: NavController, 
    uiState: AuthUiState, 
    authViewModel: AuthViewModel
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> imageUri = uri }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                // TODO: Lanzar la cámara para tomar una foto
            } else {
                // TODO: Mostrar un mensaje si el permiso es denegado
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Box {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUri),
                        contentDescription = "Foto de Perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de Perfil",
                        modifier = Modifier.size(120.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(
                onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Cambiar foto de perfil", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(uiState.username, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(uiState.email, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(modifier = Modifier.fillMaxWidth()) {
            Column {
                ProfileOptionItem(Icons.Default.Favorite, "Mis Favoritos") { navController.navigate(Screen.Favorites.route) }
                Divider()
                ProfileOptionItem(Icons.Default.Edit, "Editar Perfil") { navController.navigate(Screen.EditProfile.route) }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { authViewModel.logout() }, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Cerrar Sesión")
        }
    }
}

@Composable
fun ProfileOptionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
    }
}
