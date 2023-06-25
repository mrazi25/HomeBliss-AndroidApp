package com.example.babbage_pb_19.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class RegistActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regist)
        supportActionBar?.hide()

        // Initialize Firebase Auth
        auth = Firebase.auth

        val btn = findViewById<TextView>(R.id.alreadyHaveAccount)
        btn.setOnClickListener {
            startActivity(
                Intent(
                    this@RegistActivity,
                    LoginActivity::class.java
                )
            )
        }

        val btnRegist = findViewById<TextView>(R.id.btnRegister)
        btnRegist.setOnClickListener {
            performRegister()
        }


    }
    private fun saveUserInfo(name: String, email: String) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRefs: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val img = "https://firebasestorage.googleapis.com/v0/b/babbage-pb-19.appspot.com/o/Default%20Profile%20Pict%2Fdefaultpropict.png?alt=media&token=582130e9-6769-4fe7-842c-938d23c803d1"
        val user = User(name, email, img)

        userRefs.child(currentUserID).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User Data Saved", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun performRegister() {
        val name = findViewById<EditText>(R.id.inputUsername)
        val email = findViewById<EditText>(R.id.inputEmail)
        val pass = findViewById<EditText>(R.id.inputPassword)
        val passConfirm = findViewById<EditText>(R.id.inputConfirmPassword)

        if (email.text.isEmpty() || pass.text.isEmpty() || name.text.isEmpty()) {
            Toast.makeText(this, "Please fill all the credential", Toast.LENGTH_SHORT).show()
            return
        } else if (passConfirm.text.isEmpty()) {
            Toast.makeText(this, "Please confirm your password", Toast.LENGTH_SHORT).show()
            return
        }
        val nameToFirebase = name.text.toString()
        val emailToFirebase = email.text.toString()
        val passToFirebase = pass.text.toString()

        auth.createUserWithEmailAndPassword(emailToFirebase, passToFirebase)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        baseContext,
                        "Registration success.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    saveUserInfo(nameToFirebase, emailToFirebase)
                    val intent = Intent(this@RegistActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        baseContext,
                        "Registration failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    FirebaseAuth.getInstance().signOut()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error Occurred ${it.localizedMessage}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}