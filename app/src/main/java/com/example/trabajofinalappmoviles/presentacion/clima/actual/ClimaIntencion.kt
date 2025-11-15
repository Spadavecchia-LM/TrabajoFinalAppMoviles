package com.example.trabajofinalappmoviles.presentacion.clima.actual

sealed class ClimaIntencion {
    object actualizarClima: ClimaIntencion()
    object CompartirClima: ClimaIntencion()

}