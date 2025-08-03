package com.autoever.assignment.dto.user

data class UserResponse(
    val account: String,
    val name: String,
    val rrn: String,
    val phone: String,
    val region: String
) {
    companion object {
        fun extractRegion(address: String): String {
            return address.split(" ").firstOrNull() ?: "UNKNOWN"
        }
    }
}
