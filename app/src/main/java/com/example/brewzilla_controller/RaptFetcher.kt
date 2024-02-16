package com.example.brewzilla_controller
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Serializable
data class ResponseData(val access_token: String)

@Serializable
data class Brewzilla(
    val temperature: Double,
)

class RaptFetcher {

    val apiEndpoint = "https://api.rapt.io/api"

    suspend fun getBrewzillData() = withContext(Dispatchers.IO) {
        val result = queryApi("GET", "/Brewzillas/GetBrewzillas")
        val json = Json { ignoreUnknownKeys = true }
        val brewzillas: List<Brewzilla> = json.decodeFromString(result)
        val brewzilla = brewzillas.firstOrNull()
        return@withContext brewzilla
    }

    suspend fun queryApi(apiMethod: String, apiUrl: String, apiPayload: Map<String, String>? = null): String = withContext(
        Dispatchers.IO) {
        val url = "$apiEndpoint$apiUrl"
        val bearerToken = getActiveBearerToken()

        val client = OkHttpClient()

        val request = when (apiMethod) {
            "GET" -> {
                val params = apiPayload?.map { "${it.key}=${it.value}" }?.joinToString("&") ?: ""
                Request.Builder()
                    .url("$url?$params")
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer $bearerToken")
                    .build()
            }
            "POST" -> {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val jsonPayload = JsonObject(apiPayload?.mapValues { JsonPrimitive(it.value) } ?: emptyMap()).toString()
                val body = jsonPayload.toRequestBody(mediaType)
                Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Bearer $bearerToken")
                    .build()
            }
            else -> throw IllegalArgumentException("Unsupported method: $apiMethod")
        }

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            throw Exception("HTTP error! status: ${response.code}")
        }

        return@withContext response.body?.string() ?: ""
    }

    suspend fun getActiveBearerToken(): String {

        return suspendCoroutine { continuation ->
            val client = OkHttpClient()

            val payload = FormBody.Builder()
                .add("client_id", "rapt-user")
                .add("grant_type", "password")
                .add("username", username)
                .add("password", password)
                .build()

            val request = Request.Builder()
                .url("https://id.rapt.io/connect/token")
                .post(payload)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "application/json")
                .build()


            client.newCall(request).execute().use { response ->
                response.body?.string()?.let { jsonData ->
                    val json = Json { ignoreUnknownKeys = true }
                    val responseData = json.decodeFromString<ResponseData>(jsonData)
                    continuation.resume(responseData.access_token)
                }
            }

        }
    }
}