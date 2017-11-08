package com.yuyii.spring.KtREST.bookmark

import com.yuyii.spring.KtREST.account.AccountRepository
import com.yuyii.spring.KtREST.error.UserNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder


@RestController
@RequestMapping("/{userId}/bookmarks")
internal class BookmarkRestController @Autowired
constructor(private val bookmarkRepository: BookmarkRepository,
            private val accountRepository: AccountRepository) {

    @RequestMapping(method = arrayOf(RequestMethod.GET))
    fun readBookmarks(@PathVariable userId: String): Collection<Bookmark> {
        this.validateUser(userId)
        return this.bookmarkRepository.findByAccountUsername(userId)
    }

    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun add(@PathVariable userId: String, @RequestBody input: Bookmark): ResponseEntity<*> {
        this.validateUser(userId)
        return this.accountRepository
                .findByUsername(userId)
                .map { account ->
                    val result = bookmarkRepository.save(Bookmark(account,
                            input.uri, input.description))

                    val location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(result.id!!).toUri()

                    ResponseEntity.created(location).build<Any>()
                }
                .orElse(ResponseEntity.noContent().build())
    }

    @RequestMapping(method = arrayOf(RequestMethod.GET), value = "/{bookmarkId}")
    fun readBookmark(@PathVariable userId: String, @PathVariable bookmarkId: Long?): Bookmark {
        this.validateUser(userId)
        return this.bookmarkRepository.getOne(bookmarkId!!)
    }

    private fun validateUser(userId: String) {
        this.accountRepository.findByUsername(userId).orElseThrow<RuntimeException> { UserNotFoundException(userId) }
    }
}