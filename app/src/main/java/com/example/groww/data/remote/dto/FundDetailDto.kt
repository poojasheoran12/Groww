package com.example.groww.data.remote.dto

//import com.example.groww.domain.model.FundDetail
//import com.example.groww.domain.model.FundMeta
//import com.example.groww.domain.model.NavHistory
import kotlinx.serialization.Serializable

//@Serializable
//data class FundDetailDto(
//    val meta: FundMetaDto,
//    val data: List<NavHistoryDto>
//)
//
//@Serializable
//data class FundMetaDto(
//    val fund_house: String,
//    val scheme_type: String,
//    val scheme_category: String?,
//    val scheme_code: Int,
//    val scheme_name: String,
//    val isin_growth: String? = null,
//    val isin_div_reinvestment: String? = null
//)
//
//@Serializable
//data class NavHistoryDto(
//    val date: String,
//    val nav: String
//)
//
//fun FundDetailDto.toDomain(): FundDetail {
//    return FundDetail(
//        meta = meta.toDomain(),
//        data = data.map { it.toDomain() }
//    )
//}
//
//fun FundMetaDto.toDomain(): FundMeta {
//    return FundMeta(
//        fundHouse = fund_house,
//        schemeType = scheme_type,
//        schemeCategory = scheme_category,
//        schemeCode = scheme_code,
//        schemeName = scheme_name,
//        isinGrowth = isin_growth,
//        isinDivReinvestment = isin_div_reinvestment
//    )
//}
//
//fun NavHistoryDto.toDomain(): NavHistory {
//    return NavHistory(
//        date = date,
//        nav = nav
//    )
//}