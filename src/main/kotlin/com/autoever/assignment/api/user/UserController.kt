package com.autoever.assignment.api.user

import com.autoever.assignment.dto.user.LoginRequest
import com.autoever.assignment.dto.user.UserResponse
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

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: LoginRequest): ResponseEntity<String> {
        return if (userService.login(request.account, request.password)) {
            ResponseEntity.ok("Login successful")
        } else {
            ResponseEntity.status(401).body("Invalid credentials")
        }
    }

    @GetMapping("/me")
    fun getMyInfo(@RequestHeader("X-Account") account: String): ResponseEntity<UserResponse> {
        val user = userService.getUserByAccount(account)
        return ResponseEntity.ok(user)
    }

}
