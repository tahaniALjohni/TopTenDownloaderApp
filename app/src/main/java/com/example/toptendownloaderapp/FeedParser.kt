package com.example.toptendownloaderapp

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import kotlin.collections.ArrayList


class FeedParser {
    private val TAGFeedParser = "FeedParser"
    private val applications = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        Log.d(TAGFeedParser, "parse called with $xmlData")
        var status = true
        var inEntry = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {

                val tagName = xpp.name?.toLowerCase()
                Log.d(TAGFeedParser, "Parse: tag for " + tagName)
                when (eventType) {

                    XmlPullParser.START_TAG -> {
                        Log.d(TAGFeedParser, "Parse: Starting tag for " + tagName)
                        if (tagName == "entry") {
                            inEntry = true
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG -> {
                        Log.d(TAGFeedParser, "Parse: Ending tag for " + tagName)
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()   // create a new object
                                }
                                "name" -> currentRecord.nameFeed = textValue
                            }
                        }
                    }
                }
                eventType = xpp.next()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        return status
    }

    fun getParsedList(): ArrayList<FeedEntry> {
        return applications
    }
}