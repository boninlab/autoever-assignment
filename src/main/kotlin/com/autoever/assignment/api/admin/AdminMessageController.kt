package com.autoever.assignment.api.admin

import com.autoever.assignment.dto.message.MessageRequest
import com.autoever.assignment.service.message.MessageQueueService
import com.autoever.assignment.domain.user.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.Period

@RestController
@RequestMapping("/admin/messages")
class AdminMessageController(
    private val userRepository: UserRepository,
    private val messageQueueService: MessageQueueService
) {

    @PostMapping("/send")
    fun enqueueMessages(@RequestBody messageMap: Map<Int, String>): ResponseEntity<String> {
        val users = userRepository.findAll()

        users.forEach { user ->
            val ageGroup = calculateAgeGroup(user.rrn)
            val message = "${user.name}님, 안녕하세요. 현대 오토에버입니다.\n" +
                    (messageMap[ageGroup] ?: "모든 연령을 위한 혜택을 확인하세요.")

            val request = MessageRequest(
                account = user.account,
                phone = user.phone,
                message = message
            )

            messageQueueService.enqueue(request)
        }

        return ResponseEntity.ok("총 ${users.size}명 대상 메시지를 큐에 등록했습니다.")
    }

    private fun calculateAgeGroup(rrn: String): Int {
        val birth = rrn.take(6)
        val genderCode = rrn.getOrNull(6)

        val yearPrefix = when (genderCode) {
            '1', '2', '5', '6' -> 1900
            '3', '4', '7', '8' -> 2000
            else -> 1900
        }

        val year = yearPrefix + birth.substring(0, 2).toInt()
        val month = birth.substring(2, 4).toInt()
        val day = birth.substring(4, 6).toInt()

        val birthDate = LocalDate.of(year, month, day)
        val now = LocalDate.now()

        val age = Period.between(birthDate, now).years
        return (age / 10) * 10
    }
}
