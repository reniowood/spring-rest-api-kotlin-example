package com.jinhyuk.springrestapi

data class Item(
        val name: String,
        val description: String,
        val price: Int,
        val saleStatus: SaleStatus = SaleStatus.DRAFT
)

enum class SaleStatus {
    DRAFT, FOR_SALE, RESERVED, SOLD
}