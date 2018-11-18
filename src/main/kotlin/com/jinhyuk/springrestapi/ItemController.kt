package com.jinhyuk.springrestapi

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.Resource
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping(path = ["/api/items"], produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class ItemController(val itemRepository: ItemRepository) {

    @GetMapping
    fun getItems(pageable: Pageable): ResponseEntity<Page<Item>> {
        return ResponseEntity.ok(itemRepository.findAll(pageable))
    }

    @GetMapping("/{id}")
    fun getItem(@PathVariable("id") id: Int): ResponseEntity<Resource<Item>> {
        return itemRepository.findById(id).map { item ->
            val itemResource = Resource(item)
            itemResource.add(linkTo(ItemController::class.java).slash(item.id).withSelfRel())

            ResponseEntity.ok(itemResource)
        }.orElseGet { ResponseEntity.notFound().build() }
    }

    @PostMapping
    fun createItem(@Valid @RequestBody itemDto: ItemDto, errors: Errors): ResponseEntity<Any> {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorsResource(errors))
        }

        val item = itemDto.toItem()
        val createdItem = itemRepository.save(item)
        val itemResource = Resource(createdItem)
        itemResource.add(linkTo(ItemController::class.java).slash(createdItem.id).withSelfRel())

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
            val itemResource = Resource(updatedItem)
            itemResource.add(linkTo(ItemController::class.java).slash(updatedItem.id).withSelfRel())

            ResponseEntity.ok(itemResource)
        }.orElseGet {
            ResponseEntity.notFound().build()
        }
    }
}