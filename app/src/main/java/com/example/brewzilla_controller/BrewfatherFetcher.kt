package com.example.brewzilla_controller

import com.example.brewzilla_controller.models.Batch
import com.example.brewzilla_controller.models.MashStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Base64

class BrewfatherFetcher {
    // TODO: Move these to a secure location
    // Manually add and remove these here for now
    val clientId = "_"
    val clientSecret = "_"

    private suspend fun getBatches(): List<Batch> {
        val result = fetchFromBrewfather(
            "GET",
            "https://api.brewfather.app/v2/",
            "batches",
            "complete=true&limit=100"
        )
        val json = Json { ignoreUnknownKeys = true }
        val batches: List<Batch> = json.decodeFromString(result)
        return batches
    }

    suspend fun getLatestAllGrainBatch(): Batch? {
        val batches = getBatches()
        // All grain recipes should have a strike temp, use this as a proxy for all grain
        return batches.sortedBy { -it._timestamp_ms }.firstOrNull { it.strikeTemp != null }
    }
    private suspend fun fetchFromBrewfather(apiMethod: String, apiEndpoint: String, apiUrl: String, params: String = ""): String {
        val url = "$apiEndpoint$apiUrl"
        val auth = Base64.getEncoder().encodeToString("$clientId:$clientSecret".toByteArray())


        val client = OkHttpClient()

        val request = when (apiMethod) {
            "GET" -> Request.Builder()
                .url("$url?$params")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Basic $auth")
                .build()
            "POST" -> {
                val mediaType = "application/json; charset=utf-8".toMediaType()
                val body = "".toRequestBody(mediaType) // Replace "" with your JSON payload
                Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", "Basic $auth")
                    .build()
            }
            else -> throw IllegalArgumentException("Unsupported method: $apiMethod")
        }

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            response.body?.string() ?: ""
        }
    }
}
