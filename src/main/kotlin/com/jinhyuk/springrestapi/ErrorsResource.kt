package com.jinhyuk.springrestapi

import org.springframework.hateoas.Resource
import org.springframework.validation.Errors

data class ErrorsResource(val errors: Errors): Resource<Errors>(errors)
