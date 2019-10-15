package com.example.project.core

import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.JWT
import io.javalin.core.security.AccessManager
import io.javalin.core.security.Role
import io.javalin.http.Context
import io.javalin.http.Handler

object JWT {
    val ContextKey = "jwt"

    fun containedBy(ctx: Context): Boolean = ctx.attribute<DecodedJWT>(this.ContextKey) != null

    fun get(ctx: Context): DecodedJWT? = ctx.attribute(this.ContextKey) as? DecodedJWT

    fun set(ctx: Context, jwt: DecodedJWT): Boolean {
        return try {
            ctx.attribute(this.ContextKey, jwt)
            true
        } catch (e: Exception) {
            false
        }
    }
}

class JWTAccessManager(val userRoleClaim: String, val roles: Map<String, Role>, val dfltRole: Role) : AccessManager{
    private fun roleFrom(ctx: Context): Role {
       val jwt = JWT.get(ctx) ?: return this.dfltRole
       val roleString = jwt.getClaim(this.userRoleClaim).asString()

       return this.roles.getOrDefault(roleString, this.dfltRole)
    }

    override fun manage(handler: Handler, ctx: Context, permittedRoles: Set<Role>): Unit {
        val role = roleFrom(ctx)
        val isAuthorized = permittedRoles.contains(role)
        if (isAuthorized) { handler.handle(ctx) } else { ctx.status(401).result("Unauthorized") }
    }
}

class JWTAuthenticationService(val algorithm: Algorithm) {
    fun generateToken(): String {

    }
}
