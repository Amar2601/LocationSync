package com.example.firstapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var repository: Repository

)  : ViewModel(){

    var locationResponse = MutableLiveData<NetworkResult<ResponseBody>>()

    data class requestbody(
        @SerializedName("user_id" ) var userId : String? = null,
        @SerializedName("lat"     ) var lat    : String? = null,

        @SerializedName("long"    ) var long   : String? = null
    )
    fun updateLocation(userId: String?,lat: String?,long: String?){
        viewModelScope.launch(Dispatchers.IO+ repository.getExceptionHandler(locationResponse)) {
            locationResponse.postValue(NetworkResult.Loading())
            var requestbody=requestbody(userId,lat,long)
            val response = repository.updateLocation(requestbody)
            handleResponse(response,locationResponse)
        }
    }
}