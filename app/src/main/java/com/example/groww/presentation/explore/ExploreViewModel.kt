package com.example.groww.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Fund
import com.example.groww.domain.usecase.GetExploreFundsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getExploreFundsUseCase: GetExploreFundsUseCase
) : ViewModel() {

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    val exploreState: StateFlow<Map<String, List<Fund>>> = getExploreFundsUseCase()
        .map { data -> 
            data.mapKeys { it.key.displayName } 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _loadingState.value = true
            try {
                getExploreFundsUseCase.cleanup()
                getExploreFundsUseCase.sync()
            } catch (e: Exception) {
                // Background sync error, UI will still show cached DB data
            } finally {
                _loadingState.value = false
            }
        }
    }
}
