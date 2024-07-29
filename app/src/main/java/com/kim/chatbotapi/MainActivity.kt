package com.kim.chatbotapi

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.util.concurrent.Executors

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
        val btnQuoteMaker = findViewById<Button>(R.id.btnQuoteMaker)

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

        btnQuoteMaker.setOnClickListener{
            getQuote()
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
    @SuppressLint("SetTextI18n")
    private fun getQuote(){
        val executor = Executors.newSingleThreadExecutor()
        var Text = findViewById<TextView>(R.id.txtQuote)
        executor.execute {
            try {
                val url = URL("https://zenquotes.io/api/random")
                val json = url.readText()
                if(json.equals("null")){
                    Handler(Looper.getMainLooper()).post{
                        Text.setText("Member not found")
                    }
                }
                else{
                    val quote = Gson().fromJson(json, Array<Quote>::class.java)

                    Handler(Looper.getMainLooper()).post {
                        Text.setText(quote[0].q + " - " + quote[0].a)
                    }
                }
            } catch (e: Exception) {
                Log.d("AddNewUser", "Error: " + e.toString())
                Text.setText("Error: " + e.toString())
            }
        }
    }
}
