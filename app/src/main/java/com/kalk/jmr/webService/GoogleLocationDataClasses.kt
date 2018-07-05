package com.kalk.jmr.webService

import com.google.gson.annotations.SerializedName

data class AddressComponent(val long_name:String, val short_name:String, val types:List<String>)

data class GoogleLocationResult(
        @SerializedName("results")
        val results: List<GoogleLocation>,
        val status:String
)

data class GoogleLocation(
        @SerializedName("address_components")
        val addressComponent: List<AddressComponent>,
        @SerializedName("formatted_address")
        val formattedAddress: String,
        val geometry:Any
)


