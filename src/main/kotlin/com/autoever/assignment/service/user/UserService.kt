package com.autoever.assignment.service.user

import com.autoever.assignment.domain.user.User
import com.autoever.assignment.domain.user.UserRepository
import com.autoever.assignment.dto.user.UserSignUpRequest
import com.autoever.assignment.dto.user.UserUpdateRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun signUp(request: UserSignUpRequest): Long {
        if (userRepository.existsByAccount(request.account)) {
            throw IllegalArgumentException("이미 사용 중인 계정입니다.")
        }
        if (userRepository.existsByRrn(request.rrn)) {
            throw IllegalArgumentException("이미 등록된 주민등록번호입니다.")
        }

        val user = User(
            account = request.account,
            password = request.password,
            name = request.name,
            rrn = request.rrn,
            phone = request.phone,
            address = request.address
        )

        return userRepository.save(user).id
    }

    @Transactional(readOnly = true)
    fun getUsers(pageRequest: PageRequest): Page<User> {
        return userRepository.findAll(pageRequest)
    }

    @Transactional
    fun updateUser(id: Long, request: UserUpdateRequest) {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("존재하지 않는 사용자입니다.") }
        if (request.password != null) {
            user.password = request.password
        }
        if (request.address != null) {
            user.address = request.address
        }
    }

    @Transactional
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("존재하지 않는 사용자입니다.")
        }
        userRepository.deleteById(id)
    }
}
