package com.example.firstapplication

import android.Manifest
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
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.firstapplication.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private lateinit var viewModel: MainViewModel
    private val binding: ActivityMainBinding
        get() = _binding!!

    private var service: Intent? = null
    var buttonclick=false

    private var backgroundlocationPermission = false

    private lateinit var backgroundLocation: ActivityResultLauncher<String>

    private val newLocationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.example.firstapplication.NEW_LOCATION") {
                val latitude = intent.getDoubleExtra("latitude", 0.0)
                val longitude = intent.getDoubleExtra("longitude", 0.0)

                this@MainActivity.putPrefeb("Latitude", latitude.toString())
                this@MainActivity.putPrefeb("Lontitude", longitude.toString())

                binding.latitude.text = "Latitude : $latitude"
                binding.longitude.text = "Lontitude : $longitude"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    checkBackgroundLocationPermission()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    checkBackgroundLocationPermission()
                }
                else -> {

                    showPermissionDeniedDialog()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var latitude = this.getPrefeb("Latitude")
        var lotitude = this.getPrefeb("Lontitude")

        if (lotitude.isNotEmpty() && latitude.isNotEmpty()) {
            binding.latitude.visibility = View.VISIBLE
            binding.longitude.visibility = View.VISIBLE
            binding.latitude.text = "Latitude : $latitude"
            binding.longitude.text = "Lontitude : $lotitude"
        }

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        service = Intent(this, LocationService::class.java)

        backgroundLocation =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                backgroundlocationPermission = granted
            }

        binding.logout.setOnClickListener {
            buttonclick=false
            val preferences = this
                .getSharedPreferences("amar_${this?.packageName}", Context.MODE_PRIVATE)
            var editor = preferences.edit()
            editor.clear()
            editor.apply()

            stopService(service)

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val filter = IntentFilter("com.example.firstapplication.NEW_LOCATION")
        registerReceiver(newLocationReceiver, filter)

        binding.Activebutton.setOnClickListener {
            buttonclick=true
            binding.latitude.visibility = View.VISIBLE
            binding.longitude.visibility = View.VISIBLE

            checkInternetConnection()

            if (backgroundlocationPermission) {
                startLocationService()
            } else {
                checkPermissions()
            }
        }

        binding.CLEARBUTTON.setOnClickListener {
            buttonclick=false
            binding.latitude.visibility = View.GONE
            binding.longitude.visibility = View.GONE

            stopService(service)
        }
    }

    override fun onResume() {
        super.onResume()
        if (buttonclick)
        {
            if (backgroundlocationPermission)
            {
                startLocationService()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent) {
        // Update UI with location event data if needed
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun checkInternetConnection() {
        if (isOnline(this)) {
            requestNotificationPermission()
        } else {
            showAlertDialogBox()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun showAlertDialogBox() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setCancelable(false)
            .setPositiveButton("Retry") { dialog, _ ->
                checkInternetConnection()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun requestNotificationPermission() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.areNotificationsEnabled()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alert")
                .setMessage("Please enable notification permission to see location updates in notifications.")
                .setPositiveButton("Go To Settings") { dialog, _ ->
                    val intent = Intent()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    } else {
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(false)
                .create()
                .show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            checkBackgroundLocationPermission()
        }
    }

    private fun checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                backgroundLocation.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            } else {
                backgroundlocationPermission = true
                startLocationService()
            }
        } else {
            backgroundlocationPermission = true
            startLocationService()
        }
    }

    private fun startLocationService() {
        if (!isServiceRunning(this, LocationService::class.java)) {
            startService(service)
        } else {
            Log.e("ServiceisAlreadyRunning", "Service is Already Running")
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun showPermissionDeniedDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Permission Required")
            .setMessage("This app needs location permission to provide the required functionality. Please grant the permission.")
            .setPositiveButton("Grant") { dialog, _ ->
                checkPermissions()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this,"Location Permission Decline",Toast.LENGTH_SHORT).show()
            }
            .setCancelable(false)
            .create()
            .show()
    }
}
