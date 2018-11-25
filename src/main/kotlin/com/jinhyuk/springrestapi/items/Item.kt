package com.jinhyuk.springrestapi.items

import com.jinhyuk.springrestapi.accounts.Account
import org.springframework.hateoas.Resource
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import javax.persistence.*
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty

@Entity
data class Item(
        @Id @GeneratedValue val id: Int? = null,
        val name: String,
        val description: String,
        val price: Int,
        @Enumerated(EnumType.STRING) val saleStatus: SaleStatus = SaleStatus.DRAFT,
        @ManyToOne val owner: Account? = null
)

data class ItemDto(
        @field:NotEmpty val name: String,
        @field:NotEmpty val description: String,
        @field:Min(0) val price: Int
) {
    fun toItem() = Item(
            name = name,
            description = description,
            price = price
    )
}

enum class SaleStatus {
    DRAFT, FOR_SALE, RESERVED, SOLD
}

class ItemResource(item: Item) : Resource<Item>(item) {
    init {
        add(linkTo(ItemController::class.java).slash(item.id).withSelfRel())
    }
}