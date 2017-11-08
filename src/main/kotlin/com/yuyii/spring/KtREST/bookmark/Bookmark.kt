package com.yuyii.spring.KtREST.bookmark

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yuyii.spring.KtREST.account.Account
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne


@Entity
data class Bookmark(
        @ManyToOne
        @JsonIgnore
        var account: Account? = null,
        var uri: String,
        var description: String
) {
    @Id
    @GeneratedValue
    var id: Long? = null

    internal constructor() : this(null, "", "") { // jpa only
    }
}