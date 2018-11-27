package com.jinhyuk.springrestapi.accounts

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository: JpaRepository<Account, Int> {
    fun findByEmail(email: String?): Account?
}