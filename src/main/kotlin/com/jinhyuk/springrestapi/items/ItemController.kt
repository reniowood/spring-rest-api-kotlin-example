package com.jinhyuk.springrestapi.items

import com.jinhyuk.springrestapi.accounts.Account
import com.jinhyuk.springrestapi.common.ErrorsResource
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.MediaTypes
import org.springframework.hateoas.PagedResources
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
    fun getItems(@PageableDefault pageable: Pageable, assembler: PagedResourcesAssembler<Item>, @CurrentUser account: Account?): ResponseEntity<PagedResources<ItemResource>> {
        val items = itemRepository.findAll(pageable)
        val itemsResource = assembler
                .toResource(items) { ItemResource(it) }
                .also { if (account != null) it.add(linkTo(ItemController::class.java).withRel("create")) }

        return ResponseEntity.ok(itemsResource)
    }

    @GetMapping("/{id}")
    fun getItem(@PathVariable("id") id: Int, @CurrentUser account: Account?): ResponseEntity<ItemResource> {
        val item = itemRepository.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()
        val itemResource = ItemResource(item)

        if (account != null) {
            itemResource.add(linkTo(ItemController::class.java).slash(id).withRel("update"))
        }

        return ResponseEntity.ok(itemResource)
    }

    @PostMapping
    fun createItem(@Valid @RequestBody itemDto: ItemDto, errors: Errors, @CurrentUser account: Account): ResponseEntity<Any> {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorsResource(errors))
        }

        val item = itemDto.toItem(account)
        val createdItem = itemRepository.save(item)
        val itemResource = ItemResource(createdItem)

        return ResponseEntity.status(HttpStatus.CREATED).body(itemResource)
    }

    @PutMapping("/{id}")
    fun updateItem(@PathVariable("id") id: Int,
                   @Valid @RequestBody itemDto: ItemDto,
                   errors: Errors,
                   @CurrentUser account: Account): ResponseEntity<out Any> {
        if (errors.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorsResource(errors))
        }

        val item = itemRepository.findById(id).orElse(null) ?: return ResponseEntity.notFound().build()

        if (account != item.owner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val newItem = item.copy(name = itemDto.name, description = itemDto.description, price = itemDto.price)
        val updatedItem = itemRepository.save(newItem)
        val itemResource = ItemResource(updatedItem)

        return ResponseEntity.ok(itemResource)
    }
}