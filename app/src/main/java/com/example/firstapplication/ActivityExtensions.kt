package com.example.firstapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log

import android.view.View

import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData

import com.google.android.material.snackbar.Snackbar

import org.json.JSONObject
import retrofit2.Response


internal fun Activity.showSnackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
        anchorView = view.rootView
        show()
    }
}

fun <T> handleResponse(
    response: Response<T>? = null,
    responseList: MutableLiveData<NetworkResult<T>>? = null
) {
    try {
        if (response!!.code() == 200 || response.code() == 201) {
            val responseBody = response.body()!!
            responseList!!.postValue(NetworkResult.Success(responseBody))
            Log.e("response", "$responseBody")
        } else if (response.errorBody() != null) {
            val errorobj = JSONObject(response.errorBody()!!.charStream().readText())
            responseList!!.postValue(NetworkResult.Error(errorobj.getString("message")))

            val message = errorobj.getString("message")
            Log.e("errorresponse", "$errorobj")
            Log.e("errorrmessage", "$message")
        } else {
            responseList!!.postValue(NetworkResult.Error("Something Went Wrong"))
        }
    } catch (ex: Exception) {
        responseList!!.postValue(NetworkResult.Error(ex.message))
        ex.printStackTrace()
    }
}



internal fun Fragment.showSnackBar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
        anchorView = view.rootView
        show()
    }
}

internal fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

internal inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    block(intent)
    startActivity(intent)
}

