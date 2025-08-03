package com.autoever.assignment.domain.user

import jakarta.persistence.*

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["account"]),
        UniqueConstraint(columnNames = ["rrn"])
    ]
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val account: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, unique = true, length = 13)
    val rrn: String,

    @Column(nullable = false, length = 11)
    val phone: String,

    @Column(nullable = false)
    var address: String
)
