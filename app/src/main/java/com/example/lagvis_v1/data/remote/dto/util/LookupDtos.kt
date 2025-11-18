package com.example.lagvis_v1.data.remote.dto.util

class LookupDtos {
    data class SimpleLookupResponse(
        val success: Boolean,
        val data: List<SimpleLookupItem>
    )

    data class SimpleLookupItem(
        val id: String,
        val nombre: String
    )

}