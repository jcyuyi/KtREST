package com.yuyii.spring.KtREST.account

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface AccountRepository : JpaRepository<Account, Long> {
    fun findByUsername(username: String): Optional<Account>
}