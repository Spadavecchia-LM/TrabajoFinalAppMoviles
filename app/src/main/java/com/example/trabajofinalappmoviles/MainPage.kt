package com.example.trabajofinalappmoviles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.trabajofinalappmoviles.presentacion.ciudades.CiudadesPage
import com.example.trabajofinalappmoviles.presentacion.ciudades.PreferenciasCiudad
import com.example.trabajofinalappmoviles.presentacion.clima.actual.ClimaPage
import com.example.trabajofinalappmoviles.router.Ruta


@Composable
fun MainPage() {
    val navHostController = rememberNavController()
    val context = LocalContext.current
    val preferencias = remember { PreferenciasCiudad(context) }

    var pantallaInicial by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val ciudad = preferencias.obtenerCiudad()
        if (ciudad != null) {
            val (nombre, lat, lon) = ciudad
            pantallaInicial = "clima?lat=$lat&lon=$lon&nombre=$nombre"
        }
    }

    NavHost(
        navController = navHostController,
        startDestination = pantallaInicial ?: Ruta.Ciudades.id
    ) {
        composable(
            route = Ruta.Ciudades.id
        ) {
            CiudadesPage(navHostController, preferencias)
        }
        composable(
            route = "clima?lat={lat}&lon={lon}&nombre={nombre}",
            arguments =  listOf(
                navArgument("lat") { type= NavType.FloatType },
                navArgument("lon") { type= NavType.FloatType },
                navArgument("nombre") { type= NavType.StringType }
            )
        ) {
            val lat = it.arguments?.getFloat("lat") ?: 0.0f
            val lon = it.arguments?.getFloat("lon") ?: 0.0f
            val nombre = it.arguments?.getString("nombre") ?: ""
            ClimaPage(navHostController, lat = lat, lon = lon, nombre = nombre, preferencias)
        }
    }
}