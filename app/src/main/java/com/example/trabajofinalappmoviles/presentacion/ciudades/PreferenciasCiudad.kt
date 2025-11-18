package com.example.trabajofinalappmoviles.presentacion.ciudades

import android.content.Context

class PreferenciasCiudad(context: Context) {
    private val sharedPref = context.getSharedPreferences("ciudad_favorita", Context.MODE_PRIVATE)

    fun guardarCiudad(nombre: String, lat: Float, lon: Float) {
        with(sharedPref.edit()) {
            putString("nombre", nombre)
            putFloat("lat", lat)
            putFloat("lon", lon)
            apply()
        }
    }

    fun obtenerCiudad(): Triple<String, Float, Float>? {
        val nombre = sharedPref.getString("nombre", null)
        val lat = sharedPref.getFloat("lat", 0f)
        val lon = sharedPref.getFloat("lon", 0f)

        return if (nombre != null && lat != 0f && lon != 0f) {
            Triple(nombre, lat, lon)
        } else {
            null
        }
    }

    fun eliminarCiudad() {
        with(sharedPref.edit()) {
            remove("nombre")
            remove("lat")
            remove("lon")
            apply()
        }
    }
}