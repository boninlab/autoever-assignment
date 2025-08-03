package com.autoever.assignment.service.message

import com.autoever.assignment.dto.message.Channel
import com.autoever.assignment.dto.message.MessageRequest
import com.autoever.assignment.dto.message.Result
import com.autoever.assignment.dto.message.SendResult
import com.autoever.assignment.external.KakaoClient
import com.autoever.assignment.external.SmsClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.util.concurrent.RateLimiter
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class MessageDispatcherService(
    private val redisTemplate: StringRedisTemplate,
    private val kakaoClient: KakaoClient,
    private val smsClient: SmsClient,
    private val objectMapper: ObjectMapper
) {
    private val queueKey = "message:queue"
    private val kakaoLimiter = RateLimiter.create(100.0 / 60) // 카카오 TPS 제한
    private val smsLimiter = RateLimiter.create(500.0 / 60)   // SMS TPS 제한

    @Scheduled(fixedDelay = 100) // 100ms 간격으로 계속 polling
    fun pollAndSend() {
        val json = redisTemplate.opsForList().leftPop(queueKey) ?: return

        val request = objectMapper.readValue(json, MessageRequest::class.java)

        kakaoLimiter.acquire()
        val kakaoSent = kakaoClient.send(request.phone, request.message)

        if (kakaoSent) {
            log(request, Channel.KAKAO, Result.SUCCESS)
        } else {
            smsLimiter.acquire()
            val smsSent = smsClient.send(request.phone, request.message)
            log(request, Channel.SMS, if (smsSent) Result.SUCCESS else Result.FAIL)
        }
    }

    private fun log(req: MessageRequest, channel: Channel, result: Result) {
        println("[DISPATCH] [${channel.name}] [${result.name}] to ${req.phone} (${req.account})\n> ${req.message}")
    }
}
