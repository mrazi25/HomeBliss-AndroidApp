package com.example.babbage_pb_19.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.babbage_pb_19.datas.Post
import com.example.babbage_pb_19.repositories.PostRepository

class PostViewModel : ViewModel() {
    private val repository : PostRepository = PostRepository().getInstance()
    private val _allPosts = MutableLiveData<List<Post>>()
    val allPosts : LiveData<List<Post>> = _allPosts

    init {
        repository.loadPosts(_allPosts)
    }
}