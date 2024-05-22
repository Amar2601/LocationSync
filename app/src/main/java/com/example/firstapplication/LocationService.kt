package com.example.firstapplication

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.IBinder

import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.scopes.ServiceScoped
import java.util.Locale


@ServiceScoped
class LocationService : Service() {

    companion object {
        const val CHANNEL_ID = "12345"
        const val NOTIFICATION_ID = 12345
        const val ACTION_RESTART_SERVICE = "com.example.firstapplication.RESTART_SERVICE"
    }

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private var notificationManager: NotificationManager? = null
    private var location: Location? = null
    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest =
            LocationRequest.create().setInterval(15000).setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { onNewLocation(it) }
            }
        }

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "Location updates",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        locationRequest?.let {
            locationCallback?.let { it1 ->
                fusedLocationProviderClient?.requestLocationUpdates(
                    it,
                    it1,
                    null
                )
            }
        }
    }

    private fun onNewLocation(newLocation: Location) {
        location = newLocation
        updateNotification()

        val data = Data.Builder()
            .putDouble("latitude", newLocation.latitude)
            .putDouble("longitude", newLocation.longitude)
            .build()

        // Create a WorkRequest to send the location data to the API
        val locationWorkRequest = OneTimeWorkRequestBuilder<LocationWorker>()
            .setInputData(data)
            .build()

        // Enqueue the WorkRequest
        WorkManager.getInstance(this@LocationService).enqueue(locationWorkRequest)

    }

    private fun updateNotification() {
        val notification = getNotification()
        startForeground(NOTIFICATION_ID, notification)

    }

    private fun getNotification(): Notification {
        val contentText = getAddressFromLocation(location!!.latitude, location!!.longitude)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(contentText)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Updates")
            .setContentText(contentText)
            .setStyle(bigTextStyle)
            .setSmallIcon(R.drawable.baseline_announcement_24)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        requestLocationUpdates()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()

        locationCallback?.let { fusedLocationProviderClient?.removeLocationUpdates(it) }
        restartService()
    }

    private fun restartService() {

        val restartServiceIntent = Intent(this, LocationService::class.java).apply {
            action = ACTION_RESTART_SERVICE
        }
        val restartServicePendingIntent = PendingIntent.getService(
            this,
            1,
            restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 60000,
            restartServicePendingIntent
        )
    }


    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        var addressText = ""
        var geocoder = Geocoder(this, Locale.getDefault())
        var address = geocoder.getFromLocation(latitude, longitude, 1)
        addressText = address?.get(0)?.getAddressLine(0) ?: "Address not found"
        return addressText
    }
}
