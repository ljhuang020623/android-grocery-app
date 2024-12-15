package com.example.groceryapp.ui.list

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.groceryapp.R
import com.example.groceryapp.data.FirebaseRepository
import com.example.groceryapp.data.models.Item
import com.example.groceryapp.databinding.FragmentItemDetailBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ItemDetailFragment : Fragment() {
    private var _binding: FragmentItemDetailBinding?=null
    private val binding get()=_binding!!
    private val repo = FirebaseRepository()
    private var token: String?=null
    private var nickname:String?=null

    private val itemId: String by lazy {
        arguments?.getString("itemId") ?: ""
    }

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
        _binding=FragmentItemDetailBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)


        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("list_token",null)
        nickname = prefs.getString("nickname",null)

        if(token==null||nickname==null){
            findNavController().navigate(R.id.homeFragment)
            return
        }

        // Load item details
        lifecycleScope.launch {
            try {
                val item = repo.getItem(token!!, itemId)
                if(item!=null){
                    displayItem(item)
                } else {
                    Toast.makeText(requireContext(),"Item not found",Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            } catch(e:Exception){
                Toast.makeText(requireContext(),"Error: ${e.message}",Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.editBtn.setOnClickListener {
            val bundle = Bundle().apply { putString("itemId", itemId) }
            findNavController().navigate(R.id.editItemFragment, bundle)
        }

        binding.removeBtn.setOnClickListener {
            lifecycleScope.launch {
                try {
                    repo.removeItem(token!!, itemId)
                    Toast.makeText(requireContext(),"Item removed",Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } catch(e:Exception){
                    Toast.makeText(requireContext(),"${getString(R.string.error_saving)} ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.homeLogoutBtn.setOnClickListener {
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.homeFragment)
        }
    }


    private fun displayItem(item: Item){
        binding.itemNameTv.text = item.name
        binding.quantityTv.text = "Quantity: ${item.quantity}"
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
        binding.buyBeforeTv.text = "Buy Before: ${item.buyBefore?.toDate()?.let { sdf.format(it) }?:"N/A"}"
        binding.priceTv.text = "Price: $${item.price}"
        binding.lastModifiedByTv.text = "Last modified by: ${item.lastModifiedBy}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}

