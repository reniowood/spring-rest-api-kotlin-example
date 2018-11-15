package com.jinhyuk.springrestapi

import javax.persistence.*

@Entity
data class Item(
        @Id @GeneratedValue val id: Int? = null,
        val name: String,
        val description: String,
        val price: Int,
        @Enumerated(EnumType.STRING) val saleStatus: SaleStatus = SaleStatus.DRAFT
)

data class ItemDto(
        val name: String,
        val description: String,
        val price: Int
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