package com.example.firstapplication

import com.google.gson.annotations.SerializedName

data class LocationData(
    @SerializedName("driver_id" ) var userId : Int? = null,
    @SerializedName("latitude"     ) var lat    : Double? = null,
    @SerializedName("longitude"    ) var long   : Double? = null,
    @SerializedName("address"    ) var address   : String? = null,
    @SerializedName("school_id"    ) var schoolid   : Int? = null

)
