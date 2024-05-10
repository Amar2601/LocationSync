package com.example.firstapplication

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.example.firstapplication.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.LoginButton.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}