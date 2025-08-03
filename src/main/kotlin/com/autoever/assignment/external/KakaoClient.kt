package com.autoever.assignment.external

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.*

@Component
class KakaoClient {

    private val endpoint = "http://localhost:8081/kakaotalk-messages"

    fun send(phone: String, message: String): Boolean {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            val encodedAuth = Base64.getEncoder().encodeToString("autoever:1234".toByteArray())
            set("Authorization", "Basic $encodedAuth")
        }

        val body = mapOf("phone" to phone, "message" to message)
        val request = HttpEntity(body, headers)

        val simulatedStatus = simulateResponseCode()
        println("[KAKAO] REQUEST to $endpoint with $body")
        println("[KAKAO] MOCK RESPONSE $simulatedStatus for $phone")

        return simulatedStatus == HttpStatus.OK
    }

    private fun simulateResponseCode(): HttpStatus {
        val chance = (1..10).random()
        return when {
            chance <= 5 -> HttpStatus.OK
            chance == 6 -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}
