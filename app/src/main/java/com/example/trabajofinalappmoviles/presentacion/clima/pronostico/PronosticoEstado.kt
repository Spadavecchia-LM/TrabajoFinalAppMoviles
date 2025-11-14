package com.example.trabajofinalappmoviles.presentacion.clima.pronostico

import com.example.trabajofinalappmoviles.repository.modelos.ListForecast

sealed class PronosticoEstado {
    data class Exitoso (
        val climas: List<ListForecast>,
    ) : PronosticoEstado()
    data class Error(
        val mensaje :String = "",
    ) : PronosticoEstado()
    data object Vacio: PronosticoEstado()
    data object Cargando: PronosticoEstado()

}