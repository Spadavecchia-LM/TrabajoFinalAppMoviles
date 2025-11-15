package com.example.trabajofinalappmoviles.presentacion.clima.actual

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.trabajofinalappmoviles.ui.theme.TrabajoFinalAppMovilesTheme
@Composable
fun ClimaView(
    modifier: Modifier = Modifier,
    state : ClimaEstado,
    onAction: (ClimaIntencion)->Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAction(ClimaIntencion.actualizarClima)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(state){
            is ClimaEstado.Error -> ErrorView(mensaje = state.mensaje)
            is ClimaEstado.Exitoso -> ClimaView(
                ciudad = state.ciudad,
                temperatura = state.temperatura,
                descripcion = state.descripcion,
                st = state.st,
                //Nuevos campos del modelo
                tempMin = state.tempMin,
                tempMax = state.tempMax,
                humedad = state.humedad

            )
            ClimaEstado.Vacio -> LoadingView()
            ClimaEstado.Cargando -> EmptyView()
        }
        Spacer(modifier = Modifier.height(100.dp))
        Button(onClick = { onAction(ClimaIntencion.CompartirClima) }) {
            Text(text = "Compartir clima")
        }
    }
}

@Composable
fun EmptyView(){
    Text(text = "No hay nada que mostrar")
}

@Composable
fun LoadingView(){
    Text(text = "Cargando")
}

@Composable
fun ErrorView(mensaje: String){
    Text(text = mensaje)
}

@Composable
fun ClimaView(ciudad: String, temperatura: Double, descripcion: String, st:Double, tempMin:Double, tempMax:Double, humedad:Long)
{
    Log.d("objetoEntero", "trae -> $descripcion")
    /*
    Text(text = ciudad, style = MaterialTheme.typography.titleMedium)
    Text(text = "${temperatura}°", style = MaterialTheme.typography.titleLarge)
    Text(text = descripcion, style = MaterialTheme.typography.bodyMedium)
    Text(text = "sensacionTermica: ${st}°", style = MaterialTheme.typography.bodyMedium)
    Text(text = "temperaturaMaxima: ${tempMax}°", style = MaterialTheme.typography.bodyMedium)
    Text(text = "temperaturaMinima: ${tempMin}°", style = MaterialTheme.typography.bodyMedium)
    Text(text = "humedad: ${humedad}%", style = MaterialTheme.typography.bodyMedium)
    */
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
    ) { }
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Text( text = ciudad, style = MaterialTheme.typography.titleLarge )
        Text( text = "${temperatura.toInt()}°C", style = MaterialTheme.typography.displaySmall )
        Text( text ="$descripcion",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text( text = "ST: ${st.toInt()}°", style = MaterialTheme.typography.bodyMedium )
            Text( text = "Min: ${tempMin.toInt()}°", style = MaterialTheme.typography.bodyMedium )
            Text( text = "Max: ${tempMax.toInt()}°", style = MaterialTheme.typography.bodyMedium )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text( text = "Humedad: ${humedad}%", style = MaterialTheme.typography.bodyMedium )
        }

}
}

@Preview(showBackground = true)
@Composable
fun ClimaPreviewVacio() {
    TrabajoFinalAppMovilesTheme() {
        ClimaView(state = ClimaEstado.Vacio, onAction = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ClimaPreviewError() {
    TrabajoFinalAppMovilesTheme {
        ClimaView(state = ClimaEstado.Error("Se rompio todo"), onAction = {})
    }
}

@Preview(showBackground = true)
@Composable
fun ClimaPreviewExitoso() {
    TrabajoFinalAppMovilesTheme {
        ClimaView(
            ciudad = "Mendoza",
            temperatura = 22.5,
            descripcion = "cielo despejado",
            st = 23.0,
            tempMin = 18.0,
            tempMax = 25.0,
            humedad = 40L
        )
    }
}

