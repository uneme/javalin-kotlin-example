package com.example.project

import com.example.project.core.controller.LoginController
import com.example.project.core.controller.UserController
import com.example.project.core.service.AccessRole
import com.example.project.core.service.LoginService
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.core.security.SecurityUtil.roles
import org.h2.tools.Server


fun main(args: Array<String>) {
    Server.createWebServer().start()
    val app = Javalin.create{ config -> 
        config.accessManager { handler, ctx, permittedRoles ->
	        LoginService.accessManager(handler, ctx, permittedRoles)
        }
        config.addStaticFiles("/public")
    }.start(7000)

    app.get("/index", UserController::index, roles(AccessRole.ANYONE))
    app.get("/login", LoginController::login, roles(AccessRole.ANYONE))
    app.post("/login", LoginController::loginTo, roles(AccessRole.ANYONE))
    app.post("/logout", LoginController::logout, roles(AccessRole.ANYONE))
}
