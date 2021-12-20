package com.example.toptendownloaderapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class FeedEntry {
    var nameFeed: String = ""

    override fun toString(): String {
        return """
            name = $nameFeed
           """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {
    private val TAGMainActivity = "MainActivity"
    lateinit var tvfeed : TextView
    lateinit var rvFeed :RecyclerView
    lateinit var feedBtn:Button
    lateinit var itemsList:ArrayList<String>
    val feed_URL = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAGMainActivity, "onCreate called")
        tvfeed = findViewById<TextView>(R.id.tvFeed)
        feedBtn = findViewById(R.id.fetchBtn)
        feedBtn.setOnClickListener{
            requestApi(feed_URL)
            initRecyclerView()
        }
        Log.d(TAGMainActivity, "onCreate: done")
    }

    // initial RecyclerView
    private fun initRecyclerView() {
        rvFeed = findViewById(R.id.rvFeed)
        rvFeed.layoutManager = LinearLayoutManager(this)
        rvFeed.setHasFixedSize(true)
    }
    //XML download
    private fun downloadXML(urlPath: String?): String {
        val xmlResult = StringBuilder()
        try {
            val url = URL(urlPath)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            val response = connection.responseCode
            Log.d(TAGMainActivity, "downloadXML: The response code was $response")

            val reader = BufferedReader(InputStreamReader(connection.inputStream))

            val inputBuffer = CharArray(500)
            var charsRead = 0
            while (charsRead >= 0) {
                charsRead = reader.read(inputBuffer)
                if (charsRead > 0) {
                    xmlResult.append(String(inputBuffer, 0, charsRead))
                }
            }
            reader.close()
            Log.d(TAGMainActivity, "Received ${xmlResult.length} bytes")
            return xmlResult.toString()

        } catch (e: MalformedURLException) {
            Log.e(TAGMainActivity, "downloadXML: Invalid URL ${e.message}")
        } catch (e: IOException) {
            Log.e(TAGMainActivity, "downloadXML: IO Exception reading data: ${e.message}")
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e(TAGMainActivity, "downloadXML: Security exception.  Needs permissions? ${e.message}")
        } catch (e: Exception) {
            Log.e(TAGMainActivity, "Unknown error: ${e.message}")
        }
        return ""
    }
    //API request
    private fun requestApi(url:String){
        var listItems = ArrayList<FeedEntry>()

        CoroutineScope(Dispatchers.IO).launch {
            val rssFeed = async {
                downloadXML(url)
            }.await()

            if (rssFeed.isEmpty()) {
                Log.e(TAGMainActivity, "requestApi fun: Error downloading")
            } else {
                val parseApplications = async {
                    FeedParser()
                }.await()

                parseApplications.parse(rssFeed)
                listItems = parseApplications.getParsedList()
                withContext(Dispatchers.Main) {
                    rvFeed.adapter = FeedAdapter(listItems)
                }
            }
        }
    }
}