package com.example.project.core.service

import com.example.project.Config
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import io.javalin.http.Context
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*


class LoginServiceTest {
    @BeforeEach
    fun setup() {
    }

    @Test
    fun isLoggedIn() {
	val request = mock(HttpServletRequest::class.java)
	val response = mock(HttpServletResponse::class.java)
	val ctx = Context(request, response)

	ctx.cookieStore(Config.TOKEN_COOKIE_KEY, "test")

        assertEquals(LoginService.isLoggedIn(ctx), false)
    }
}
