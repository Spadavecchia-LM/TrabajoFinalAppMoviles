package com.example.trabajofinalappmoviles.presentacion.ciudades

import com.example.trabajofinalappmoviles.repository.api.Repositorio
import com.example.trabajofinalappmoviles.repository.modelos.Ciudad
import com.example.trabajofinalappmoviles.router.Router
import com.example.trabajofinalappmoviles.router.Ruta
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.*

class CiudadesViewModelTest {

    private lateinit var viewModel: CiudadesViewModel
    private lateinit var repositorio: Repositorio
    private lateinit var router: Router
    private lateinit var preferencias: PreferenciasCiudad

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)

        repositorio = mockk()
        router = mockk(relaxed = true)
        preferencias = mockk(relaxed = true)
        viewModel = CiudadesViewModel(repositorio, router, preferencias)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // TEST 1 - cuando se busca una ciudad vacia se vuelve al estado Inicial
    @Test
    fun testBuscarCiudadVaciaVuelveEstadoInicial() = runTest {
        viewModel.ejecutar(CiudadesIntencion.Buscar(""))
        assert(viewModel.uiState is CiudadesEstado.Inicial)
    }

    // TEST 2 - buscar ciudad con resultados actualiza estado Resultado
    @Test
    fun testBuscarCiudadConResultados() = runTest {
        val listaCiudades = listOf(
            Ciudad("Buenos Aires", -34.6f, -58.4f, "AR")
        )
        coEvery { repositorio.buscarCiudad("buenos aires") } returns listaCiudades

        viewModel.ejecutar(CiudadesIntencion.Buscar("buenos aires"))

        advanceUntilIdle()

        assert(viewModel.uiState is CiudadesEstado.Resultado)
        val estado = viewModel.uiState as CiudadesEstado.Resultado
        Assert.assertEquals(1, estado.ciudades.size)
    }

    // TEST 3 - buscar ciudad sin resultados muestra estado Vacio
    @Test
    fun testBuscarCiudadSinResultados() = runTest {
        coEvery { repositorio.buscarCiudad("aaaaaaa") } returns emptyList()

        viewModel.ejecutar(CiudadesIntencion.Buscar("aaaaaaa"))

        advanceUntilIdle()

        Assert.assertEquals(CiudadesEstado.Vacio, viewModel.uiState)
    }

    // TEST 4 - cuando buscarCiudad lanza excepcion se muestra estado Error
    @Test
    fun testBuscarCiudadConError() = runTest {
        coEvery { repositorio.buscarCiudad("roma") } throws Exception("Fallo API")

        viewModel.ejecutar(CiudadesIntencion.Buscar("roma"))

        advanceUntilIdle()

        assert(viewModel.uiState is CiudadesEstado.Error)
        val estado = viewModel.uiState as CiudadesEstado.Error
        Assert.assertEquals("Fallo API", estado.mensaje)
    }


    // TEST 5 - al seleccionar ciudad se llama a router-navegar
    @Test
    fun testSeleccionCiudadNavegaCorrectamente() = runTest {
        val ciudad = Ciudad("Luján", -34.57f, -59.11f, "AR")

        viewModel.ejecutar(CiudadesIntencion.Seleccionar(ciudad))

        verify {
            router.navegar(
                withArg { ruta ->
                    val clima = ruta as Ruta.Clima
                    Assert.assertEquals(ciudad.lat, clima.lat, 0.0f)
                    Assert.assertEquals(ciudad.lon, clima.lon, 0.0f)
                    Assert.assertEquals(ciudad.name, clima.nombre)
                }
            )
        }
    }
}
