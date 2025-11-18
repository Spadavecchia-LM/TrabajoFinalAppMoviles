package com.example.trabajofinalappmoviles.presentacion.clima.actual

import com.example.trabajofinalappmoviles.repository.api.Repositorio
import com.example.trabajofinalappmoviles.repository.modelos.*
import com.example.trabajofinalappmoviles.router.Router
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class ClimaViewModelTest {

    private lateinit var viewModel: ClimaViewModel
    private lateinit var repositorio: Repositorio
    private lateinit var router: Router

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repositorio = mockk()
        router = mockk(relaxed = true)

        viewModel = ClimaViewModel(
            repositorio = repositorio,
            router = router,
            lat = -34.6f,
            lon = -58.4f,
            nombre = "Buenos Aires"
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // TEST 1: verificar que actualizacion
    @Test
    fun testActualizarClimaCambiarEstadoACargando() = runTest {
        // Act
        viewModel.ejecutar(ClimaIntencion.actualizarClima)

        // Assert
        Assert.assertTrue(viewModel.uiState is ClimaEstado.Cargando)
    }

    // TEST 2: el repositorio devuelve clima → estado Exitoso
    @Test
    fun testTraerClimaExitoso() = runTest {

        val climaSimulado = Clima(
            base = "stations",
            name = "Buenos Aires",
            coord = Coord(-58.4, -34.6),
            weather = listOf(Weather(800, "Clear", "cielo despejado", "01d")),
            main = Main(
                temp = 22.5,
                feels_like = 23.0,
                temp_min = 18.0,
                temp_max = 25.0,
                pressure = 1000,
                humidity = 60
            ),
            wind = Wind(2.5, 120),
            clouds = Clouds(5),
        )

        coEvery { repositorio.obtenerClimaCiudad(any(), any()) } returns climaSimulado

        viewModel.ejecutar(ClimaIntencion.actualizarClima)

        advanceUntilIdle()

        Assert.assertTrue(viewModel.uiState is ClimaEstado.Exitoso)
        val estado = viewModel.uiState as ClimaEstado.Exitoso

        Assert.assertEquals("Buenos Aires", estado.ciudad)
        Assert.assertEquals(22.5, estado.temperatura, 0.01)
        Assert.assertEquals("cielo despejado", estado.descripcion)
        Assert.assertEquals(23.0, estado.st, 0.01)
        Assert.assertEquals(18.0, estado.tempMin, 0.01)
        Assert.assertEquals(25.0, estado.tempMax, 0.01)
        Assert.assertEquals(60, estado.humedad)
    }

    // TEST 3: repositorio lanza error → estado Error
    @Test
    fun testTraerClimaError() = runTest {

        coEvery { repositorio.obtenerClimaCiudad(any(), any()) } throws Exception("Fallo API")

        viewModel.ejecutar(ClimaIntencion.actualizarClima)

        advanceUntilIdle()

        Assert.assertTrue(viewModel.uiState is ClimaEstado.Error)
        val estado = viewModel.uiState as ClimaEstado.Error

        Assert.assertEquals("Fallo API", estado.mensaje)
    }
}
