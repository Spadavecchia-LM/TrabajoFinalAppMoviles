package com.example.trabajofinalappmoviles.presentacion.ciudades

import com.example.trabajofinalappmoviles.repository.modelos.Ciudad

sealed class CiudadesEstado {
    data object Vacio: CiudadesEstado()
    data object Cargando: CiudadesEstado()
    data class Resultado(val ciudades : List<Ciudad>) : CiudadesEstado()
    data class Error(val mensaje : String) : CiudadesEstado()
}
