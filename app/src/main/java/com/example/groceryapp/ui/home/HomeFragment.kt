package com.example.groceryapp.ui.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.groceryapp.R
import com.example.groceryapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get()=_binding!!

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        binding.createListBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createListFragment)
        }
        binding.joinListBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_joinListFragment)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
