package com.example.babbage_pb_19.activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.R.id as ID
import com.example.babbage_pb_19.R.layout as LAYOUT
import com.example.babbage_pb_19.data.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val userList = mutableListOf<User>()
    private val GOOGLE_LOGIN_CODE = 909

    var google_signin_opt: GoogleSignInOptions? = null
    var m_google_signin_client: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(LAYOUT.activity_login)
        supportActionBar?.hide()

        getAllUsers()
        google_signin_opt = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        m_google_signin_client = GoogleSignIn.getClient(this, google_signin_opt)
        auth = Firebase.auth

        findViewById<TextView>(ID.textViewSignUp).setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    RegistActivity::class.java
                )
            )
        }

        findViewById<TextView>(ID.btnlogin).setOnClickListener {
            performLogin()
        }

        findViewById<TextView>(ID.btnGoogle).setOnClickListener {
            performLogInWithGoogle()
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
        val email = findViewById<EditText>(ID.inputEmail)
        val pass = findViewById<EditText>(ID.inputPassword)

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
    private fun performLogInWithGoogle() {
        val signInIntent = m_google_signin_client!!.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        auth = FirebaseAuth.getInstance()
        if (requestCode == GOOGLE_LOGIN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val me = auth.currentUser
                            var status = false
                            for (user in userList) {
                                if (user.email == me!!.email) {
                                    status = true
                                    break
                                }
                            }
                            if (!status) {
                                saveUserInfo("Fulan", me!!.email.toString())
                            }
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Sign in gagal
                            Toast.makeText(this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            } catch (e: ApiException) {
                // Menangani error yang terjadi saat sign in dengan Google
                Toast.makeText(this, "Google sign in failed: ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserInfo(name: String, email: String) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRefs: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val img = "https://firebasestorage.googleapis.com/v0/b/babbage-pb-19.appspot.com/o/Default%20Profile%20Pict%2Fpropict.jpeg?alt=media&token=d789a115-776b-4b26-a164-f3f197b1e9f2"
        val user = User(name, email, img)

        userRefs.child(currentUserID).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User Data Saved", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getAllUsers() {
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference.child("Users")

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let { userList.add(it) }
                }
                println(userList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan jika ada
            }
        })
    }
}