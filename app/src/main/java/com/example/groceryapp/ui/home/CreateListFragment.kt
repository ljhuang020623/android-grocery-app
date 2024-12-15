package com.example.groceryapp.ui.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.groceryapp.R
import com.example.groceryapp.data.FirebaseRepository
import com.example.groceryapp.databinding.FragmentCreateListBinding
import kotlinx.coroutines.launch

class CreateListFragment: Fragment() {
    private var _binding: FragmentCreateListBinding? = null
    private val binding get()=_binding!!
    private val repo = FirebaseRepository()

    override fun onCreateView(inflater: LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
        _binding=FragmentCreateListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        binding.saveBtn.setOnClickListener {
            val listName = binding.listNameEt.text.toString().trim()
            if(listName.isEmpty()){
                Toast.makeText(requireContext(),"Please enter a list name",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the list
            lifecycleScope.launch {
                try {
                    val token = repo.createList(listName)
                    val nickname = "Owner"
                    val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit()
                        .putString("list_token", token)
                        .putString("nickname", nickname)
                        .apply()

                    Toast.makeText(requireContext(), "Your token: $token", Toast.LENGTH_LONG).show()

                    findNavController().navigate(R.id.homeFragment)
                    AlertDialog.Builder(requireContext())
                        .setTitle("List Token")
                        .setMessage("Your token is: $token\nCopy and save it to join later.")
                        .setPositiveButton("Copy") { _, _ ->
                            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("List Token", token)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(requireContext(), "Token copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("Ok") { _, _ ->
                            findNavController().navigate(R.id.homeFragment)
                        }
                        .show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), getString(R.string.error_saving) + " " + e.message, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding=null
    }
}
