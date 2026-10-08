package com.example.data.api

import com.example.data.model.CheckInRequest
import com.example.data.model.CheckInResponse
import com.example.data.model.ClientLookupResponse
import com.example.data.model.CreateClientRequest
import com.example.data.model.CreateClientResponse
import com.example.data.model.DeviceStatusResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface GuestFlowApi {

    @GET("api/device/status")
    suspend fun getDeviceStatus(
        @Header("X-GuestFlow-Device-Id") deviceId: String,
        @Query("branchId") branchId: String
    ): Response<DeviceStatusResponse>

    @POST("api/device/heartbeat")
    suspend fun sendHeartbeat(
        @Header("X-GuestFlow-Device-Id") deviceId: String
    ): Response<ResponseBody>

    @GET("api/spagym/branches")
    suspend fun getBranches(
        @Header("X-GuestFlow-Device-Id") deviceId: String
    ): Response<ResponseBody>

    @GET("api/spagym/clients/lookup")
    suspend fun lookupClient(
        @Header("X-GuestFlow-Device-Id") deviceId: String,
        @Query("phone") phone: String
    ): Response<ClientLookupResponse>

    @POST("api/spagym/clients")
    suspend fun createClient(
        @Header("X-GuestFlow-Device-Id") deviceId: String,
        @Body request: CreateClientRequest
    ): Response<CreateClientResponse>

    @POST("api/spagym/check-ins")
    suspend fun checkIn(
        @Header("X-GuestFlow-Device-Id") deviceId: String,
        @Body request: CheckInRequest
    ): Response<CheckInResponse>
}
