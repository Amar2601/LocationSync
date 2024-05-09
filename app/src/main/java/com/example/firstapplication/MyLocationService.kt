package com.example.firstapplication

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Looper
import android.telecom.TelecomManager.EXTRA_LOCATION
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import java.io.*
import java.util.*


class MyLocationService : JobService() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var wayLatitude = 0.0
    private var wayLongitude = 0.0
    private var stringBuilder: StringBuilder? = null

    override fun onStartJob(p0: JobParameters?): Boolean {

        locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        stringBuilder = StringBuilder()
        val cal=Calendar.getInstance()
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
            mFusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }else{
            Toast.makeText(this,"Please allow location permission.",Toast.LENGTH_SHORT).show()
        }
        return true
    }

    fun writeFileOnInternalStorage(mcoContext: Context, sFileName: String?, sBody: String?) {
        val dir = File(mcoContext.filesDir.canonicalPath, "myFile")
        if (!dir.exists()) {
            dir.mkdir()
        }
        try {
            val gpxfile = File(dir, sFileName)
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

        showNotification(appendContents.toString())
    }

    private fun showNotification(loc: String){

        val channelId = "${System.currentTimeMillis()}"
        val NOTIFICATION_ID : Int = Integer.parseInt(channelId.substring(4))
        val targetIntent = Intent(this, MainActivity::class.java)
        val contentIntent =
            PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val builder = NotificationCompat.Builder(this,channelId)
            .setContentTitle("Location!") // title for notification
            .setContentText("your location $loc")
            .setSmallIcon(com.example.firstapplication.R.mipmap.ic_launcher_round)
            .setContentIntent(contentIntent)

        val mNotificationManagerCompat = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Location Group",
                NotificationManager.IMPORTANCE_HIGH
            )
            mNotificationManagerCompat.createNotificationChannel(channel)
            builder.setChannelId(channelId)
        }


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return
        }
        mNotificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT)
            .show()
        return false;
    }
}