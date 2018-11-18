package com.jinhyuk.springrestapi

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.MediaTypes
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
internal class ItemControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("물품 생성시 201 Created 응답과 생성된 Item 정보가 온다")
    fun testCreateItem() {
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000
        )

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper().writeValueAsString(item)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isCreated)
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").value(item.name))
                .andExpect(jsonPath("description").value(item.description))
                .andExpect(jsonPath("price").value(item.price))
                .andExpect(jsonPath("saleStatus").value(SaleStatus.DRAFT.name))
                .andExpect(jsonPath("_links").hasJsonPath())
                .andExpect(jsonPath("_links.self").hasJsonPath())
    }

    @Test
    @DisplayName("잘못된 값으로 물품 생성시 400 Bad Request 응답")
    fun testCreateItemByBadRequest() {
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = -100
        )

        mockMvc.perform(post("/api/items")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper().writeValueAsString(item)))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.content[0].objectName").value("itemDto"))
                .andExpect(jsonPath("$.content[0].field").value("price"))
                .andExpect(jsonPath("$.content[0].rejectedValue").value("-100"))
                .andExpect(jsonPath("$.content[0].defaultMessage").hasJsonPath())
    }
}