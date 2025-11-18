package com.example.trabajofinalappmoviles.presentacion.clima.actual

import PronosticoViewModel
import PronosticoViewModelFactory
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.trabajofinalappmoviles.presentacion.ciudades.PreferenciasCiudad
import com.example.trabajofinalappmoviles.presentacion.clima.pronostico.PronosticoView
import com.example.trabajofinalappmoviles.repository.api.RepositorioApi
import com.example.trabajofinalappmoviles.router.Enrutador

@Composable
fun ClimaPage(
    navHostController: NavHostController,
    lat : Float,
    lon : Float,
    nombre: String,
    preferencias: PreferenciasCiudad
){
    val viewModel : ClimaViewModel = viewModel(
        factory = ClimaViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController),
            lat = lat,
            lon = lon,
            nombre = nombre
        )
    )
    val pronosticoViewModel : PronosticoViewModel = viewModel(
        factory = PronosticoViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController),
            nombre = nombre,
            lat = lat,
            lon = lon
        )
    )

    val context = LocalContext.current

    Column {
        ClimaView(
            state = viewModel.uiState,
            preferencias = preferencias,
            nombreCiudad = nombre,
            lat = lat,
            lon = lon,
            onAction = { intencion ->
                when (intencion) {
                    ClimaIntencion.actualizarClima -> {
                        viewModel.ejecutar(intencion)
                    }
                    ClimaIntencion.CompartirClima -> {
                        val estado = viewModel.uiState
                        val texto = when (estado) {
                            is ClimaEstado.Exitoso ->
                                "Clima en ${estado.ciudad}: ${estado.temperatura}°, ${estado.descripcion}, sensación térmica ${estado.st}°"
                            is ClimaEstado.Error ->
                                "No se pudo obtener el clima de $nombre: ${estado.mensaje}"
                            ClimaEstado.Vacio ->
                                "Clima en $nombre: aún no hay datos para mostrar."
                            ClimaEstado.Cargando ->
                                "Obteniendo el clima actual de $nombre..."
                        }
                        compartirClima(context, texto)
                    }
                }
            },
            navController = navHostController
        )
        PronosticoView(
            state = pronosticoViewModel.uiState,
            onAction = { intencion ->
                pronosticoViewModel.ejecutar(intencion)
            }
        )
    }

}

fun compartirClima(context: Context, texto: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, texto)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Compartir clima")
    context.startActivity(shareIntent)
}
