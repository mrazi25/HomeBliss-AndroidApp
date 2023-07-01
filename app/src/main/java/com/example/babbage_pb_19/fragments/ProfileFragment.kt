package com.example.babbage_pb_19.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.babbage_pb_19.R.drawable as DRAWABLE
import com.example.babbage_pb_19.activity.LoginActivity
import com.example.babbage_pb_19.activity.ProfileSettingActivity
import com.example.babbage_pb_19.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentProfileBinding.inflate(inflater, container, false)

        binding.editBtn.setOnClickListener {
            startActivity(Intent(context, ProfileSettingActivity::class.java))
        }
        binding.logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(context, LoginActivity::class.java))
        }
        userInfo()

        return binding.root
    }

    private fun userInfo() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            userRef.child(user.uid).get().addOnSuccessListener {
                if (it.exists()) {
                    _binding.etName.text = "Name: "+it.child("name").value.toString()
                    _binding.etEmail.text = "Email: "+it.child("email").value.toString()
                    Picasso.get()
                        .load(it.child("image").value.toString())
                        .placeholder(DRAWABLE.homebliss)
                        .error(DRAWABLE.homebliss)
                        .into(_binding.ivProfile)

                }
            }
        } else {

        }
    }

}