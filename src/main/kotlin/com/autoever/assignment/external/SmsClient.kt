package com.autoever.assignment.external

import org.springframework.http.*
import org.springframework.stereotype.Component
import java.util.*

@Component
class SmsClient {

    private val endpoint = "http://localhost:8082/sms"

    fun send(phone: String, message: String): Boolean {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            val encodedAuth = Base64.getEncoder().encodeToString("autoever:5678".toByteArray())
            set("Authorization", "Basic $encodedAuth")
        }

        val body = "message=$message"
        val request = HttpEntity(body, headers)

        val simulatedStatus = simulateResponseCode()
        println("[SMS] REQUEST to $endpoint?phone=$phone with $body")
        println("[SMS] MOCK RESPONSE $simulatedStatus for $phone")

        return simulatedStatus == HttpStatus.OK
    }

    private fun simulateResponseCode(): HttpStatus {
        val chance = (1..10).random()
        return when {
            chance <= 8 -> HttpStatus.OK
            chance == 9 -> HttpStatus.BAD_REQUEST
            else -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}
