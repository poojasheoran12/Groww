package com.example.groww.domain.model

enum class FundCategory(val displayName: String, val apiQuery: String) {
    INDEX("Index", "index"),
    BLUECHIP("Bluechip", "bluechip"),
    TAX("Tax", "tax"),
    LARGE_CAP("Large", "large"),
    SEARCH("Search", "");

    companion object {
        fun fromDisplayName(name: String): FundCategory {
            return entries.find { it.displayName == name } ?: SEARCH
        }
    }
}
