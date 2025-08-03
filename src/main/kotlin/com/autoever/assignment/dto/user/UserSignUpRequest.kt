package com.autoever.assignment.dto.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserSignUpRequest(
    @field:NotBlank
    val account: String,

    @field:NotBlank
    @field:Size(min = 6)
    val password: String,

    @field:NotBlank
    val name: String,

    @field:NotBlank
    @field:Size(min = 13, max = 13)
    val rrn: String,

    @field:NotBlank
    @field:Size(min = 11, max = 11)
    val phone: String,

    @field:NotBlank
    val address: String
)
