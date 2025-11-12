package com.example.proyectoaplicaciones.ui.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyectoaplicaciones.Navigation.Screen
import com.example.proyectoaplicaciones.ui.theme.ProyectoAplicacionesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {Text("Something Cool", fontWeight = FontWeight.Bold)}
            )
        }
    ){ innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
                    contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Bienvenido al Foro Gamer 🎮",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Selecciona una categoría:",
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate(Screen.Popular.route) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Posts Populares")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Screen.Noticias.route) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Noticias")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate(Screen.Comunidad.route) },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Comunidad")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview(){
    ProyectoAplicacionesTheme {
        HomeScreen(navController = rememberNavController())
    }
}