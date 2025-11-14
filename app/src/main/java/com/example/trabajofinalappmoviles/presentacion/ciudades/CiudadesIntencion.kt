package com.example.trabajofinalappmoviles.presentacion.ciudades

import com.example.trabajofinalappmoviles.repository.modelos.Ciudad

sealed class CiudadesIntencion {
    data class Buscar(val nombre: String) : CiudadesIntencion()
    data class Seleccionar(val ciudad : Ciudad) : CiudadesIntencion()
}


