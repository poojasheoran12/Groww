package com.example.groww.presentation.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groww.domain.model.Fund
import com.example.groww.domain.repository.FundRepository
import com.example.groww.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: FundRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Resource<Map<String, List<Fund>>>>(Resource.Loading)
    val uiState = _uiState.asStateFlow()

    private var cachedData: Map<String, List<Fund>>? = null

    init {
        fetchExploreData()
    }

    fun fetchExploreData() {
        if (cachedData != null) {
            _uiState.value = Resource.Success(cachedData!!)
            return
        }

        viewModelScope.launch {
            _uiState.value = Resource.Loading
            try {
                // Parrallel API Calls for 4 categories
                val indexDef = async { repository.searchFunds("index") }
                val bluechipDef = async { repository.searchFunds("bluechip") }
                val taxDef = async { repository.searchFunds("tax") }
                val largeDef = async { repository.searchFunds("large") }

                val results = awaitAll(indexDef, bluechipDef, taxDef, largeDef)
                
                val categories = mapOf(
                    "Index Funds" to results[0].getOrThrow().take(50),
                    "Bluechip Funds" to results[1].getOrThrow().take(50),
                    "Tax Saver (ELSS)" to results[2].getOrThrow().take(50),
                    "Large Cap Funds" to results[3].getOrThrow().take(50)
                )

                cachedData = categories
                _uiState.value = Resource.Success(categories)


            } catch (e: Exception) {
                _uiState.value = Resource.Error(e.localizedMessage ?: "Failed to fetch funds")
            }
        }
    }



}
