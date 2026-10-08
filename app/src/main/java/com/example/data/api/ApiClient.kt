package com.example.data.api

import com.example.data.security.KioskPreferences
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient(private val preferences: KioskPreferences) {
    val cookieJar = SecureCookieJar(preferences)

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private var currentBaseUrl: String = preferences.getServerBaseUrl()
    private var cachedRetrofit: Retrofit? = null
    private var cachedApi: GuestFlowApi? = null

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(CacheControlInterceptor())
            .addInterceptor(DeviceIdHeaderInterceptor(preferences))

        // Logging interceptor: log headers only, redact sensitive auth, never log body
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
            redactHeader("Cookie")
            redactHeader("Set-Cookie")
            redactHeader("Authorization")
        }
        builder.addInterceptor(loggingInterceptor)

        builder.build()
    }

    @Synchronized
    fun getApi(): GuestFlowApi {
        val configuredUrl = preferences.getServerBaseUrl()
        if (cachedApi == null || configuredUrl != currentBaseUrl) {
            currentBaseUrl = configuredUrl
            val retrofit = Retrofit.Builder()
                .baseUrl(currentBaseUrl)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            cachedRetrofit = retrofit
            cachedApi = retrofit.create(GuestFlowApi::class.java)
        }
        return cachedApi!!
    }

    private class CacheControlInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val requestWithCache = original.newBuilder()
                .header("Cache-Control", "no-store, no-cache")
                .header("Pragma", "no-cache")
                .build()
            return chain.proceed(requestWithCache)
        }
    }

    private class DeviceIdHeaderInterceptor(
        private val prefs: KioskPreferences
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()
            val deviceId = prefs.getOrCreateInstallationId()
            val requestBuilder = original.newBuilder()
            if (original.header("X-GuestFlow-Device-Id") == null) {
                requestBuilder.header("X-GuestFlow-Device-Id", deviceId)
            }
            return chain.proceed(requestBuilder.build())
        }
    }
}
