package com.example.babbage_pb_19.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.babbage_pb_19.R
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed({
            var intent : Intent
            if (FirebaseAuth.getInstance().currentUser != null) {
                intent = Intent(this, MainActivity::class.java)
            } else{
                intent = Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
        }, 2500)
    }
}