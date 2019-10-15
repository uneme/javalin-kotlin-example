package com.example.project

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import org.h2.tools.Server

fun main(args: Array<String>) {
    Server.createWebServer().start()
    val app = Javalin.create{ config ->
        config.accessManager { handler, ctx, permittedRoles ->
        }
    }.start(7000)

    app.routes {
        get("/") { ctx -> ctx.result("Hello World") }
    }
}
