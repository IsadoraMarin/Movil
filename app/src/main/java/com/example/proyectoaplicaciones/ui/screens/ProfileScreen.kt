package com.example.proyectoaplicaciones.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectoaplicaciones.R
import com.example.proyectoaplicaciones.data.model.Role
import com.example.proyectoaplicaciones.navigation.Screen
import com.example.proyectoaplicaciones.viewModel.AuthViewModel

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel) {
    val uiState by authViewModel.uiState.collectAsState()
    val currentUser = uiState.user

    // --- INICIO DE LA CORRECCIÓN ---
    // Doble guarda para máxima seguridad: el usuario debe estar autenticado Y el objeto no debe ser nulo.
    if (!uiState.isAuthenticated || currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Inicia sesión para ver tu perfil.")
        }
        return
    }
    // --- FIN DE LA CORRECCIÓN ---

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = uiState.profileImageUri ?: R.drawable.ic_launcher_foreground,
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.ic_launcher_foreground)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Se usan llamadas seguras (?.) y valores por defecto (?:) para NUNCA crashear.
        Text(
            text = currentUser.username ?: "Usuario desconocido",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = currentUser.role?.name ?: "SIN ROL", // <-- PROTEGIDO
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        ProfileOption(icon = Icons.Default.Edit, text = "Editar Perfil") {
            navController.navigate(Screen.EditProfile.route)
        }
        Divider()
        ProfileOption(icon = Icons.Default.Favorite, text = "Mis Favoritos") {
            navController.navigate(Screen.Favorites.route)
        }

        // Se comprueba el rol de forma segura.
        if (currentUser.role != Role.ADMIN) {
            Divider()
            ProfileOption(icon = Icons.Default.Star, text = "Convertirse en Admin (DEBUG)") {
                authViewModel.grantAdminRole()
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate(Screen.Welcome.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión")
        }
    }
}

@Composable
private fun ProfileOption(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text, fontSize = 18.sp, modifier = Modifier.weight(1f))
        }
    }
}
