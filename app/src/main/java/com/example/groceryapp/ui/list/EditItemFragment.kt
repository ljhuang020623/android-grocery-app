package com.example.groceryapp.ui.list

import android.app.DatePickerDialog
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
import com.example.groceryapp.databinding.FragmentEditItemBinding
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.*

class EditItemFragment: Fragment() {
    private var _binding: FragmentEditItemBinding?=null
    private val binding get()=_binding!!
    private val repo = FirebaseRepository()
    private var token: String?=null
    private var nickname:String?=null
    private var buyBeforeTimestamp: Timestamp? = null
    private var currentItem: Item? = null
    private val itemId: String by lazy {
        arguments?.getString("itemId") ?: ""
    }

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
        _binding=FragmentEditItemBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("list_token",null)
        nickname = prefs.getString("nickname",null)

        if(token==null || nickname==null){
            findNavController().navigate(R.id.homeFragment)
            return
        }

        // Load item from Firestore
        lifecycleScope.launch {
            try {
                val item = repo.getItem(token!!, itemId)
                if(item!=null){
                    currentItem = item
                    // Fill fields
                    binding.itemNameEt.setText(item.name)
                    binding.quantityEt.setText(item.quantity)
                    if(item.buyBefore!=null){
                        val cal=Calendar.getInstance()
                        cal.time=item.buyBefore.toDate()
                        binding.buyBeforeEt.setText("${cal.get(Calendar.YEAR)}/${cal.get(Calendar.MONTH)+1}/${cal.get(Calendar.DAY_OF_MONTH)}")
                        buyBeforeTimestamp=item.buyBefore
                    }
                    binding.priceEt.setText(item.price.toString())
                }
            } catch(e:Exception) {
                Toast.makeText(requireContext(),"Error loading item: ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }

        binding.buyBeforeEt.setOnClickListener {
            val c = Calendar.getInstance()
            val dpd = DatePickerDialog(requireContext(), {_, year, month, dayOfMonth->
                val cal = Calendar.getInstance()
                cal.set(year,month,dayOfMonth,0,0,0)
                buyBeforeTimestamp = Timestamp(cal.time)
                binding.buyBeforeEt.setText("$year/${month+1}/$dayOfMonth")
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
            dpd.show()
        }

        binding.saveBtn.setOnClickListener {
            val name = binding.itemNameEt.text.toString().trim()
            val quantity = binding.quantityEt.text.toString().trim()
            val priceStr = binding.priceEt.text.toString().trim()

            if(name.isEmpty()||quantity.isEmpty()||priceStr.isEmpty()){
                Toast.makeText(requireContext(),"Please fill all fields",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull()?:0.0
            val updatedItem = currentItem?.copy(
                name = name,
                quantity = quantity,
                buyBefore = buyBeforeTimestamp,
                price = price,
                lastModifiedBy = nickname!!
            )

            if(updatedItem!=null) {
                lifecycleScope.launch {
                    try {
                        repo.updateItem(token!!, updatedItem)
                        Toast.makeText(requireContext(),"Item updated",Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }catch(e:Exception){
                        Toast.makeText(requireContext(),"${getString(R.string.error_saving)} ${e.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.homeLogoutBtn.setOnClickListener {
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
