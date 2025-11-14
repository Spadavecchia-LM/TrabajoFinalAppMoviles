package com.example.trabajofinalappmoviles.presentacion.ciudades

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.trabajofinalappmoviles.repository.api.RepositorioApi
import com.example.trabajofinalappmoviles.router.Enrutador

@Composable
fun CiudadesPage(
    navHostController:  NavHostController
) {
    val viewModel : CiudadesViewModel = viewModel(
        factory = CiudadesViewModel.CiudadesViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController)
        )
    )
    CiudadesView(
        state = viewModel.uiState,
        onAction = { intencion ->
            viewModel.ejecutar(intencion)
        }
    )
}
