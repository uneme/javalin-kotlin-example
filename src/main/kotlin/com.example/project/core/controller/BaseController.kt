package com.example.project.core.controller
  
import java.io.File
import java.io.InputStream
import java.util.Locale
import java.util.Properties
import io.javalin.http.Context
import com.example.project.Config


interface BaseController {
    private fun loadResourceAsInputStream(filename: String, isInJarFile: Boolean): InputStream {
        return when (isInJarFile) {
            false -> File(filename).inputStream()
            else -> javaClass.classLoader.getResourceAsStream(filename.removePrefix(File.separator))
        }
    }

    fun Context.renderWith(filepath: String, model: MutableMap<String, Any?>, locale: Locale?) {
        //this.cookieStore<String>(Config.TOKEN_COOKIE_KEY)?.let { println(it); model.put("token", it) }

        val localeCode = locale?.let { it } ?: this.sessionAttribute<String?>("locale")?.let { Locale(it) } ?: Locale.ENGLISH
        val filename = String.format("localization/%s.properties", localeCode)

        val prop = Properties()
        loadResourceAsInputStream(filename, true).use { prop.load(it) }

        this.render(filepath, model + mapOf<String,Any>("prop" to prop))
    }
}
