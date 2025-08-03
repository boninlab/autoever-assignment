package com.autoever.assignment.api.admin

import com.autoever.assignment.dto.message.SendResult
import com.autoever.assignment.service.message.MessageService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/messages")
class AdminMessageController(
    private val messageService: MessageService
) {

    @PostMapping("/send")
    fun sendMessages(@RequestBody messageMap: Map<Int, String>): ResponseEntity<List<SendResult>> {
        val results = messageService.sendMessagesToAllUsers(messageMap)
        return ResponseEntity.ok(results)
    }

}
