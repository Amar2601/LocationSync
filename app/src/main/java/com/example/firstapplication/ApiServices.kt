package com.example.firstapplication

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ApiServices {
    companion object {

        const val BASE_URL = "https://jsonplaceholder.typicode.com/"
        const val GET_POST = "posts"

    }
    @GET(GET_POST)
    suspend fun getSkills(): Response<Root>
}