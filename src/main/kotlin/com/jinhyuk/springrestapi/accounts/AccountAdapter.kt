package com.jinhyuk.springrestapi.accounts

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

data class AccountAdapter(val account: Account): User(account.email, account.password, authorities(account.roles)) {
    companion object {
        fun authorities(roles: Set<AccountRole>): Collection<GrantedAuthority> =
                roles.map { SimpleGrantedAuthority("ROLE_${it.name}") }
    }
}