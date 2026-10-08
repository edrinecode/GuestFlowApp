package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@JsonClass(generateAdapter = true)
data class DeviceStatusResponse(
    @Json(name = "status") val status: String, // "pending", "approved", "revoked"
    @Json(name = "branchId") val branchId: String? = null,
    @Json(name = "branchName") val branchName: String? = null,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class SpaGymClient(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "day") val day: String? = null,
    @Json(name = "month") val month: String? = null,
    @Json(name = "branch") val branch: String? = null
) {
    val firstName: String
        get() = name.trim().split(" ").firstOrNull()?.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
        } ?: "Guest"
}

@JsonClass(generateAdapter = true)
data class SpaVisit(
    @Json(name = "id") val id: String? = null,
    @Json(name = "clientId") val clientId: String? = null,
    @Json(name = "clientName") val clientName: String? = null,
    @Json(name = "phone") val phone: String? = null,
    @Json(name = "branch") val branch: String? = null,
    @Json(name = "checkInTime") val checkInTime: String? = null
)

@JsonClass(generateAdapter = true)
data class ClientLookupResponse(
    @Json(name = "client") val client: SpaGymClient? = null
)

@JsonClass(generateAdapter = true)
data class CreateClientRequest(
    @Json(name = "name") val name: String,
    @Json(name = "day") val day: String,
    @Json(name = "month") val month: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "branchId") val branchId: String
)

@JsonClass(generateAdapter = true)
data class CreateClientResponse(
    @Json(name = "client") val client: SpaGymClient? = null,
    @Json(name = "created") val created: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class CheckInRequest(
    @Json(name = "phone") val phone: String,
    @Json(name = "clientId") val clientId: String,
    @Json(name = "branchId") val branchId: String? = null
)

@JsonClass(generateAdapter = true)
data class CheckInResponse(
    @Json(name = "client") val client: SpaGymClient? = null,
    @Json(name = "visit") val visit: SpaVisit? = null,
    @Json(name = "alreadyCheckedIn") val alreadyCheckedIn: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class BranchItem(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String
)

/**
 * Birthday comparison helper evaluating dates under Kampala calendar context (Africa/Kampala).
 */
object BirthdayUtils {
    private val MONTH_NAMES = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    fun getMonthNames(): List<String> = MONTH_NAMES

    fun isTodayInKampala(clientDay: String?, clientMonth: String?): Boolean {
        if (clientDay.isNullOrBlank() || clientMonth.isNullOrBlank()) return false
        return try {
            val kampalaZone = ZoneId.of("Africa/Kampala")
            val today = LocalDate.now(kampalaZone)

            val parsedDay = clientDay.trim().toIntOrNull() ?: return false
            if (today.dayOfMonth != parsedDay) return false

            val monthStr = clientMonth.trim()
            val parsedMonthInt = monthStr.toIntOrNull()

            if (parsedMonthInt != null) {
                today.monthValue == parsedMonthInt
            } else {
                val todayMonthName = today.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                monthStr.equals(todayMonthName, ignoreCase = true)
            }
        } catch (_: Exception) {
            false
        }
    }
}
