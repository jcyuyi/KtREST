package com.yuyii.spring.KtREST

import com.yuyii.spring.KtREST.account.Account
import com.yuyii.spring.KtREST.account.AccountRepository
import com.yuyii.spring.KtREST.bookmark.Bookmark
import com.yuyii.spring.KtREST.bookmark.BookmarkRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
class KtRestApplication {
    @Bean
    fun init(accountRepository: AccountRepository,
             bookmarkRepository: BookmarkRepository): CommandLineRunner {
        return CommandLineRunner { _ ->
            "jhoeller,dsyer,pwebb,ogierke,rwinch,mfisher,mpollack,jlong".split(",").forEach { a ->
                val account = accountRepository.save(Account(username = a,
                        password = "password"))
                bookmarkRepository.save(Bookmark(account,
                        "http://bookmark.com/1/" + a, "A description"))
                bookmarkRepository.save(Bookmark(account,
                        "http://bookmark.com/2/" + a, "A description"))
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<KtRestApplication>(*args)
}
