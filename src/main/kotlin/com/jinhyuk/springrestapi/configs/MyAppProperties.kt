package com.jinhyuk.springrestapi.configs

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

// 현재 Kotlin에서 ConfigurationProperties를 사용하기 위해서는 lateinit var를 사용해야 한다.
@Configuration @ConfigurationProperties("my-app")
class MyAppProperties {
    lateinit var clientId: String
    lateinit var clientSecret: String
}