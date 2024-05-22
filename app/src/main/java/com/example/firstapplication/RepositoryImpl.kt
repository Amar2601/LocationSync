package com.example.firstapplication
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineExceptionHandler
import okhttp3.ResponseBody
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject


class RepositoryImpl @Inject constructor(var apiServices: ApiServices)
    : Repository {

    override fun <T> getExceptionHandler(responseList: MutableLiveData<NetworkResult<T>>?): CoroutineExceptionHandler {
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

    override suspend fun updateLocation(reqModel: Any): Response<ResponseBody> {
        return apiServices.updatelocation(reqModel)
    }



}