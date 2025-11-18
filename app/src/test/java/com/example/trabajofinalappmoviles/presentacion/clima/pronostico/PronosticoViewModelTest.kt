package com.example.trabajofinalappmoviles.presentacion.clima.pronostico

import PronosticoViewModel
import com.example.trabajofinalappmoviles.repository.api.Repositorio
import com.example.trabajofinalappmoviles.repository.modelos.ListForecast
import com.example.trabajofinalappmoviles.repository.modelos.MainForecast
import com.example.trabajofinalappmoviles.router.Router
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class PronosticoViewModelTest {

    private lateinit var viewModel: PronosticoViewModel
    private lateinit var repositorio: Repositorio
    private lateinit var router: Router

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        repositorio = mockk()
        router = mockk(relaxed = true)

        viewModel = PronosticoViewModel(
            repositorio = repositorio,
            router = router,
            nombre = "Buenos Aires",
            lat = -34.6f,
            lon = -58.4f
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // TEST 1: verificar que actualiza
    @Test
    fun testActualizarPronosticoCambiaEstadoACargando() = runTest {
        viewModel.ejecutar(PronosticoIntencion.actualizarClima)

        Assert.assertTrue(viewModel.uiState is PronosticoEstado.Cargando)
    }

    // TEST 2: cuando el repositorio devuelve datos
    @Test
    fun testPronosticoExitoso() = runTest {

        val listaPronostico = listOf(
            ListForecast(
                dt = 123456,
                main = MainForecast(
                    temp = 22.0,
                    feels_like = 23.0,
                    temp_min = 18.0,
                    temp_max = 26.0,
                    pressure = 1000,
                    sea_level = 1000,
                    grnd_level = 990,
                    humidity = 40,
                    temp_kf = 0.0
                )
            )
        )

        coEvery { repositorio.obtenerPronosticoPorCoord(any(), any()) } returns listaPronostico

        viewModel.ejecutar(PronosticoIntencion.actualizarClima)

        advanceUntilIdle()

        Assert.assertTrue(viewModel.uiState is PronosticoEstado.Exitoso)
        val estado = viewModel.uiState as PronosticoEstado.Exitoso

        Assert.assertEquals(1, estado.climas.size)
    }

    // TEST 3: cuando el repositorio lanza excepción
    @Test
    fun testPronosticoError() = runTest {

        coEvery { repositorio.obtenerPronosticoPorCoord(any(), any()) } throws Exception("Fallo API")

        viewModel.ejecutar(PronosticoIntencion.actualizarClima)

        advanceUntilIdle()

        Assert.assertTrue(viewModel.uiState is PronosticoEstado.Error)
        val estado = viewModel.uiState as PronosticoEstado.Error

        Assert.assertEquals("Fallo API", estado.mensaje)
    }
}
