package com.autoever.assignment.dto.user

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank
    val account: String,

    @field:NotBlank
    val password: String
)
