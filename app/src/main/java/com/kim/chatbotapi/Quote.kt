package com.kim.chatbotapi

data class Quote (
    val q : String,
    val a : String,
    val h : String
)
data class Contents (
    val translated: String,
    val text: String,
    val translation: String
)
data class Response(
    val contents: Contents
)