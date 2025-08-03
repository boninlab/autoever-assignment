package com.autoever.assignment.service.message

import com.autoever.assignment.domain.user.UserRepository
import com.autoever.assignment.dto.message.Channel
import com.autoever.assignment.dto.message.Result
import com.autoever.assignment.dto.message.SendResult
import com.autoever.assignment.external.KakaoClient
import com.autoever.assignment.external.SmsClient
import com.google.common.util.concurrent.RateLimiter
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class MessageService(
    private val userRepository: UserRepository,
    private val kakaoClient: KakaoClient,
    private val smsClient: SmsClient
) {
    // TPS 제한 설정: 분당 → 초당 환산
    private val kakaoLimiter = RateLimiter.create(100.0 / 60) // 100 req/min
    private val smsLimiter = RateLimiter.create(500.0 / 60)   // 500 req/min

    fun sendMessagesToAllUsers(ageGroupMessages: Map<Int, String>): List<SendResult> {
        val users = userRepository.findAll()
        val results = mutableListOf<SendResult>()

        for (user in users) {
            val ageGroup = calculateAgeGroup(user.rrn)
            val messageBody = "${user.name}님, 안녕하세요. 현대 오토에버입니다.\n" +
                    (ageGroupMessages[ageGroup] ?: "모든 연령을 위한 혜택을 확인하세요.")

            kakaoLimiter.acquire()
            val kakaoSent = kakaoClient.send(user.phone, messageBody)

            if (kakaoSent) {
                results.add(
                    SendResult(
                        account = user.account,
                        phone = user.phone,
                        channel = Channel.KAKAO,
                        result = Result.SUCCESS,
                        message = messageBody
                    )
                )
            } else {
                smsLimiter.acquire()
                val smsSent = smsClient.send(user.phone, messageBody)
                results.add(
                    SendResult(
                        account = user.account,
                        phone = user.phone,
                        channel = Channel.SMS,
                        result = if (smsSent) Result.SUCCESS else Result.FAIL,
                        message = messageBody
                    )
                )
            }
        }

        return results
    }

    private fun calculateAgeGroup(rrn: String): Int {
        val birthPart = rrn.substring(0, 6)
        val genderCode = rrn.getOrNull(6)

        val yearPrefix = when (genderCode) {
            '1', '2', '5', '6' -> 1900
            '3', '4', '7', '8' -> 2000
            else -> 1900 // fallback
        }

        val year = yearPrefix + birthPart.substring(0, 2).toInt()
        val month = birthPart.substring(2, 4).toInt()
        val day = birthPart.substring(4, 6).toInt()

        val birthDate = LocalDate.of(year, month, day)
        val now = LocalDate.now()

        val age = now.year - birthDate.year
        return (age / 10) * 10
    }


}
