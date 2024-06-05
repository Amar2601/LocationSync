package com.example.firstapplication

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("error"   ) var error   : Boolean? = null,
    @SerializedName("message" ) var message : String?  = null,
    @SerializedName("data"    ) var data    : Data?    = Data(),
    @SerializedName("code"    ) var code    : Int?     = null,
    @SerializedName("token"   ) var token   : String?  = null
)
