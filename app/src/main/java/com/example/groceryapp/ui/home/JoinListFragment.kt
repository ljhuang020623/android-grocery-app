package com.example.groceryapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.groceryapp.R
import com.example.groceryapp.data.FirebaseRepository
import com.example.groceryapp.databinding.FragmentJoinListBinding
import kotlinx.coroutines.launch

class JoinListFragment: Fragment() {
    private var _binding: FragmentJoinListBinding? = null
    private val binding get()=_binding!!


    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
        _binding=FragmentJoinListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        val repo = FirebaseRepository()
        super.onViewCreated(view,savedInstanceState)
        binding.joinBtn.setOnClickListener {
            val token = binding.tokenEt.text.toString().trim()
            val nickname = binding.nicknameEt.text.toString().trim()
            if(token.isEmpty()||nickname.isEmpty()){
                Toast.makeText(requireContext(),"Please fill all fields",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val valid= repo.isTokenValid(token)
                    if(!valid){
                        Toast.makeText(requireContext(),getString(com.example.groceryapp.R.string.invalid_token),Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val prefs = requireContext().getSharedPreferences("user_prefs",Context.MODE_PRIVATE)
                    prefs.edit().putString("list_token",token)
                        .putString("nickname",nickname).apply()

                    findNavController().navigate(R.id.listHomeFragment)

                }catch (e:Exception){
                    Toast.makeText(requireContext(),e.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding=null
    }
}
