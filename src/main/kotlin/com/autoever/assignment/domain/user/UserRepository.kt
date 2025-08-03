package com.autoever.assignment.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun existsByAccount(account: String): Boolean
    fun existsByRrn(rrn: String): Boolean
}
