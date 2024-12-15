package com.example.groceryapp.ui.list

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.groceryapp.R
import com.example.groceryapp.data.models.Item
import com.example.groceryapp.databinding.FragmentListHomeBinding
import com.example.groceryapp.viewmodel.ListViewModel

class ListHomeFragment: Fragment(), ItemAdapter.OnItemClickListener {

    private var _binding: FragmentListHomeBinding?=null
    private val binding get()=_binding!!
    private val viewModel: ListViewModel by viewModels()
    private lateinit var adapter: ItemAdapter
    private var token: String? = null
    private var nickname: String? = null

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?):View{
        _binding = FragmentListHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("list_token",null)
        nickname = prefs.getString("nickname",null)

        if(token == null || nickname == null){
            findNavController().navigate(R.id.homeFragment)
            return
        }

        // Pass nickname to the adapter
        adapter = ItemAdapter(this, nickname ?: "Unknown")
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        if (drawable != null) {
            divider.setDrawable(drawable)
        }
        binding.recyclerView.addItemDecoration(divider)

        // Observe items
        viewModel.loadItems(token!!)
        viewModel.items.observe(viewLifecycleOwner) { items ->
            val sortedItems = items.sortedByDescending { it.id }
            adapter.submitList(sortedItems)
            val total = items.sumOf { it.price }
            binding.totalTv.text = getString(R.string.total_cost) + "$" + total
        }

        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.addItemFragment)
        }

        binding.homeLogoutBtn.setOnClickListener {
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.homeFragment)
        }

    }

    override fun onItemClicked(item: Item) {
        item.id?.let { itemId ->
            val bundle = Bundle().apply { putString("itemId", itemId) }
            findNavController().navigate(R.id.itemDetailFragment, bundle)
        }
    }

    override fun onEditButtonClicked(item: Item) {
        item.id?.let { itemId ->
            val bundle = Bundle().apply { putString("itemId", itemId) }
            findNavController().navigate(R.id.editItemFragment, bundle)
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding=null
    }
}
