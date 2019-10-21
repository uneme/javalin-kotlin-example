package com.example.project.core.controller

import io.javalin.http.Context
import com.example.project.core.service.LoginService


object LoginController: BaseController {
    fun login(ctx: Context): Unit = if (LoginService.isLoggedIn(ctx)) {
        ctx.renderWith("/velocity/login/login.vm", mutableMapOf<String,Any?>(), null)
    } else {
        ctx.renderWith("/velocity/login/login.vm", mutableMapOf<String,Any?>(), null)
    }

    fun loginTo(ctx: Context): Unit = if (LoginService.canLoggedIn(ctx)) {
        ctx.redirect("/index") 
    } else {
        ctx.redirect("/login")
    }

    fun logout(ctx: Context): Unit = if (LoginService.isLoggedOut(ctx)) {
        ctx.redirect("/index") 
    } else {
        ctx.redirect("/login") 
    }
}
