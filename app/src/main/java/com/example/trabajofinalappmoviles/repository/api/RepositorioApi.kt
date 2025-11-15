package com.example.trabajofinalappmoviles.repository.api

import com.example.trabajofinalappmoviles.repository.modelos.Ciudad
import com.example.trabajofinalappmoviles.repository.modelos.Clima
import com.example.trabajofinalappmoviles.repository.modelos.ForecastDTO
import com.example.trabajofinalappmoviles.repository.modelos.ListForecast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


class RepositorioApi : Repositorio{

    private val apiKey = "b89b53e37ce53de43fd019ef83aab867"

    private val cliente = HttpClient(){
        install(ContentNegotiation){
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        if (ciudad.isBlank()){
            return emptyList()
        }

        val respuesta = cliente.get("https://api.openweathermap.org/geo/1.0/direct"){
            parameter("q",ciudad)
            parameter("limit",100)
            parameter("appid",apiKey)
        }

        if (respuesta.status == HttpStatusCode.OK){
            val ciudades = respuesta.body<List<Ciudad>>()
            return ciudades
        }else{
            throw Exception("Error al buscar ciudad desde la API")
        }
    }

    override suspend fun obtenerClimaCiudad(
        lat: Float,
        lon: Float
    ): Clima {
        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/weather"){
            parameter("lat",lat)
            parameter("lon",lon)
            parameter("units","metric")
            parameter("appid",apiKey)
        }
        if (respuesta.status == HttpStatusCode.OK){
            val clima = respuesta.body<Clima>()
            return clima
        }else{
            throw Exception()
        }
    }

    override suspend fun obtenerPronosticoCiudad(nombre: String): List<ListForecast> {
        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/forecast"){
            parameter("q",nombre)
            parameter("units","metric")
            parameter("appid",apiKey)
        }
        if (respuesta.status == HttpStatusCode.OK){
            val forecast = respuesta.body<ForecastDTO>()
            return forecast.list
        }else{
            throw Exception()
        }

    }


}