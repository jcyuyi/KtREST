package com.yuyii.spring.KtREST.account

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yuyii.spring.KtREST.bookmark.Bookmark
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.OneToMany


@Entity
data class Account(
        var username: String,
        @JsonIgnore
        var password: String
) {
    @Id
    @GeneratedValue
    var id: Long? = null

    @OneToMany(mappedBy = "account")
    var bookmarks: Set<Bookmark> = HashSet()

    internal constructor() : this("", "") { // jpa only
    }
}