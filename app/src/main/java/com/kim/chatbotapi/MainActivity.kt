package com.kim.chatbotapi

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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


    private val choices = arrayOf("Pirate", "Yoda", "Minion")
    private val languageMap = mapOf(
        "Yoda" to "yoda",
        "Pirate" to "pirate",
        "Minion" to "minion",
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

        languageSpinner = findViewById(R.id.language_spinner)
        requestButton = findViewById(R.id.request_button)
        val btnQuoteMaker = findViewById<Button>(R.id.btnQuoteMaker)
        val btnImage = findViewById<Button>(R.id.btnGetImage)
        val btnTranslate = findViewById<Button>(R.id.request_button)

        simSimiApiService = SimSimiApiService()

        // Setup Spinner
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, choices)
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

        btnImage.setOnClickListener{
            getImage()
        }

        btnTranslate.setOnClickListener {
            val choice = languageSpinner.selectedItem.toString()
            translateYoda(choice)
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
        val Text = findViewById<TextView>(R.id.txtQuote)
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
    private fun getImage(){
        var image: Bitmap? = null
        val imgQuote : ImageView = findViewById(R.id.imgQuote)
        val handler = Handler(Looper.getMainLooper())
        val executor = Executors.newSingleThreadExecutor()

        executor.execute{
            val imageURL = "https://zenquotes.io/api/image"
            try
            {
                val `in` = java.net.URL(imageURL).openStream()
                image = BitmapFactory.decodeStream(`in`)
                Log.d("Welcome", "Image added "+ image.toString())
                handler.post{
                    Log.d("Welcome", "Image added")
                    imgQuote.setImageBitmap(image)
                }
            }
            catch (e:java.lang.Exception)
            {
                Log.d("Welcome", "Error occurred: $e")
                e.printStackTrace()
            }
        }
    }
    private fun translateYoda(choice: String){
        var Text = findViewById<TextView>(R.id.txtQuote)
        val executor = Executors.newSingleThreadExecutor()
        var htmlString = ""
        var quote = Text.text.toString().split(" -").toTypedArray()
        val sentence = quote[0].split(" ").toTypedArray()
        for (i in sentence.indices){
            if(i == 0){
                htmlString += "${sentence[i]}"
            }
            else{
                htmlString = htmlString + "%20" + sentence[i]
            }
        }
        executor.execute {
            try {
                val url = URL("https://api.funtranslations.com/translate/${choice}.json?text=${htmlString}")
                val json = url.readText()
                Log.d("Test", url.toString())
                if(json.equals("null")){
                    Handler(Looper.getMainLooper()).post{
                        Text.setText("Member not found")
                    }
                }
                else{
                    val quote = Gson().fromJson(json, Response::class.java)

                    Handler(Looper.getMainLooper()).post {
                        Text.setText(quote.contents.translated + " - " + quote.contents.translation)
                    }
                }
            } catch (e: Exception) {
                Log.d("AddNewUser", "Error: " + e.toString())
                Text.setText("Error: " + e.toString())
            }
        }
    }
}
