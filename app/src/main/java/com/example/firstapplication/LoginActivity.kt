package com.example.firstapplication

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

import com.example.firstapplication.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var viewModel:LoginViewModel
    private var progressDialog: Dialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        var accesstoken=this.getPrefeb("drivertoken")

        if (accesstoken.isNotEmpty())
        {
            var intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        progressDialog = this.getProgressDialog()

        binding.LoginButton.setOnClickListener {

            var vechilenumber= binding.vechilenumber.text.toString().trim()

            if (vechilenumber.isEmpty())
            {
                binding.vechilenumber.error="Please enter your Vehicle Number"
                binding.vechilenumber.requestFocus()
            }
            else
            {
                viewModel.userLogin(AppConst.API_TOKEN,vechilenumber)
            }


        }

        viewModel.driverloginResponse.observe(this) {

            when (it) {

                is NetworkResult.Loading -> {

                    progressDialog?.show()

                }

                is NetworkResult.Success -> {

                    progressDialog?.dismiss()

                    var response= it.data

                    var drivertoken=response?.token
                    var driverid=response?.data?.id
                    var schoolid=response?.data?.schoolId

                    this.putPrefeb("drivertoken",drivertoken.toString())
                    this.putPrefeb("driverid",driverid.toString())
                    this.putPrefeb("schoolid",schoolid.toString())


                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)

                }

                is NetworkResult.Error -> {

                    progressDialog?.show()

                    Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                }

                else -> {
                }
            }
        }


    }
}