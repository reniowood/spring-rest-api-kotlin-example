package com.jinhyuk.springrestapi.accounts

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class AccountSerializer: JsonSerializer<Account>() {
    override fun serialize(account: Account, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeStartObject()
        jsonGenerator.writeNumberField("id", account.id ?: 0)
        jsonGenerator.writeEndObject()
    }
}