package com.example.firstapplication
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject

class Repository @Inject constructor(var apiServices: ApiServices) {

    fun <T> getExceptionHandler(responseList: MutableLiveData<NetworkResult<T>>? = null): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { cctx, throwable ->
            throwable.printStackTrace()
            when (throwable) {
                is UnknownHostException -> {
                    responseList!!.postValue(NetworkResult.Error("No Internet connection found"))
                }

                is ConnectException -> {
                    responseList!!.postValue(NetworkResult.Error("Connection failed, Please check internet connection and retry."))
                }

                else -> {

                    when {
                        throwable.localizedMessage?.toString()!!.contains("end of input") -> {
                            responseList!!.postValue(NetworkResult.Error("Server not reachable!!!"))
                        }

                        else -> {
                            responseList!!.postValue(NetworkResult.Error(throwable.localizedMessage))
                        }
                    }

                }
            }
        }
    }

    suspend fun getSkills(): Response<Root> {
        return apiServices.getSkills()
    }



}