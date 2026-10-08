package com.example.data.security

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import java.security.SecureRandom
import java.util.UUID

/**
 * Encrypted preferences manager for the GuestFlow Kiosk.
 * Sensitive device credentials and installation identifiers are protected by Keystore encryption.
 */
class KioskPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("guestflow_kiosk_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ENCRYPTED_DEVICE_ID = "enc_device_id"
        private const val KEY_SERVER_BASE_URL = "server_base_url"
        private const val KEY_BRANCH_ID = "branch_id"
        private const val KEY_BRANCH_NAME = "branch_name"
        private const val KEY_ENCRYPTED_COOKIES = "enc_cookies"
    }

    /**
     * Retrieves or generates a cryptographically secure installation ID (at least 20 chars).
     * Protected in Keystore-backed encrypted storage.
     */
    fun getOrCreateInstallationId(): String {
        val encrypted = prefs.getString(KEY_ENCRYPTED_DEVICE_ID, null)
        if (!encrypted.isNullOrEmpty()) {
            val decrypted = KeystoreHelper.decrypt(encrypted)
            if (decrypted.isNotEmpty()) {
                return decrypted
            }
        }

        // Generate cryptographically random installation ID (32+ chars)
        val secureRandom = SecureRandom()
        val randomBytes = ByteArray(12)
        secureRandom.nextBytes(randomBytes)
        val hexSuffix = randomBytes.joinToString("") { "%02x".format(it) }
        val newId = "gf_kiosk_${UUID.randomUUID().toString().replace("-", "")}_$hexSuffix"

        val encNew = KeystoreHelper.encrypt(newId)
        prefs.edit().putString(KEY_ENCRYPTED_DEVICE_ID, encNew).apply()
        return newId
    }

    fun getServerBaseUrl(): String {
        val stored = prefs.getString(KEY_SERVER_BASE_URL, null)
        if (!stored.isNullOrBlank()) {
            return normalizeUrl(stored)
        }
        return normalizeUrl(BuildConfig.DEFAULT_BASE_URL)
    }

    fun setServerBaseUrl(url: String) {
        val normalized = normalizeUrl(url)
        prefs.edit().putString(KEY_SERVER_BASE_URL, normalized).apply()
    }

    private fun normalizeUrl(url: String): String {
        val trimmed = url.trim()
        return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
    }

    fun getBranchId(): String {
        return prefs.getString(KEY_BRANCH_ID, BuildConfig.DEFAULT_BRANCH_ID)
            ?: BuildConfig.DEFAULT_BRANCH_ID
    }

    fun setBranchId(branchId: String) {
        prefs.edit().putString(KEY_BRANCH_ID, branchId.trim()).apply()
    }

    fun getBranchName(): String {
        return prefs.getString(KEY_BRANCH_NAME, "SpaGym Reception") ?: "SpaGym Reception"
    }

    fun setBranchName(name: String) {
        prefs.edit().putString(KEY_BRANCH_NAME, name.trim()).apply()
    }

    /**
     * Stores serialized cookies in encrypted storage.
     */
    fun saveEncryptedCookies(cookieData: String) {
        val encrypted = KeystoreHelper.encrypt(cookieData)
        prefs.edit().putString(KEY_ENCRYPTED_COOKIES, encrypted).apply()
    }

    /**
     * Loads serialized cookies from encrypted storage.
     */
    fun loadEncryptedCookies(): String {
        val encrypted = prefs.getString(KEY_ENCRYPTED_COOKIES, null) ?: return ""
        return KeystoreHelper.decrypt(encrypted)
    }

    /**
     * Clears device cookies/session upon revocation or reset.
     */
    fun clearDeviceCookies() {
        prefs.edit().remove(KEY_ENCRYPTED_COOKIES).apply()
    }
}
