package com.example.groww.data.remote.api

import com.example.groww.data.remote.dto.FundDetailDto
import com.example.groww.data.remote.dto.SearchResultDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MutualFundApi {
    @GET("mf/search")
    suspend fun searchFunds(@Query("q") query: String): List<SearchResultDto>

    @GET("mf/{id}")
    suspend fun getFundDetails(@Path("id") id: Int): FundDetailDto

    companion object {
        const val BASE_URL = "https://api.mfapi.in/"
    }
}
