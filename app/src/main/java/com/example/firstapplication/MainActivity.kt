package com.example.firstapplication

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.firstapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private lateinit var viewModel: MainViewModel
    var locationResponse = MutableLiveData<NetworkResult<ResponseBody>>()
    private val binding: ActivityMainBinding
        get() = _binding!!


    private var service: Intent?=null

    private val backgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {

            }
        }


    private val newLocationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.firstapplication.NEW_LOCATION") {
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)

                Toast.makeText(this@MainActivity,""+longitude.toString()+latitude.toString(),Toast.LENGTH_SHORT).show()


            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }

                }
                it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {

                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel= ViewModelProvider(this)[MainViewModel::class.java]

        requestNotificationPermission(this)

        service = Intent(this, LocationService::class.java)

                checkPermissions()

        val filter = IntentFilter("com.example.firstapplication.NEW_LOCATION")
        registerReceiver(newLocationReceiver, filter)


    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }


    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }else{
                startService(service)
            }
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        stopService(service)
//        if(EventBus.getDefault().isRegistered(this)){
//            EventBus.getDefault().unregister(this)
//        }
//    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent){
//        binding.tvLatitude.text = "Latitude -> ${locationEvent.latitude}"
//        binding.tvLongitude.text = "Longitude -> ${locationEvent.longitude}"
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestNotificationPermission(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.areNotificationsEnabled()) {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Alert ")
            builder.setMessage("Please Enable Notification Permission To See Location Update in Notification")
            builder.setPositiveButton("Go To Setting") { dialog, _ ->

                val intent = Intent()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                } else {
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->

                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()

        }
    }

}


















