package com.example.trabajofinalappmoviles.presentacion.ciudades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabajofinalappmoviles.repository.modelos.Ciudad
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import kotlinx.coroutines.delay
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CiudadesView (
    modifier: Modifier = Modifier,
    state : CiudadesEstado,
    ubicando: Boolean,
    onAction: (CiudadesIntencion)->Unit
) {
    var value by rememberSaveable{ mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Open Istea Weather App") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "Ingrese nombre de ciudad",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    },
                    value = value,
                    onValueChange = {
                        value = it
                        onAction(CiudadesIntencion.Buscar(value))
                    },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    enabled = !ubicando,
                    onClick = { onAction(CiudadesIntencion.MiUbicacion) }
                ) {
                    Text(text = "Usar mi ubicación actual")
                }
                when (state) {
                    CiudadesEstado.Cargando -> CargandoLista()
                    CiudadesEstado.Inicial -> ListaInicial()
                    is CiudadesEstado.Error -> ErrorView(value = state.mensaje)
                    is CiudadesEstado.Resultado -> ListaDeCiudades(state.ciudades) {
                        onAction(
                            CiudadesIntencion.Seleccionar(it)
                        )
                    }
                    CiudadesEstado.Vacio -> ListaVacia(value = value)
                }
            }
        }

        if (ubicando) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 4.dp,
                        modifier = Modifier.height(60.dp)
                    )
                    Text(
                        text = "Obteniendo ubicación...",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDeCiudades(ciudades: List<Ciudad>, onSelect: (Ciudad) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp)
    ) {
        items(items = ciudades) { ciudad ->
            Card(
                onClick = { onSelect(ciudad) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Ubicación",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(34.dp)
                    )
                    Spacer(modifier = Modifier.width(50.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = ciudad.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (!ciudad.country.isNullOrEmpty()) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically

                            ){
                                Text(
                                    text = ciudad.country,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                AsyncImage(
                                    model = "https://flagsapi.com/${ciudad.country}/flat/32.png",
                                    contentDescription = "Bandera de ${ciudad.country}",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        if (!ciudad.state.isNullOrEmpty()) {
                            Text(
                                text = ciudad.state,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Ver clima",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(28.dp)
                    )

                }
            }
        }
    }
}

@Composable
fun ListaInicial() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "🏙️",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                "Busca una ciudad",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.typography.bodyMedium.color
            )
            Text(
                "Escribe el nombre de una ciudad para ver su clima",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ListaVacia(value: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "🔍",
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                "No encontramos ciudades con el nombre ${value}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

            Text(
                "💡 Verificá que el nombre esté bien escrito o probá con otra ciudad",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 10.dp)
            )

        }
    }
}

@Composable
fun CargandoLista() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var dotState by remember { mutableStateOf(0) }

        LaunchedEffect(Unit) {
            while (true) {
                delay(500)
                dotState = (dotState + 1) % 4
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Buscando${".".repeat(dotState)}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorView(value: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "❌",
                style = MaterialTheme.typography.displayLarge
            )

            Text(
                "ERROR",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "El mensaje del error es: ${value}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

        }
    }
}
