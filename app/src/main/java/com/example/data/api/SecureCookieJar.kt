package com.example.data.api

import com.example.data.security.KioskPreferences
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

/**
 * Secure HTTP CookieJar for kiosk device authentication.
 * Automatically accepts and retains the server's Secure, HttpOnly, SameSite=Strict device credential.
 * Protects persisted cookies using Android Keystore-backed encrypted storage.
 * Does not expose or log HttpOnly cookie secrets.
 */
class SecureCookieJar(private val preferences: KioskPreferences) : CookieJar {
    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    init {
        loadPersistedCookies()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isEmpty()) return
        val host = url.host
        val currentCookies = cookieStore.getOrPut(host) { mutableListOf() }

        synchronized(currentCookies) {
            for (newCookie in cookies) {
                // Replace existing cookie with same name and path if present
                currentCookies.removeAll { it.name == newCookie.name && it.matches(url) }
                if (newCookie.expiresAt > System.currentTimeMillis()) {
                    currentCookies.add(newCookie)
                }
            }
        }
        persistCookies()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.host
        val cookies = cookieStore[host] ?: return emptyList()
        val now = System.currentTimeMillis()

        synchronized(cookies) {
            // Remove expired cookies
            cookies.removeAll { it.expiresAt < now }
            return cookies.filter { it.matches(url) }
        }
    }

    private fun persistCookies() {
        val serializedList = mutableListOf<String>()
        val now = System.currentTimeMillis()

        for ((host, cookies) in cookieStore) {
            synchronized(cookies) {
                for (cookie in cookies) {
                    if (cookie.expiresAt >= now) {
                        // Serialize in safe delimiter format: host|name|value|expiresAt|path|secure|httpOnly
                        val serialized = listOf(
                            host,
                            cookie.name,
                            cookie.value,
                            cookie.expiresAt.toString(),
                            cookie.path,
                            cookie.secure.toString(),
                            cookie.httpOnly.toString()
                        ).joinToString(";;")
                        serializedList.add(serialized)
                    }
                }
            }
        }
        preferences.saveEncryptedCookies(serializedList.joinToString("###"))
    }

    private fun loadPersistedCookies() {
        val raw = preferences.loadEncryptedCookies()
        if (raw.isBlank()) return

        val entries = raw.split("###")
        val now = System.currentTimeMillis()

        for (entry in entries) {
            val parts = entry.split(";;")
            if (parts.size >= 7) {
                try {
                    val host = parts[0]
                    val name = parts[1]
                    val value = parts[2]
                    val expiresAt = parts[3].toLong()
                    val path = parts[4]
                    val secure = parts[5].toBoolean()
                    val httpOnly = parts[6].toBoolean()

                    if (expiresAt > now) {
                        val cookieBuilder = Cookie.Builder()
                            .name(name)
                            .value(value)
                            .domain(host)
                            .path(path)
                            .expiresAt(expiresAt)

                        if (secure) cookieBuilder.secure()
                        if (httpOnly) cookieBuilder.httpOnly()

                        val cookie = cookieBuilder.build()
                        cookieStore.getOrPut(host) { mutableListOf() }.add(cookie)
                    }
                } catch (_: Exception) {
                    // Ignore corrupted individual entry
                }
            }
        }
    }

    /**
     * Clears all session cookies immediately upon device revocation or reset.
     */
    fun clearAllCookies() {
        cookieStore.clear()
        preferences.clearDeviceCookies()
    }
}
