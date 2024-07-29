package com.kim.chatbotapi

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var simSimiApiService: SimSimiApiService



    private val languageMap = mapOf(
        "English" to "en",
        "Spanish" to "es",
        "French" to "fr",
        "German" to "de",
        "Chinese" to "zh"
    )

    private lateinit var textEditText: EditText
    private lateinit var languageSpinner: Spinner
    private lateinit var requestButton: Button
    private lateinit var messageTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textEditText = findViewById(R.id.text_editText)
        languageSpinner = findViewById(R.id.language_spinner)
        requestButton = findViewById(R.id.request_button)
        messageTextView = findViewById(R.id.message)

        simSimiApiService = SimSimiApiService()

        // Setup Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.languages,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter

        requestButton.setOnClickListener {
            val message = textEditText.text.toString()
            val languageName = languageSpinner.selectedItem.toString()
            val languageCode = languageMap[languageName] ?: "en" 

            Log.d("MainActivity", "Button clicked. Message: $message, Language: $languageCode")
            sendMessage(message, languageCode)
        }
    }

    private fun sendMessage(message: String, lc: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = simSimiApiService.getResponse(message, lc)
                withContext(Dispatchers.Main) {
                    if (response.isNotEmpty()) {
                        messageTextView.text = response
                    } else {
                        messageTextView.text = "No response from server"
                    }
                    Log.d("MainActivity", "Response: $response")
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    messageTextView.text = "Error: ${e.message}"
                }
                Log.e("MainActivity", "Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
