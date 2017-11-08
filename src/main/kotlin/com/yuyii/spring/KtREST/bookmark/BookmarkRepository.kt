package com.yuyii.spring.KtREST.bookmark

import org.springframework.data.jpa.repository.JpaRepository


interface BookmarkRepository : JpaRepository<Bookmark, Long> {
    fun findByAccountUsername(username: String): Collection<Bookmark>
}