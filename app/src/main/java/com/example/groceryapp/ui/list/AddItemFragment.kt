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
import com.example.groceryapp.databinding.FragmentAddItemBinding
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.*

class AddItemFragment: Fragment() {
    private var _binding: FragmentAddItemBinding?=null
    private val binding get()=_binding!!
    private val repo = FirebaseRepository()
    private var buyBeforeTimestamp: Timestamp? = null
    private var token: String?=null
    private var nickname:String?=null

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
        _binding=FragmentAddItemBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        val restoredText = savedInstanceState?.getString("currentText")
        if (restoredText != null) {
            binding.itemNameEt.setText(restoredText)
        }


        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("list_token",null)
        nickname = prefs.getString("nickname",null)

        if(token==null || nickname==null){
            findNavController().navigate(R.id.homeFragment)
            return
        }

        // DatePicker
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

            val price = priceStr.toDoubleOrNull() ?: 0.0

            val newItem = Item(
                name = name,
                quantity = quantity,
                buyBefore = buyBeforeTimestamp,
                price = price,
                lastModifiedBy = nickname!!
            )

            lifecycleScope.launch {
                try {
                    repo.addItem(token!!,newItem)
                    Toast.makeText(requireContext(),"Item added",Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                } catch(e:Exception){
                    Toast.makeText(requireContext(), "${getString(R.string.error_saving)} ${e.message}",Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.homeLogoutBtn.setOnClickListener {
            prefs.edit().clear().apply()
            findNavController().navigate(R.id.homeFragment)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("currentText", binding.itemNameEt.text.toString())
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}
