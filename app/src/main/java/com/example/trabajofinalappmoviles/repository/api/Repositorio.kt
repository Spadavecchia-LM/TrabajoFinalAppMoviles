package com.example.trabajofinalappmoviles.repository.api

import com.example.trabajofinalappmoviles.repository.modelos.Ciudad
import com.example.trabajofinalappmoviles.repository.modelos.Clima
import com.example.trabajofinalappmoviles.repository.modelos.ListForecast

interface Repositorio {
    suspend fun buscarCiudad(ciudad: String): List<Ciudad>
    suspend fun obtenerClimaCiudad(lat: Float, lon: Float): Clima
    suspend fun obtenerPronosticoCiudad(nombre: String): List<ListForecast>
}