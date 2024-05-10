package com.example.firstapplication

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.Writer
import java.util.Calendar

class MyLocationService : JobService() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private lateinit var locationCallback: LocationCallback
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    var NOTIFICATION_ID:Int = 0
    lateinit var channelId:String
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private var stringBuilder: StringBuilder? = null

    companion object {
        private var isServiceRunning = false

        fun isRunning(): Boolean {
            return isServiceRunning
        }
    }

    override fun onStartJob(p0: JobParameters?): Boolean {

        isServiceRunning =true

        channelId = "${System.currentTimeMillis()}"
        NOTIFICATION_ID= Integer.parseInt(channelId.substring(4))
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        createNotificationChannel()

        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        stringBuilder = StringBuilder()
        val cal= Calendar.getInstance()
        writeFileOnInternalStorage(this@MyLocationService,"location.txt","\n${cal.time}--> job started.")

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    location?.let {
                        wayLatitude = location.latitude
                        wayLongitude = location.longitude
                        stringBuilder!!.clear()

                        stringBuilder!!.append(wayLatitude)
                        stringBuilder!!.append("-")
                        stringBuilder!!.append(wayLongitude)
                        stringBuilder!!.append("\n\n")
                        Toast.makeText(
                            this@MyLocationService,
                            "" + stringBuilder.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        writeFileOnInternalStorage(this@MyLocationService,"location.txt","\n${cal.time}--> ${it.latitude}-${it.longitude}")
                    }
                }
            }
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }else{
            Toast.makeText(this,"Please allow location permission.",Toast.LENGTH_SHORT).show()
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        isServiceRunning = false
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT)
            .show()
        return false;
    }



    fun writeFileOnInternalStorage(mcoContext: Context, sFileName: String?, sBody: String?) {
        val dir = File(mcoContext.filesDir.canonicalPath, "myFile")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val gpxfile = sFileName?.let { File(dir, it) }
            appendStringToFile(sBody,gpxfile)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun appendStringToFile(appendContents: String?, file: File?) {
        var result = false
        try {
            if (file != null && file.canWrite()) {
                file.createNewFile() // ok if returns false, overwrite
                val out: Writer = BufferedWriter(FileWriter(file, true), 1024)
                out.write("\n"+appendContents)
                out.close()
                result = true
            }

        } catch (e: IOException) {
            //   Log.e(Constants.LOG_TAG, "Error appending string data to file " + e.getMessage(), e);
        }

       sendNotification(appendContents!!)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, "Test", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotification(loc:String) {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, getPendingIntentFlags())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = Notification.Builder(this, channelId)
        } else {
            builder = Notification.Builder(this)
        }

        builder.apply {
            setContentTitle("My Notification")
            setSmallIcon(R.drawable.baseline_announcement_24) // Replace with your proper icon
            setContentText(loc)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun getPendingIntentFlags(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }
    }



