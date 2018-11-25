package com.jinhyuk.springrestapi.accounts

import javax.persistence.*

@Entity
data class Account(
        @Id @GeneratedValue val id: Int? = null,
        val email: String,
        val password: String,
        @ElementCollection(fetch = FetchType.EAGER)
        @Enumerated(EnumType.STRING)
        val roles: Set<AccountRole>
)

enum class AccountRole {
    USER, ADMIN
}