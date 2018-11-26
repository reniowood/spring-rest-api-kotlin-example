package com.jinhyuk.springrestapi

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("/docs 아래의 파일은 인증이 필요 없이 보여준다.")
    fun testAccessingDocs() {
        mockMvc.perform(get("/docs/index.html"))
                .andExpect(status().isNotFound)
    }

}