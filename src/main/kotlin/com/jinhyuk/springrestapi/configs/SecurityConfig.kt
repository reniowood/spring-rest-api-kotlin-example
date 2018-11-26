package com.jinhyuk.springrestapi.configs

import com.jinhyuk.springrestapi.accounts.AccountService
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
class SecurityConfig(val accountService: AccountService) : WebSecurityConfigurerAdapter() {
    override fun configure(web: WebSecurity) {
        /*
            /docs/index.html -> /error -> /login으로 가기 때문에 /error도 추가해준다.
         */
        web.ignoring().mvcMatchers("/docs/**", "/error")
    }
}