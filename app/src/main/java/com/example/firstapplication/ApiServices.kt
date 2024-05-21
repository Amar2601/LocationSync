package com.example.firstapplication

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiServices {
    companion object {

        const val BASE_URL = "https://campuslink-location-api.synchsoft.in/"
        const val UPDATE_LOCATION = "api/V1/saved-driver-location"

    }
    @POST(UPDATE_LOCATION)
    suspend fun updatelocation(
        @Body body: Any
    ): Response<ResponseBody>
}