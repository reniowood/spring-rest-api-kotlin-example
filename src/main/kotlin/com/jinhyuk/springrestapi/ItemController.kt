package com.jinhyuk.springrestapi

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.PagedResources
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(path = ["/api/items"], produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class ItemController(val itemRepository: ItemRepository) {

    @GetMapping
    fun getItems(@PageableDefault pageable: Pageable, assembler: PagedResourcesAssembler<Item>): ResponseEntity<PagedResources<ItemResource>> {
        val items = itemRepository.findAll(pageable)
        val itemsResource = assembler.toResource(items) { ItemResource(it) }

        return ResponseEntity.ok(itemsResource)
    }

    @GetMapping("/{id}")
    fun getItem(@PathVariable("id") id: Int): ResponseEntity<ItemResource> {
        return itemRepository.findById(id)
                .map { ResponseEntity.ok(ItemResource(it)) }
                .orElseGet { ResponseEntity.notFound().build() }
    }

    @PostMapping
    fun createItem(@Valid @RequestBody itemDto: ItemDto, errors: Errors): ResponseEntity<Any> {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorsResource(errors))
        }

        val item = itemDto.toItem()
        val createdItem = itemRepository.save(item)
        val itemResource = ItemResource(createdItem)

        return ResponseEntity.status(HttpStatus.CREATED).body(itemResource)
    }

    @PutMapping("/{id}")
    fun updateItem(@PathVariable("id") id: Int, @Valid @RequestBody itemDto: ItemDto, errors: Errors): ResponseEntity<out Any> {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorsResource(errors))
        }

        return itemRepository.findById(id).map { item ->
            val newItem = item.copy(name = itemDto.name, description = itemDto.description, price = itemDto.price)
            val updatedItem = itemRepository.save(newItem)
            val itemResource = ItemResource(updatedItem)

            ResponseEntity.ok(itemResource)
        }.orElseGet {
            ResponseEntity.notFound().build()
        }
    }
}