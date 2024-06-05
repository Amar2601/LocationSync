package com.example.firstapplication

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServices {
    companion object {

        const val BASE_URL = "https://campuslink.synchsoft.com/api/"
        const val UPDATE_LOCATION = "driver/save-driver-location"
        const val DRIVER_LOGIN = "driver/login"

    }
    @POST(UPDATE_LOCATION)
    suspend fun updatelocation(
        @Header("Authorization") headerToken: String,
        @Body body: Any
    ): Response<ResponseBody>

    @POST(DRIVER_LOGIN)
    suspend fun driverLogin(
        @Header("Authorization") headerToken: String,
        @Body body: Any
    ): Response<LoginResponse>

}