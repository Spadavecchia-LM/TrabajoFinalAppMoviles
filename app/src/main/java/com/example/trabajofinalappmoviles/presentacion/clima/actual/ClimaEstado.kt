package com.example.trabajofinalappmoviles.presentacion.clima.actual

sealed class ClimaEstado {
    data class Exitoso (
        val ciudad: String = "",
        val temperatura: Double = 0.0,
        val descripcion: String= "",
        val st :Double = 0.0,
        //Agrego estos campos para mejorar UX de la app cuando renderizamos el clima
        val tempMin: Double = 0.0,
        val tempMax: Double = 0.0,
        val humedad: Long = 0L,
    ) : ClimaEstado()
    data class Error(
        val mensaje :String = "",
    ) : ClimaEstado()
    data object Vacio: ClimaEstado()
    data object Cargando: ClimaEstado()

}