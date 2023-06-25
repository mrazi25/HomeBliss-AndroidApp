package com.example.babbage_pb_19.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.babbage_pb_19.repository.PostRepository

class PostViewModel : ViewModel() {
    private val repository : PostRepository
    private val _allUsers = MutableLiveData<List<Post>>()
    val allUsers : LiveData<List<Post>> = _allUsers


    init {

        repository = PostRepository().getInstance()
        println("Test Post ViewModel1")
        repository.loadUsers(_allUsers)
        println("Test Post ViewModel2")

    }
}