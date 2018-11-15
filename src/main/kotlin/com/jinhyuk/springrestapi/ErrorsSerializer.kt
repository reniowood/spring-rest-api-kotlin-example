package com.jinhyuk.springrestapi

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import org.springframework.boot.jackson.JsonComponent
import org.springframework.validation.Errors

@JsonComponent
class ErrorsSerializer: JsonSerializer<Errors>() {
    override fun serialize(errors: Errors, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        jsonGenerator.writeStartArray()

        errors.fieldErrors.forEach { fieldError ->
            jsonGenerator.writeStartObject()

            jsonGenerator.writeStringField("objectName", fieldError.objectName)
            jsonGenerator.writeStringField("field", fieldError.field)
            jsonGenerator.writeStringField("rejectedValue", fieldError.rejectedValue?.toString() ?: "")
            jsonGenerator.writeStringField("defaultMessage", fieldError.defaultMessage)

            jsonGenerator.writeEndObject()
        }

        jsonGenerator.writeEndArray()
    }
}