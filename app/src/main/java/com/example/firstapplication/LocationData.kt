package com.example.firstapplication

import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("user_id" ) var userId : String? = null,
    @SerializedName("lat"     ) var lat    : String? = null,
    @SerializedName("long"    ) var long   : String? = null
)
