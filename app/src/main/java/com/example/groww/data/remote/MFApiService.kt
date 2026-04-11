package com.example.groww.data.remote

import com.example.groww.data.remote.dto.FundDto
import com.example.groww.domain.model.FundDetail
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MFApiService {
    
    // Search API: https://api.mfapi.in/mf/search?q={query}
    @GET("mf/search")
    suspend fun searchFunds(@Query("q") query: String): List<FundDto>

    // Fund Details & NAV History: https://api.mfapi.in/mf/{scheme_code}
    @GET("mf/{schemeCode}")
    suspend fun getFundDetails(@Path("schemeCode") schemeCode: Int): FundDetail

    companion object {
        const val BASE_URL = "https://api.mfapi.in/"
    }
}
