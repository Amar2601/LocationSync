package com.example.firstapplication
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject


interface Repository {

    fun <T> getExceptionHandler(responseList: MutableLiveData<NetworkResult<T>>? = null): CoroutineExceptionHandler

    suspend fun updateLocation(reqModel: Any): Response<ResponseBody>


}