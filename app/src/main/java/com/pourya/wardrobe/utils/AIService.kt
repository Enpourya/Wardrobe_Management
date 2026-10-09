package com.pourya.wardrobe.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

object AIService {

    private const val PREFS_NAME = "wardrobe_prefs"
    private const val PREF_API_KEY = "openrouter_api_key"
    private const val OPENROUTER_BASE = "https://openrouter.ai/api/v1/chat/completions"
    private const val MODEL = "openai/gpt-4o-mini"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    fun saveApiKey(context: Context, key: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(PREF_API_KEY, key.trim()).apply()
    }

    fun getApiKey(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(PREF_API_KEY, "") ?: ""
    }

    fun isApiKeySet(context: Context): Boolean = getApiKey(context).isNotEmpty()

    suspend fun getOutfitSuggestions(
        context: Context,
        occasion: String,
        availableItems: List<String>,
        isFarsi: Boolean
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = getApiKey(context)
        if (apiKey.isEmpty()) return@withContext Result.failure(Exception("API key not set"))

        val itemsList = availableItems.joinToString("\n") { "- $it" }
        val prompt = if (isFarsi) {
            """تو یک متخصص مد و استایل هستی. 
من می‌خوام برای مراسم «$occasion» لباس بپوشم.
لیست لباس‌ها و وسایل موجود در کمد من:
$itemsList

لطفاً ۳ ست لباس کامل و متفاوت پیشنهاد بده. 
برای هر ست: نام ست، آیتم‌های مورد استفاده از لیست، و دلیل مناسب بودن برای این مراسم را بنویس.
پاسخ را فقط به فارسی بده."""
        } else {
            """You are a fashion and style expert.
I want to dress for the occasion: "$occasion".
Here are the clothes and items available in my wardrobe:
$itemsList

Please suggest 3 complete and different outfit combinations.
For each outfit: provide the outfit name, items used from the list, and why it's suitable for this occasion."""
        }

        try {
            val json = Gson().toJson(mapOf(
                "model" to MODEL,
                "messages" to listOf(mapOf("role" to "user", "content" to prompt)),
                "max_tokens" to 1200
            ))

            val request = Request.Builder()
                .url(OPENROUTER_BASE)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .addHeader("HTTP-Referer", "https://github.com/Enpourya/Wardrobe_Management")
                .addHeader("X-Title", "Wardrobe Manager")
                .post(json.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("API Error: ${response.code}"))
            }

            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val parsed = Gson().fromJson(body, JsonObject::class.java)
            val text = parsed
                .getAsJsonArray("choices")
                ?.get(0)?.asJsonObject
                ?.getAsJsonObject("message")
                ?.get("content")?.asString
                ?: return@withContext Result.failure(Exception("Parse error"))

            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
