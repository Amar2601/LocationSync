package com.example.firstapplication

import android.util.Log
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
class LoginViewModel @Inject constructor(
    var repository: Repository
) : ViewModel() {

    var driverloginResponse = MutableLiveData<NetworkResult<LoginResponse>>()


    data class DriverloginModel(
        @SerializedName("vehicle_number") var vehiclenumber: String? = null,
    )

    fun userLogin(token : String, vehiclenumber: String?)
    {
        viewModelScope.launch(Dispatchers.IO + repository.getExceptionHandler(driverloginResponse)) {

            driverloginResponse.postValue(NetworkResult.Loading())
            val response = repository.driverLogin(token,DriverloginModel(vehiclenumber))
            handleResponse(response, driverloginResponse)
        }
    }
}