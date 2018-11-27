package com.jinhyuk.springrestapi.config

import com.jinhyuk.springrestapi.accounts.Account
import com.jinhyuk.springrestapi.accounts.AccountRole
import com.jinhyuk.springrestapi.accounts.AccountService
import com.jinhyuk.springrestapi.configs.MyAppProperties
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var accountService: AccountService
    @Autowired lateinit var myAppProperties: MyAppProperties

    @Test
    @DisplayName("/docs 아래의 파일은 인증이 필요 없이 보여준다.")
    fun testAccessingDocs() {
        mockMvc.perform(get("/docs/index.html"))
                .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("저장된 email과 password로 access token을 가져온다.")
    fun testAccessToken() {
        // Given
        val email = "test@email.com"
        val password = "test"
        val account = Account(email = email, password = password, roles = setOf(AccountRole.ADMIN))
        accountService.createAccount(account)

        // When
        val result = mockMvc.perform(post("/oauth/token")
                .with(httpBasic(myAppProperties.clientId, myAppProperties.clientSecret))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("grant_type", "password")
                .param("username", email)
                .param("password", password))
                .andDo(MockMvcResultHandlers.print())

        // Then
        result.andExpect(status().isOk)
                .andExpect(jsonPath("access_token").hasJsonPath())
    }
}