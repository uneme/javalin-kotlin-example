package com.example.project.core.controller

import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler

object UserController {
    fun index(ctx: Context) {
        ctx.result("Hello world")
    }
}
