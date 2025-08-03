package com.autoever.assignment.service.message

import com.autoever.assignment.dto.message.MessageRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class MessageQueueService(
    private val redisTemplate: StringRedisTemplate,
    private val objectMapper: ObjectMapper
) {
    private val queueKey = "message:queue"

    fun enqueue(request: MessageRequest) {
        val json = objectMapper.writeValueAsString(request)
        redisTemplate.opsForList().rightPush(queueKey, json)
    }
}
