package com.jinhyuk.springrestapi

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/items")
class ItemController(val itemRepository: ItemRepository) {

    @PostMapping
    fun createItem(@RequestBody itemDto: ItemDto): ResponseEntity<Item> {
        val item = itemDto.toItem()
        val createdItem = itemRepository.save(item)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdItem)
    }
}