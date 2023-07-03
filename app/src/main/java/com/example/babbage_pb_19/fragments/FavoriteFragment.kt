package com.example.babbage_pb_19.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R.id as ID
import com.example.babbage_pb_19.adapters.LikeAdapter
import com.example.babbage_pb_19.viewmodels.LikeViewModel
import com.example.babbage_pb_19.viewmodels.PostViewModel
import com.example.babbage_pb_19.databinding.FragmentFavoriteBinding
import com.google.firebase.auth.FirebaseAuth

class FavoriteFragment : Fragment() {

    private lateinit var _binding: FragmentFavoriteBinding
    private val binding get() = _binding!!

    private lateinit var viewModel : PostViewModel
    private lateinit var viewModel2 : LikeViewModel
    private lateinit var postRecyclerView: RecyclerView
    lateinit var adapter: LikeAdapter
    private var user_id: String? = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postRecyclerView = view.findViewById(ID.recycler_view_fav)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        postRecyclerView.layoutManager = linearLayoutManager
        postRecyclerView.setHasFixedSize(true)
        adapter = LikeAdapter()
        postRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[PostViewModel::class.java]
        viewModel2 = ViewModelProvider(this)[LikeViewModel::class.java]

        viewModel.allPosts.observe(viewLifecycleOwner) {
            adapter.updatePostList(it)
        }

        viewModel2.loadLikes(user_id.toString())
        viewModel2.allLikes.observe(viewLifecycleOwner) {
            adapter.updateLikeList(it)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }
}