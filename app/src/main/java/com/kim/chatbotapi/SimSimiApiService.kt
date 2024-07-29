package com.kim.chatbotapi

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URLEncoder

class SimSimiApiService {
    companion object {
        private const val API_URL = "http://sandbox.api.simsimi.com/request.p"
        private const val API_KEY = "d6bbfd1b-7cb3-4cfe-87b1-261e4d210d19"
    }

    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun getResponse(text: String, lc: String): String {
        val encodedText = URLEncoder.encode(text, "UTF-8")
        val url = "$API_URL?key=$API_KEY&lc=$lc&ft=0.0&text=$encodedText"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string() ?: ""
        }
    }
}
