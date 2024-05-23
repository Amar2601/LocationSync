package com.example.firstapplication

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
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

fun isOnline(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}

 fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

internal inline fun <reified T : Activity> Context.startActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    block(intent)
    startActivity(intent)
}

