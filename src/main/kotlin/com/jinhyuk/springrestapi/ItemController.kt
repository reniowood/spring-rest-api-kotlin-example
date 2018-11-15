package com.jinhyuk.springrestapi

import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.Resource
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.Errors
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping(path = ["/api/items"], produces = [MediaTypes.HAL_JSON_UTF8_VALUE])
class ItemController(val itemRepository: ItemRepository) {

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
}