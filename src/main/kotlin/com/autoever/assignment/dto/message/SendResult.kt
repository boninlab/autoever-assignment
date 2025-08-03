package com.autoever.assignment.dto.message

enum class Channel {
    KAKAO, SMS
}

enum class Result {
    SUCCESS, FAIL
}

data class SendResult(
    val account: String,
    val phone: String,
    val channel: Channel,
    val result: Result,
    val message: String
)
