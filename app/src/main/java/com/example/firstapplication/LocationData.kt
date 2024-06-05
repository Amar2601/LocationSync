package com.example.firstapplication

import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("driver_id" ) var userId : Int? = null,
    @SerializedName("latitude"     ) var lat    : String? = null,
    @SerializedName("longitude"    ) var long   : String? = null,
    @SerializedName("address"    ) var address   : String? = null,
    @SerializedName("school_id"    ) var schoolid   : Int? = null

)
