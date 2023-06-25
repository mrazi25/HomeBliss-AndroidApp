package com.example.babbage_pb_19.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.babbage_pb_19.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth

        val btn1 = findViewById<TextView>(R.id.textViewSignUp)
        btn1.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegistActivity::class.java
                )
            )
        }

        val btnLogin = findViewById<TextView>(R.id.btnlogin)
        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
        } else {
            FirebaseAuth.getInstance().signOut()
        }
    }

    private fun performLogin() {
        val email = findViewById<EditText>(R.id.inputEmail)
        val pass = findViewById<EditText>(R.id.inputPassword)

        if (email.text.isEmpty() || pass.text.isEmpty()) {
            Toast.makeText(this, "Please fill all the credential", Toast.LENGTH_SHORT).show()
            return
        }
        val emailForFirebase = email.text.toString()
        val passForFirebase = pass.text.toString()

        auth.signInWithEmailAndPassword(emailForFirebase, passForFirebase)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Login failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}