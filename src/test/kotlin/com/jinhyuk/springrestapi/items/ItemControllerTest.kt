package com.jinhyuk.springrestapi.items

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.jinhyuk.springrestapi.accounts.Account
import com.jinhyuk.springrestapi.accounts.AccountRole
import com.jinhyuk.springrestapi.accounts.AccountService
import com.jinhyuk.springrestapi.configs.MyAppProperties
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.hateoas.Link
import org.springframework.http.MediaType
import org.springframework.restdocs.hypermedia.HypermediaDocumentation.*
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.stream.IntStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ActiveProfiles("test")
internal class ItemControllerTest {
    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var itemRepository: ItemRepository
    @Autowired lateinit var accountService: AccountService
    @Autowired lateinit var myAppProperties: MyAppProperties

    private val email = "test@email.com"
    private val password = "test"
    private var account = Account(email = email, password = password, roles = setOf(AccountRole.ADMIN))

    @BeforeAll
    fun setUp() {
        account = accountService.createAccount(account)
    }

    @Test
    @DisplayName("물품 생성시 201 Created 응답과 생성된 Item 정보가 온다")
    fun testCreateItem() {
        val accessToken = getAccessToken()
        val item = ItemDto(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000
        )

        mockMvc.perform(post("/api/items")
                .header("Authorization", "Bearer $accessToken")
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
                .andDo(document("create-event", links(
                    halLinks(),
                    linkWithRel(Link.REL_SELF).description("link to self")
                ), requestFields(
                    fieldWithPath("name").description("name of item"),
                    fieldWithPath("description").description("description of item"),
                    fieldWithPath("price").description("price of item")
                ), responseFields(
                    fieldWithPath("id").description("id of item"),
                    fieldWithPath("name").description("name of item"),
                    fieldWithPath("description").description("description of item"),
                    fieldWithPath("price").description("price of item"),
                    fieldWithPath("saleStatus").description("sale status of item"),
                    fieldWithPath("owner").description("owner of item"),
                    fieldWithPath("owner.id").description("id of item owner"),
                    subsectionWithPath("_links").description("links to other resources")
                )))
    }

    @Test
    @DisplayName("잘못된 값으로 물품 생성시 400 Bad Request 응답")
    fun testCreateItemByBadRequest() {
        val accessToken = getAccessToken()
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = -100
        )

        mockMvc.perform(post("/api/items")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper().writeValueAsString(item)))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.content[0].objectName").value("itemDto"))
                .andExpect(jsonPath("$.content[0].field").value("price"))
                .andExpect(jsonPath("$.content[0].rejectedValue").value("-100"))
                .andExpect(jsonPath("$.content[0].defaultMessage").hasJsonPath())
    }

    @Test
    @DisplayName("물품 수정시 200 OK 응답")
    fun testModifyItem() {
        val accessToken = getAccessToken()
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000,
                owner = account
        )

        val savedItem = itemRepository.save(item)

        val updatedItem = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다. 50만원으로 가격 인하합니다.",
                price = 500000
        )

        mockMvc.perform(put("/api/items/${savedItem.id}")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper().writeValueAsString(updatedItem)))
                .andExpect(status().isOk)
                .andExpect(jsonPath("id").value(savedItem.id!!))
                .andExpect(jsonPath("name").value(savedItem.name))
                .andExpect(jsonPath("description").value(updatedItem.description))
                .andExpect(jsonPath("saleStatus").value(updatedItem.saleStatus.name))
                .andExpect(jsonPath("price").value(updatedItem.price))
                .andExpect(jsonPath("_links").hasJsonPath())
                .andExpect(jsonPath("_links.self").hasJsonPath())
                .andDo(document("modify-event", links(
                    halLinks(),
                    linkWithRel(Link.REL_SELF).description("link to self")
                ), relaxedRequestFields(
                    fieldWithPath("name").description("name of item"),
                    fieldWithPath("description").description("description of item"),
                    fieldWithPath("price").description("price of item"),
                    fieldWithPath("saleStatus").description("sale status of item"),
                    fieldWithPath("owner").description("owner of item")
                ), responseFields(
                    fieldWithPath("id").description("id of item"),
                    fieldWithPath("name").description("name of item"),
                    fieldWithPath("description").description("description of item"),
                    fieldWithPath("price").description("price of item"),
                    fieldWithPath("saleStatus").description("sale status of item"),
                    fieldWithPath("owner").description("owner of item"),
                    fieldWithPath("owner.id").description("id of item owner"),
                    subsectionWithPath("_links").description("links to other resources")
                )))
    }

    @Test
    @DisplayName("없는 물품 수정시 404 Not Found 응답")
    fun testModifyItemWithWrongId() {
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000,
                owner = account
        )

        val savedItem = itemRepository.save(item)

        val updatedItem = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다. 50만원으로 가격 인하합니다.",
                price = 500000
        )

        val accessToken = getAccessToken()

        mockMvc.perform(put("/api/items/${savedItem.id?.plus(1)}")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper().writeValueAsString(updatedItem)))
                .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("잘못된 값으로 수정 시 400 Bad Request 응답")
    fun testModifyItemWithWrongValue() {
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000,
                owner = account
        )

        val savedItem = itemRepository.save(item)

        val updatedItem = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다. 50만원으로 가격 인하합니다.",
                price = -100000
        )

        val accessToken = getAccessToken()

        mockMvc.perform(put("/api/items/${savedItem.id?.plus(1)}")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper().writeValueAsString(updatedItem)))
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.content[0].objectName").value("itemDto"))
                .andExpect(jsonPath("$.content[0].field").value("price"))
                .andExpect(jsonPath("$.content[0].rejectedValue").value("-100000"))
                .andExpect(jsonPath("$.content[0].defaultMessage").hasJsonPath())
    }

    @Test
    @DisplayName("작성자가 아닌 사용자가 수정 시 403 Forbidden 응답")
    fun testModifyItemWithWrongAccount() {
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000,
                owner = account
        )

        val savedItem = itemRepository.save(item)

        val updatedItem = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다. 50만원으로 가격 인하합니다.",
                price = 100000
        )

        val newAccount = Account(
                email = "test02@email.com",
                password = "test02",
                roles = setOf(AccountRole.USER)
        )
        accountService.createAccount(newAccount)

        val accessToken = getAccessToken(email = newAccount.email, password = newAccount.password)

        mockMvc.perform(put("/api/items/${savedItem.id}")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jacksonObjectMapper().writeValueAsString(updatedItem)))
                .andExpect(status().isForbidden)
    }

    @Test
    @DisplayName("물품 조회시 200 OK 응답")
    fun testGetItem() {
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000,
                saleStatus = SaleStatus.FOR_SALE
        )

        val savedItem = itemRepository.save(item)

        mockMvc.perform(get("/api/items/${savedItem.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("id").value(savedItem.id!!))
                .andExpect(jsonPath("name").value(savedItem.name))
                .andExpect(jsonPath("description").value(savedItem.description))
                .andExpect(jsonPath("price").value(savedItem.price))
                .andExpect(jsonPath("saleStatus").value(savedItem.saleStatus.name))
                .andExpect(jsonPath("_links").hasJsonPath())
                .andExpect(jsonPath("_links.self").hasJsonPath())
                .andDo(document("get-event", links(
                    halLinks(),
                    linkWithRel(Link.REL_SELF).description("link to self")
                ), responseFields(
                    fieldWithPath("id").description("id of item"),
                    fieldWithPath("name").description("name of item"),
                    fieldWithPath("description").description("description of item"),
                    fieldWithPath("price").description("price of item"),
                    fieldWithPath("saleStatus").description("sale status of item"),
                    fieldWithPath("owner").description("owner of item"),
                    subsectionWithPath("_links").description("links to other resources")
                )))
    }

    @Test
    @DisplayName("인증 후 물품 조회시 응답에 수정 링크 추가")
    fun testGetItemWithAccessToken() {
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000,
                saleStatus = SaleStatus.FOR_SALE
        )

        val savedItem = itemRepository.save(item)

        val accessToken = getAccessToken()

        mockMvc.perform(get("/api/items/${savedItem.id}").header("Authorization", "Bearer $accessToken"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("id").value(savedItem.id!!))
                .andExpect(jsonPath("name").value(savedItem.name))
                .andExpect(jsonPath("description").value(savedItem.description))
                .andExpect(jsonPath("price").value(savedItem.price))
                .andExpect(jsonPath("saleStatus").value(savedItem.saleStatus.name))
                .andExpect(jsonPath("_links").hasJsonPath())
                .andExpect(jsonPath("_links.self").hasJsonPath())
                .andDo(document("get-event", links(
                        halLinks(),
                        linkWithRel(Link.REL_SELF).description("link to self"),
                        linkWithRel("update").description("link to update the item")
                ), responseFields(
                        fieldWithPath("id").description("id of item"),
                        fieldWithPath("name").description("name of item"),
                        fieldWithPath("description").description("description of item"),
                        fieldWithPath("price").description("price of item"),
                        fieldWithPath("saleStatus").description("sale status of item"),
                        fieldWithPath("owner").description("owner of item"),
                        subsectionWithPath("_links").description("links to other resources")
                )))
    }

    @Test
    @DisplayName("없는 물품 조회시 404 Not Found 응답")
    fun testGetItemWithWrongId() {
        val item = Item(
                name = "맥북 프로 2015 13인치",
                description = "작년에 산 맥북 프로 2015 13인치 기본형입니다.",
                price = 800000,
                saleStatus = SaleStatus.FOR_SALE
        )

        val savedItem = itemRepository.save(item)

        mockMvc.perform(get("/api/items/${savedItem.id?.plus(1)}"))
                .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("물품 목록 조회시 200 OK 응답")
    fun testGetItems() {
        IntStream.rangeClosed(1, 40).forEach {
            val item = Item(
                    name = "iPhone ${it}",
                    description = "iPhone ${it} 16GB입니다.",
                    price = it * 100000
            )

            itemRepository.save(item)
        }

        mockMvc.perform(get("/api/items?page=1"))
                .andExpect(status().isOk)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("_embedded.itemList[0]._links").hasJsonPath())
                .andExpect(jsonPath("_links").hasJsonPath())
                .andExpect(jsonPath("_links.self").hasJsonPath())
                .andExpect(jsonPath("_links.prev").hasJsonPath())
                .andExpect(jsonPath("_links.next").hasJsonPath())
                .andDo(document("get-events", links(
                        halLinks(),
                        linkWithRel(Link.REL_SELF).description("link to self"),
                        linkWithRel(Link.REL_PREVIOUS).description("link to previous page of items"),
                        linkWithRel(Link.REL_NEXT).description("link to next page of items"),
                        linkWithRel(Link.REL_FIRST).description("link to first page of items"),
                        linkWithRel(Link.REL_LAST).description("link to last page of items")
                ), responseFields(
                        fieldWithPath("_embedded.itemList[].id").description("id of item"),
                        fieldWithPath("_embedded.itemList[].name").description("name of item"),
                        fieldWithPath("_embedded.itemList[].description").description("description of item"),
                        fieldWithPath("_embedded.itemList[].price").description("price of item"),
                        fieldWithPath("_embedded.itemList[].saleStatus").description("sale status of item"),
                        fieldWithPath("_embedded.itemList[].owner").description("owner of item"),
                        subsectionWithPath("_embedded.itemList[]._links").description("links to other resources"),
                        subsectionWithPath("page").description("current page data"),
                        subsectionWithPath("_links").description("links to other resources")
                )))
    }

    @Test
    @DisplayName("인증 후 물품 목록 조회시 응답에 물품 생성 링크 추가")
    fun testGetItemsWithAccessToken() {
        IntStream.rangeClosed(1, 40).forEach {
            val item = Item(
                    name = "iPhone ${it}",
                    description = "iPhone ${it} 16GB입니다.",
                    price = it * 100000
            )

            itemRepository.save(item)
        }

        val accessToken = getAccessToken()

        mockMvc.perform(get("/api/items?page=1").header("Authorization", "Bearer $accessToken"))
                .andExpect(status().isOk)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("_embedded.itemList[0]._links").hasJsonPath())
                .andExpect(jsonPath("_links").hasJsonPath())
                .andExpect(jsonPath("_links.self").hasJsonPath())
                .andExpect(jsonPath("_links.prev").hasJsonPath())
                .andExpect(jsonPath("_links.next").hasJsonPath())
                .andExpect(jsonPath("_links.create").hasJsonPath())
                .andDo(document("get-events", links(
                        halLinks(),
                        linkWithRel(Link.REL_SELF).description("link to self"),
                        linkWithRel(Link.REL_PREVIOUS).description("link to previous page of items"),
                        linkWithRel(Link.REL_NEXT).description("link to next page of items"),
                        linkWithRel(Link.REL_FIRST).description("link to first page of items"),
                        linkWithRel(Link.REL_LAST).description("link to last page of items"),
                        linkWithRel("create").description("link to create an item")
                ), responseFields(
                        fieldWithPath("_embedded.itemList[].id").description("id of item"),
                        fieldWithPath("_embedded.itemList[].name").description("name of item"),
                        fieldWithPath("_embedded.itemList[].description").description("description of item"),
                        fieldWithPath("_embedded.itemList[].price").description("price of item"),
                        fieldWithPath("_embedded.itemList[].saleStatus").description("sale status of item"),
                        fieldWithPath("_embedded.itemList[].owner").description("owner of item"),
                        subsectionWithPath("_embedded.itemList[]._links").description("links to other resources"),
                        subsectionWithPath("page").description("current page data"),
                        subsectionWithPath("_links").description("links to other resources")
                )))
    }

    private fun getAccessToken(email: String = this.email, password: String = this.password): String {
        val result = mockMvc.perform(post("/oauth/token")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic(myAppProperties.clientId, myAppProperties.clientSecret))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("grant_type", "password")
                .param("username", email)
                .param("password", password))

        val response = result.andReturn().response.contentAsString

        return jacksonObjectMapper().readValue<Map<String, String>>(response)["access_token"].orEmpty()
    }
}