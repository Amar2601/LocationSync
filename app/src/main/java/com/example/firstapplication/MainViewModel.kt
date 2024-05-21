package com.example.firstapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private var repository: Repository

)  : ViewModel(){

    var getSkillsResponse = MutableLiveData<NetworkResult<Root>>()

    fun getSkills(){
        viewModelScope.launch(Dispatchers.IO+ repository.getExceptionHandler(getSkillsResponse)) {
            getSkillsResponse.postValue(NetworkResult.Loading())
            val response = repository.getSkills()
            handleResponse(response,getSkillsResponse)
        }
    }
}