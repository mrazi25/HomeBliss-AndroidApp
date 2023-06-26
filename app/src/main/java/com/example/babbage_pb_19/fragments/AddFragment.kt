package com.example.babbage_pb_19.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.babbage_pb_19.activity.AddActivity
import com.example.babbage_pb_19.databinding.FragmentAddBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AddFragment : Fragment() {

    private lateinit var _binding: FragmentAddBinding
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentAddBinding.inflate(inflater, container, false)

        binding.toAddAct.setOnClickListener {
            startActivity(Intent(context, AddActivity::class.java))
        }
        return binding.root
    }
}