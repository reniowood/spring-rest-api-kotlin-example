package com.jinhyuk.springrestapi.configs

import com.jinhyuk.springrestapi.accounts.AccountService
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore

@Configuration @EnableAuthorizationServer
class AuthServerConfig(
        val accountService: AccountService,
        val passwordEncoder: PasswordEncoder,
        val authenticationManager: AuthenticationManager,
        val tokenStore: TokenStore,
        val myAppProperties: MyAppProperties
): AuthorizationServerConfigurerAdapter() {
    override fun configure(security: AuthorizationServerSecurityConfigurer) {
        security.passwordEncoder(passwordEncoder)
    }

    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
                .withClient(myAppProperties.clientId)
                .secret(passwordEncoder.encode(myAppProperties.clientSecret))
                .scopes("write", "read")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(10 * 60)
                .refreshTokenValiditySeconds(60 * 60)
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints.tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .userDetailsService(accountService)
    }
}