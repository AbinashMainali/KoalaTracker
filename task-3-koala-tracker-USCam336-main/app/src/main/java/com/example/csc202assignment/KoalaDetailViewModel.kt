package com.example.csc202assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class KoalaDetailViewModel(koalaId: UUID): ViewModel() {
    private val koalaRepository = KoalaRepository.get()

    private val _koala: MutableStateFlow<Koala?> = MutableStateFlow(null)
    val koala: StateFlow<Koala?> = _koala.asStateFlow()

    init {
        viewModelScope.launch {
            _koala.value = koalaRepository.getKoala(koalaId)
        }
    }

    fun updateKoala(onUpdate: (Koala) -> Koala) {
        _koala.update { oldCrime ->
            oldCrime?.let { onUpdate(it) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        koala.value?.let { koalaRepository.updateKoala(it) }
    }
}

class KoalaDetailViewModelFactory(
    private val koalaId: UUID
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return KoalaDetailViewModel(koalaId) as T
    }
}

