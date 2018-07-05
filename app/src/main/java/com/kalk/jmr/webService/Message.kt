package com.kalk.jmr.webService

data class Message(val token: String, val uris:List<String> = listOf(), val genre:String ="") {
}