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

    private val _exploreState = MutableStateFlow<Map<String, List<Fund>>>(emptyMap())
    val exploreState = _exploreState.asStateFlow()

    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            _loadingState.value = true
            getExploreFundsUseCase.cleanup()
            val data = getExploreFundsUseCase()
            _exploreState.value = data.mapKeys { it.key.displayName }
            _loadingState.value = false
        }
    }
}
