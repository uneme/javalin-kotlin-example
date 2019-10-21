package com.example.project.core.service

import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler
import java.time.OffsetDateTime
import net.aholbrook.paseto.meta.PasetoBuilders
import net.aholbrook.paseto.service.Token
import com.example.project.Config


enum class AccessRole(): Role {
    ANYONE,             // No authentication.
    MEMBER,             // all persons who can pass the authentication.
    ADMIN               // System administrator.
}

object LoginService {
    private val tokenKey = Config.TOKEN_PSK.toByteArray()
    private val tokenService = PasetoBuilders.V2.localService({ tokenKey }, Token::class.java).build()

    fun isLoggedIn(ctx: Context): Boolean =
        ctx.cookieStore<String>(Config.TOKEN_COOKIE_KEY).let { 
            try {
                this.tokenService.decode(it)
                true
            } catch (e: Exception) {
                false
            }
        } ?: false

    private fun isAuthorized(username: String, password: String): Boolean =
        permissionOf(username, password)?.let { true } ?: false

    private fun permissionOf(username: String): Set<AccessRole>? =
        userRoleMap.filter { it.key.first == username }.toList().firstOrNull()?.second
       

    private fun permissionOf(username: String, password: String): Set<AccessRole>? =
        userRoleMap.get(Pair(username, password))


    fun accessManager(handler: Handler, ctx: Context, permittedRoles: Set<Role>) {
        when {
            permittedRoles.contains(AccessRole.ANYONE) -> handler.handle(ctx)
            ctx.userRoles.any { it in permittedRoles } -> handler.handle(ctx)
            else -> ctx.status(401).json("Unauthoried")
        }
    }

    private val Context.userRoles: Set<AccessRole>
        get() {
            val token = this.cookieStore<String>(Config.TOKEN_COOKIE_KEY)
            return token?.let {
                val username = tokenService.decode(it).getIssuer()
                permissionOf(username) ?: setOf()
            } ?: setOf()
        }

    fun canLoggedIn(ctx: Context): Boolean {
        ctx.formParam("username")?.let { username ->
            ctx.formParam("password")?.let { password ->
                if (isAuthorized(username, password)) {
                    val token = Token()
                    token.setIssuer(Config.TOKEN_ISSUER)
                    token.setSubject(username)
                    token.setTokenId("uniqu id at TOKEN")
                    token.setIssuedAt(OffsetDateTime.now().toEpochSecond())
                    token.setExpiration(OffsetDateTime.now().plusHours(8).toEpochSecond())
                    val encoded = this.tokenService.encode(token)
                    ctx.cookieStore(Config.TOKEN_COOKIE_KEY, encoded)
                    ctx.formParam("redirectTo")?.let { ctx.redirect(it) }
                    return true
                }
            }
        }
        return false
    }

    fun isLoggedOut(ctx: Context): Boolean {
        ctx.clearCookieStore()
        return true
    }

    private val userRoleMap = hashMapOf(
        Pair("a", "aa") to setOf(AccessRole.MEMBER),
        Pair("b", "bb") to setOf(AccessRole.ADMIN)
    )
}
