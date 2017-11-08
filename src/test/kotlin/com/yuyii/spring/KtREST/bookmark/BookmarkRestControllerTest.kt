package com.yuyii.spring.KtREST.bookmark

import com.yuyii.spring.KtREST.KtRestApplication
import com.yuyii.spring.KtREST.account.Account
import com.yuyii.spring.KtREST.account.AccountRepository
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.mock.http.MockHttpOutputMessage
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext
import java.io.IOException
import java.nio.charset.Charset
import java.util.*


/**
 * @author Josh Long
 */
@RunWith(SpringRunner::class)
@SpringBootTest(classes = arrayOf(KtRestApplication::class))
@WebAppConfiguration
class BookmarkRestControllerTest {
    private val contentType = MediaType(MediaType.APPLICATION_JSON.type,
            MediaType.APPLICATION_JSON.subtype,
            Charset.forName("utf8"))

    private lateinit var mockMvc: MockMvc

    private val userName = "bdussault"

    private var mappingJackson2HttpMessageConverter: HttpMessageConverter<Any?>? = null

    private lateinit var account: Account

    private val bookmarkList = ArrayList<Bookmark>()

    @Autowired
    private lateinit var bookmarkRepository: BookmarkRepository

    @Autowired
    private lateinit var webApplicationContext: WebApplicationContext

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    fun setConverters(converters: Array<HttpMessageConverter<Any?>>) {

        this.mappingJackson2HttpMessageConverter = converters.toList().stream()
                .filter({ hmc -> hmc is MappingJackson2HttpMessageConverter })
                .findAny()
                .orElse(null)

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter)
    }

    @Before
    @Throws(Exception::class)
    fun setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build()

        this.bookmarkRepository.deleteAllInBatch()
        this.accountRepository.deleteAllInBatch()

        this.account = accountRepository.save(Account(userName, "password"))
        this.bookmarkList.add(bookmarkRepository.save(Bookmark(account, "http://bookmark.com/1/" + userName, "A description")))
        this.bookmarkList.add(bookmarkRepository.save(Bookmark(account, "http://bookmark.com/2/" + userName, "A description")))
    }

    @Test
    @Throws(Exception::class)
    fun userNotFound() {
        mockMvc.perform(post("/george/bookmarks/")
                .content(this.json(Bookmark()))
                .contentType(contentType))
                .andExpect(status().isNotFound)
    }

    @Test
    @Throws(Exception::class)
    fun readSingleBookmark() {
        mockMvc.perform(get("/" + userName + "/bookmarks/"
                + this.bookmarkList[0].id))
                .andExpect(status().isOk)
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", `is`(this.bookmarkList[0].id!!.toInt())))
                .andExpect(jsonPath("$.uri", `is`("http://bookmark.com/1/" + userName)))
                .andExpect(jsonPath("$.description", `is`("A description")))
    }

    @Test
    @Throws(Exception::class)
    fun readBookmarks() {
        mockMvc.perform(get("/$userName/bookmarks"))
                .andExpect(status().isOk)
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$", hasSize<Int>(2)))
                .andExpect(jsonPath("$[0].id", `is`(this.bookmarkList[0].id!!.toInt())))
                .andExpect(jsonPath("$[0].uri", `is`("http://bookmark.com/1/" + userName)))
                .andExpect(jsonPath("$[0].description", `is`("A description")))
                .andExpect(jsonPath("$[1].id", `is`(this.bookmarkList[1].id!!.toInt())))
                .andExpect(jsonPath("$[1].uri", `is`("http://bookmark.com/2/" + userName)))
                .andExpect(jsonPath("$[1].description", `is`("A description")))
    }

    @Test
    @Throws(Exception::class)
    fun createBookmark() {
        val bookmarkJson = json(Bookmark(
                this.account, "http://spring.io", "a bookmark to the best resource for Spring news and information"))

        this.mockMvc.perform(post("/$userName/bookmarks")
                .contentType(contentType)
                .content(bookmarkJson))
                .andExpect(status().isCreated)
    }

    @Throws(IOException::class)
    protected fun json(o: Any): String {
        val mockHttpOutputMessage = MockHttpOutputMessage()
        val converter = this.mappingJackson2HttpMessageConverter!!
        converter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage)
        return mockHttpOutputMessage.bodyAsString
    }
}