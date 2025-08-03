package com.autoever.assignment.service.message

import com.autoever.assignment.dto.message.Channel
import com.autoever.assignment.dto.message.MessageRequest
import com.autoever.assignment.dto.message.Result
import com.autoever.assignment.external.KakaoClient
import com.autoever.assignment.external.SmsClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class MessageDispatcherService(
    private val redisTemplate: StringRedisTemplate,
    private val kakaoClient: KakaoClient,
    private val smsClient: SmsClient,
    private val objectMapper: ObjectMapper
) {
    private val queueKey = "message:queue"

    // TPS 제한값
    private val maxKakaoPerMinute = 100
    private val maxSmsPerMinute = 500

    // 멀티 워커 설정
    private val threadCount = 5
    private val pollIntervalMillis = 100L

    private val scheduler = ThreadPoolTaskScheduler()

    @PostConstruct
    fun init() {
        scheduler.poolSize = threadCount
        scheduler.setThreadNamePrefix("dispatcher-")
        scheduler.initialize()

        repeat(threadCount) { workerId ->
            scheduler.scheduleAtFixedRate(
                { pollAndSend(workerId) },
                Duration.ofMillis(pollIntervalMillis)
            )
        }
    }

    private fun pollAndSend(workerId: Int) {
        val json = redisTemplate.opsForList().leftPop(queueKey) ?: return
        val request = objectMapper.readValue(json, MessageRequest::class.java)
        println("[$workerId] 메시지 처리 시작: ${request.phone}")

        if (acquirePermit("kakao", maxKakaoPerMinute)) {
            val kakaoSent = kakaoClient.send(request.phone, request.message)
            if (kakaoSent) {
                log(request, Channel.KAKAO, Result.SUCCESS)
                return
            }
        }

        if (acquirePermit("sms", maxSmsPerMinute)) {
            val smsSent = smsClient.send(request.phone, request.message)
            log(request, Channel.SMS, if (smsSent) Result.SUCCESS else Result.FAIL)
        } else {
            println("[$workerId] SMS TPS 초과 - 전송 불가: ${request.phone}")
        }
    }

    /**
     * Redis 기반 TPS 제한 제어
     */
    private fun acquirePermit(channel: String, maxPerMinute: Int): Boolean {
        val now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        val key = "tps:$channel:$now"
        val valueOps = redisTemplate.opsForValue()

        val count = valueOps.increment(key) ?: return false
        if (count == 1L) {
            redisTemplate.expire(key, Duration.ofMinutes(1))
        }

        return count <= maxPerMinute
    }

    private fun log(req: MessageRequest, channel: Channel, result: Result) {
        println(
            "[DISPATCH] [${channel.name}] [${result.name}] to ${req.phone} (${req.account})\n> ${req.message}"
        )
    }
}
