package com.kim.chatbotapi

data class Quote (
    val q : String,
    val a : String,
    val h : String
)
data class Translate (
    val translated: String,
    val text: String,
    val translation: String
)