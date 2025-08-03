package com.autoever.assignment.api.user

import com.autoever.assignment.dto.user.UserSignUpRequest
import com.autoever.assignment.service.user.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun signUp(@RequestBody @Valid request: UserSignUpRequest): ResponseEntity<Long> {
        val userId = userService.signUp(request)
        return ResponseEntity.ok(userId)
    }
}
