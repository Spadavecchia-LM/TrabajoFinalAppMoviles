import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.trabajofinalappmoviles.presentacion.clima.pronostico.PronosticoEstado
import com.example.trabajofinalappmoviles.presentacion.clima.pronostico.PronosticoIntencion
import com.example.trabajofinalappmoviles.repository.api.Repositorio
import com.example.trabajofinalappmoviles.router.Router
import kotlinx.coroutines.launch

class PronosticoViewModel(
    val repositorio: Repositorio,
    val router: Router,
    val nombre: String
) : ViewModel() {

    var uiState by mutableStateOf<PronosticoEstado>(PronosticoEstado.Vacio)

    fun ejecutar(intencion: PronosticoIntencion){
        when(intencion){
            PronosticoIntencion.actualizarClima -> traerPronostico()
        }
    }

    fun traerPronostico() {
        uiState = PronosticoEstado.Cargando
        viewModelScope.launch {
            try{
                val forecast = repositorio.obtenerPronosticoCiudad(nombre).filter {
                    //TODO agregar logica de filtrado
                    true
                }
                uiState = PronosticoEstado.Exitoso(forecast)
            } catch (exception: Exception){
                uiState = PronosticoEstado.Error(exception.localizedMessage ?: "error desconocido")
            }
        }
    }

}

class PronosticoViewModelFactory(
    private val repositorio: Repositorio,
    private val router: Router,
    private val nombre: String,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PronosticoViewModel::class.java)) {
            return PronosticoViewModel(repositorio,router,nombre) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}