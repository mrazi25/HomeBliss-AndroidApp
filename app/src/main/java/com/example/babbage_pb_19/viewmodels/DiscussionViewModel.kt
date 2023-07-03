package com.example.babbage_pb_19.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.babbage_pb_19.datas.Discussion
import com.example.babbage_pb_19.repositories.DiscussionRepository

class DiscussionViewModel : ViewModel() {
    private val repository : DiscussionRepository = DiscussionRepository().getInstance()
    private val _allDiscussions = MutableLiveData<List<Discussion>>()
    val allDiscussion : LiveData<List<Discussion>> = _allDiscussions
    var postid: String = ""
    fun loadDiscussions(postId: String) {
        repository.loadDiscussions(postId, allDiscussion as MutableLiveData<List<Discussion>>)
    }
    init {
        repository.loadDiscussions(postid, allDiscussion as MutableLiveData<List<Discussion>>)

    }
}