package com.example.firstapplication

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class LocationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val latitude = inputData.getDouble("latitude", 0.0).toString()
        val longitude = inputData.getDouble("longitude", 0.0).toString()

        Log.e("workmanager","workmanager")

        return withContext(Dispatchers.IO) {
            try {
                val response = repository.updateLocation(LocationData("1",latitude, longitude))
                if (response.isSuccessful) {
                    Log.d("LocationApi", "Location update successful")
                    Result.success()

                } else {
                    Log.d("LocationApi", "Location update failed with code: ${response.code()}")
                    Result.retry()
                }
            } catch (e: Exception) {

                Log.d("LocationApi", "Location update failed with code")
                Result.retry()
            }
        }
    }
}
