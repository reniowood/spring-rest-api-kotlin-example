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

        return AccountAdapter(account)
    }

    fun createAccount(account: Account): Account {
        val passwordEncodedAccount = account.copy(password = passwordEncoder.encode(account.password))

        return accountRepository.save(passwordEncodedAccount)
    }
}