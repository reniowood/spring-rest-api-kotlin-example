package com.jinhyuk.springrestapi.configs

import com.jinhyuk.springrestapi.accounts.AccountService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
class SecurityConfig(val accountService: AccountService) : WebSecurityConfigurerAdapter() {
    @Bean
    override fun authenticationManagerBean(): AuthenticationManager = super.authenticationManagerBean()

    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun tokenStore(): TokenStore = InMemoryTokenStore()

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(accountService).passwordEncoder(passwordEncoder())
    }

    override fun configure(web: WebSecurity) {
        /*
            /docs/index.html -> /error -> /login으로 가기 때문에 /error도 추가해준다.
         */
        web.ignoring().mvcMatchers("/docs/**", "/error")
    }
}