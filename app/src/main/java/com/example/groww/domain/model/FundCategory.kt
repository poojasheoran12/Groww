package com.example.groww.domain.model

enum class FundCategory(val displayName: String) {
    ALL("All Funds"),
    INDEX("Index"),
    BLUECHIP("Bluechip"),
    TAX("Tax"),
    LARGE_CAP("Large"),
    SEARCH("Search");

    companion object {
        fun fromDisplayName(name: String): FundCategory {
            return entries.find { it.displayName == name } ?: ALL
        }
    }
}
