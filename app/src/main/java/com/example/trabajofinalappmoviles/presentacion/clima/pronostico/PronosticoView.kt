package com.example.trabajofinalappmoviles.presentacion.clima.pronostico

import android.R.attr.label
import android.R.string.no
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.trabajofinalappmoviles.repository.modelos.ListForecast
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt


//clase privada solo para la vista de pronóstico

data class DiaPronostico(
    val label: String,
    val tempMin: Double,
    val tempMax: Double
)

@Composable
fun PronosticoView(
    modifier: Modifier = Modifier,
    state : PronosticoEstado,
    onAction: (PronosticoIntencion)->Unit
) {
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAction(PronosticoIntencion.actualizarClima)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(state){
            is PronosticoEstado.Error -> ErrorView(mensaje = state.mensaje)
            is PronosticoEstado.Exitoso -> PronosticoContenido(state.climas)
//            is PronosticoEstado.Exitoso -> PronosticoView(state.climas)
            PronosticoEstado.Vacio -> LoadingView()
            PronosticoEstado.Cargando -> EmptyView()
        }
        Spacer(modifier = Modifier.height(100.dp))
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
fun PronosticoView(climas: List<ListForecast>){
    LazyColumn {
        items(items = climas) {
            Card() {
                Text(text = "${it.main.temp}")
            }
        }
    }
}

@Composable
fun PronosticoContenido(climas:List<ListForecast>)
{
    val dias = climas.toDiasPronostico()

    if(dias.isEmpty())
    {
        Text(text = "No hay pronóstico disponible")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        stickyHeader {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
            ){
                Text(
                    text = "Pronóstico del tiempo para los proximos dias",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 10.dp)
                )
            }
        }
        item{
            GraficoPronostico(dias = dias)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(dias){ dia ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = dia.label, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "${dia.tempMin.roundToInt()}°", style = MaterialTheme.typography.bodyMedium)
                }

            }
        }
    }
}

private fun List<ListForecast>.toDiasPronostico(maxDias: Int = 5): List<DiaPronostico> {
    if (isEmpty()) return emptyList()

    val zoneId = ZoneId.systemDefault()
    val labelFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(zoneId)

    /**
     *     No pude encontrar ninguna forma de manejar las fechas de los pronosticos sin usar librerias de
     *     Java especifico de Kotlin no encontre nada
     */
    val grupos = this.groupBy { forecast ->
        val datesToConvert = Instant.ofEpochSecond(forecast.dt)
        labelFormat.format(datesToConvert)
    }

    return grupos.entries
        .sortedBy { it.key }
        .take(maxDias)
        .map { (_, valores) ->
           val dateToConvert = Instant.ofEpochSecond(valores.first().dt)
           val label = labelFormat.format(dateToConvert)

            val min = valores.minOf { it.main.temp_min }
            val max = valores.maxOf { it.main.temp_max }

            DiaPronostico(
                label = label,
                tempMin = min,
                tempMax = max
            )
        }
}

@Composable
fun GraficoPronostico(dias: List<DiaPronostico>, modifier: Modifier = Modifier) {
    val minTemp = dias.minOf { it.tempMin }
    val maxTemp = dias.maxOf { it.tempMax }
    val range = (maxTemp - minTemp).takeIf { it != 0.0 } ?: 1.0

    val colorMax = MaterialTheme.colorScheme.primary
    val colorMin = MaterialTheme.colorScheme.secondary

    val tempReference = listOf(maxTemp, minTemp + range / 2, minTemp)

    Column(modifier = modifier){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                tempReference.forEach { temp ->
                    Text(
                        text = "${temp.roundToInt()}°",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                val width = size.width
                val height = size.height

                val topPadding = 15.dp.toPx()
                val bottomPadding = 25.dp.toPx()
                val usableHeight = height - topPadding - bottomPadding

                val stepX = if (dias.size > 1) width / (dias.size - 1) else 0f

                fun yFor(temp: Double): Float {
                    val fraction = ((temp - minTemp) / range).toFloat()
                    return height - bottomPadding - fraction * usableHeight
                }

                for (i in 0 until dias.lastIndex) {
                    val start = Offset(stepX * i, yFor(dias[i].tempMax))
                    val end = Offset(stepX * (i + 1), yFor(dias[i + 1].tempMax))
                    drawLine(
                        color = colorMax,
                        start = start,
                        end = end,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                for (i in 0 until dias.lastIndex) {
                    val start = Offset(stepX * i, yFor(dias[i].tempMin))
                    val end = Offset(stepX * (i + 1), yFor(dias[i + 1].tempMin))
                    drawLine(
                        color = colorMin,
                        start = start,
                        end = end,
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                dias.forEachIndexed { index, dia ->
                    val x = if (dias.size == 1) width / 2f else stepX * index

                    drawCircle(
                        color = colorMax,
                        radius = 5.dp.toPx(),
                        center = Offset(x, yFor(dia.tempMax))
                    )
                    drawCircle(
                        color = colorMin,
                        radius = 5.dp.toPx(),
                        center = Offset(x, yFor(dia.tempMin))
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            dias.forEach { dia ->
                Text(text = dia.label, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}