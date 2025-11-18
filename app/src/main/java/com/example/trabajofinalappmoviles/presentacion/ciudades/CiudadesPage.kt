package com.example.trabajofinalappmoviles.presentacion.ciudades

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.trabajofinalappmoviles.repository.api.RepositorioApi
import com.example.trabajofinalappmoviles.router.Enrutador
import com.example.trabajofinalappmoviles.router.Ruta
import com.google.android.gms.location.LocationServices

@Composable
fun CiudadesPage(
    navHostController: NavHostController,
    preferencias: PreferenciasCiudad
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val viewModel: CiudadesViewModel = viewModel(
        factory = CiudadesViewModel.CiudadesViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController),
            preferencias = preferencias
        )
    )

    var ubicando by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                ubicando = true
                obtenerUbicacion(fusedLocationClient) { lat, lon ->
                    ubicando = false
                    val ruta = Ruta.Clima(
                        lat = lat.toFloat(),
                        lon = lon.toFloat(),
                        nombre = "Mi ubicación"
                    )
                    viewModel.router.navegar(ruta)
                }
            }
        }
    )

    CiudadesView(
        state = viewModel.uiState,
        ubicando = ubicando,
        preferencias = preferencias,
        onAction = { intencion ->
            when (intencion) {
                CiudadesIntencion.MiUbicacion -> {
                    val permiso = Manifest.permission.ACCESS_FINE_LOCATION

                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            permiso
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            ubicando = true
                            obtenerUbicacion(fusedLocationClient) { lat, lon ->
                                ubicando = false
                                val ruta = Ruta.Clima(
                                    lat = lat.toFloat(),
                                    lon = lon.toFloat(),
                                    nombre = "Mi ubicación"
                                )
                                viewModel.router.navegar(ruta)
                            }
                        }
                        else -> launcher.launch(permiso)
                    }
                }

                else -> viewModel.ejecutar(intencion)
            }
        }
    )
}

@SuppressLint("MissingPermission")
private fun obtenerUbicacion(
    fused: com.google.android.gms.location.FusedLocationProviderClient,
    onUbicacion: (Double, Double) -> Unit
) {
    val request = com.google.android.gms.location.LocationRequest.Builder(
        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
        0L
    ).setMaxUpdates(1).build()

    val callback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(result: com.google.android.gms.location.LocationResult) {
            val loc = result.lastLocation
            if (loc != null) {
                onUbicacion(loc.latitude, loc.longitude)
            }
            fused.removeLocationUpdates(this)
        }
    }

    fused.requestLocationUpdates(
        request,
        callback,
        android.os.Looper.getMainLooper()
    )
}
