package com.example.babbage_pb_19.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.activities.AddActivity
import com.example.babbage_pb_19.adapters.PostAdapter
import com.example.babbage_pb_19.viewmodels.MyPostViewModel
import com.example.babbage_pb_19.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private lateinit var _binding: FragmentAddBinding
    private val binding get() = _binding!!

    private lateinit var viewModel : MyPostViewModel
    private lateinit var postRecyclerView: RecyclerView
    lateinit var adapter: PostAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postRecyclerView = view.findViewById(R.id.recycler_view_add)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        postRecyclerView.layoutManager = linearLayoutManager
        postRecyclerView.setHasFixedSize(true)
        adapter = PostAdapter()
        postRecyclerView.adapter = adapter
        viewModel = ViewModelProvider(this)[MyPostViewModel::class.java]

        viewModel.allPost.observe(viewLifecycleOwner) {
            adapter.updatePostList(it)
        }
    }
}