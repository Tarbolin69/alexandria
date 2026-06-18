package com.libreria.alexandria.data

import com.libreria.alexandria.data.local.LibroGuardadoEntity
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@JsonClass(generateAdapter = false)
data class DeepseekRequest(
    val model: String,
    val messages: List<DeepseekMessage>,
    val stream: Boolean = false
)

@JsonClass(generateAdapter = false)
data class DeepseekMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = false)
data class DeepseekResponse(
    val choices: List<DeepseekChoice> = emptyList()
)

@JsonClass(generateAdapter = false)
data class DeepseekChoice(
    val message: DeepseekMessage? = null
)

@Singleton
class DeepseekService @Inject constructor(
    private val apiKeyStorage: DeepseekApiKeyStorage,
    private val moshi: Moshi
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    companion object {
        private const val DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions"
    }

    suspend fun getRecommendations(libraryBooks: List<LibroGuardadoEntity>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val apiKey = apiKeyStorage.getApiKey()
                    ?: return@withContext Result.failure(Exception("API key not set"))

                val booksList = libraryBooks.joinToString("\n") { libro ->
                    "\"${libro.titulo}\" de ${libro.autor}"
                }

                val systemPrompt = "Eres un asistente experto en recomendaciones literarias. " +
                    "Basándote en los libros de la biblioteca del usuario, recomienda otros libros " +
                    "que podrían gustarle. Explica brevemente por qué cada recomendación encaja con sus gustos."

                val userPrompt = "Basándote en estos libros de mi biblioteca:\n\n$booksList\n\n" +
                    "Recomiéndame otros libros que podrían gustarme. " +
                    "Proporciona al menos 5 recomendaciones con el título, autor y una breve explicación."

                val requestBody = DeepseekRequest(
                    model = "deepseek-chat",
                    messages = listOf(
                        DeepseekMessage(role = "system", content = systemPrompt),
                        DeepseekMessage(role = "user", content = userPrompt)
                    )
                )

                val adapter = moshi.adapter(DeepseekRequest::class.java)
                val json = adapter.toJson(requestBody)

                val request = Request.Builder()
                    .url(DEEPSEEK_API_URL)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .post(json.toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    return@withContext Result.failure(
                        Exception("API error ${response.code}: $responseBody")
                    )
                }

                val responseAdapter = moshi.adapter(DeepseekResponse::class.java)
                val deepseekResponse = responseAdapter.fromJson(responseBody)
                val content = deepseekResponse?.choices?.firstOrNull()?.message?.content
                    ?: return@withContext Result.failure(Exception("Empty response from AI"))

                Result.success(content)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
