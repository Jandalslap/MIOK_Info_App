import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miok_info_app.data.InformationRepository
import com.example.miok_info_app.viewmodel.HomeViewModel
import com.example.miok_info_app.viewmodel.SharedViewModel

class HomeViewModelFactory(
    private val repository: InformationRepository,
    private val sharedViewModel: SharedViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, sharedViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

