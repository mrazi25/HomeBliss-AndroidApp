package com.example.babbage_pb_19.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.databinding.ActivityMainBinding
import com.example.babbage_pb_19.fragments.AddFragment
import com.example.babbage_pb_19.fragments.FavoriteFragment
import com.example.babbage_pb_19.fragments.HomeFragment
import com.example.babbage_pb_19.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    internal var selectedFragment: Fragment? = null

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.homeFragment -> {
                moveToFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.addFragment -> {
                moveToFragment(AddFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.favoritesFragment -> {
                moveToFragment(FavoriteFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.profileFragment -> {
                moveToFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val intent = Intent()
        //intent.putExtra("postid", value)

        moveToFragment(HomeFragment())
    }

    private fun moveToFragment(fragment: Fragment)
    {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }
}