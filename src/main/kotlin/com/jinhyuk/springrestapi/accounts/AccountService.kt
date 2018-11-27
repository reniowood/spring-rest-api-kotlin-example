package com.jinhyuk.springrestapi.accounts

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountService(val accountRepository: AccountRepository): UserDetailsService {
    @Autowired lateinit var passwordEncoder: PasswordEncoder

    override fun loadUserByUsername(username: String?): UserDetails {
        val account = accountRepository.findByEmail(username) ?: throw UsernameNotFoundException(username)

        return User(account.email, account.password, authorities(account.roles))
    }

    fun createAccount(account: Account) {
        val passwordEncodedAccount = account.copy(password = passwordEncoder.encode(account.password))

        accountRepository.save(passwordEncodedAccount)
    }

    private fun authorities(roles: Set<AccountRole>): Collection<out GrantedAuthority> =
            roles.map { SimpleGrantedAuthority("ROLE_${it.name}") }
}