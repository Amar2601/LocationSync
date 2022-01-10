package com.example.firstapplication

import android.Manifest
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
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
//    override fun onBind(intent: Intent?): IBinder? {
//        throw UnsupportedOperationException("Not yet implemented")
//    }

    override fun onStartJob(p0: JobParameters?): Boolean {
//        Toast.makeText(this, "Print task started.", Toast.LENGTH_SHORT)
//            .show()
//        writeFileOnInternalStorage(this,"location.txt","hello2")

        locationRequest = LocationRequest.create()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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
//            Toast.makeText(this,"inside permission check",Toast.LENGTH_SHORT).show()
            mFusedLocationClient!!.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
//            mFusedLocationClient!!.lastLocation.addOnSuccessListener {
//                Toast.makeText(this,"outside permission check:${it.latitude}-${it.longitude}",Toast.LENGTH_SHORT).show()
//
//                writeFileOnInternalStorage(this,"location.txt","\n ${it.latitude}-${it.longitude}")
//            }
        }else{
            Toast.makeText(this,"outside permission check",Toast.LENGTH_SHORT).show()
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

    fun appendStringToFile(appendContents: String?, file: File?) {
//        var result = false
//        try {
//                if (file != null && file.canWrite()) {
//                    file.createNewFile() // ok if returns false, overwrite
//                    val out: Writer = BufferedWriter(FileWriter(file, true), 1024)
//                    out.write("\n"+appendContents)
//                    out.close()
//                    result = true
//                }
//
//        } catch (e: IOException) {
//            //   Log.e(Constants.LOG_TAG, "Error appending string data to file " + e.getMessage(), e);
//        }


        try {
            val fileOutputStream: FileOutputStream = openFileOutput(file!!.name, Context.MODE_PRIVATE)
            val outputWriter = OutputStreamWriter(fileOutputStream)
            outputWriter.write("\n"+appendContents)
            outputWriter.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT)
            .show()
        return false;
    }

//    override fun onCreate() {
//        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_SHORT)
//            .show()
//        super.onCreate()
//
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_SHORT)
//            .show()
//
//        locationRequest = LocationRequest.create()
//        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        locationRequest!!.interval = (10 * 1000).toLong() // 60 seconds
//        stringBuilder = StringBuilder()
//
//        locationRequest!!.fastestInterval = (5 * 1000).toLong()
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                for (location in locationResult.locations) {
//                    location?.let {
//                        wayLatitude = location.latitude
//                        wayLongitude = location.longitude
//                        stringBuilder!!.clear()
//
//                        stringBuilder!!.append(wayLatitude)
//                        stringBuilder!!.append("-")
//                        stringBuilder!!.append(wayLongitude)
//                        stringBuilder!!.append("\n\n")
//                        Toast.makeText(
//                            this@MyLocationService,
//                            "" + stringBuilder.toString(),
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            }
//        }
//
//            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            Toast.makeText(this,"inside permission check",Toast.LENGTH_SHORT).show()
//            mFusedLocationClient!!.requestLocationUpdates(
//                locationRequest,
//                locationCallback,
//                Looper.getMainLooper()
//            )
//        }else{
//            Toast.makeText(this,"outside permission check",Toast.LENGTH_SHORT).show()
//        }
//
//
//        return START_STICKY
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT)
//            .show()
//    }
}