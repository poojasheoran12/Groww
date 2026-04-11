package com.example.groww.data.remote.api

import com.example.groww.data.remote.dto.FundDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("mf/search")
    suspend fun searchFunds(@Query("q") query: String): List<FundDto>


//    @GET("mf/{schemeCode}")
//    suspend fun getFundDetails(@Path("schemeCode") schemeCode: Int): FundDetailsDto

    companion object {
        const val BASE_URL = "https://api.mfapi.in/"
    }
}
