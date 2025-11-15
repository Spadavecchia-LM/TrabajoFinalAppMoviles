package com.example.trabajofinalappmoviles.repository.api

import com.example.trabajofinalappmoviles.repository.modelos.Ciudad
import com.example.trabajofinalappmoviles.repository.modelos.Clima
import com.example.trabajofinalappmoviles.repository.modelos.ListForecast

interface Repositorio {
    suspend fun buscarCiudad(ciudad: String): List<Ciudad>
    suspend fun obtenerClimaCiudad(lat: Float, lon: Float): Clima
    suspend fun obtenerClimaPorNombre(nombre: String): Clima
    suspend fun obtenerPronosticoPorCoord(lat: Float, lon: Float): List<ListForecast>


    suspend fun obtenerPronosticoCiudad(nombre: String): List<ListForecast>

}