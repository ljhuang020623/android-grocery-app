package com.example.groceryapp.ui.help

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.groceryapp.databinding.FragmentHelpBinding
import android.webkit.WebViewClient

class HelpFragment: Fragment() {
    private var _binding: FragmentHelpBinding?=null
    private val binding get()=_binding!!

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
        _binding=FragmentHelpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadUrl("https://support.google.com/android/#topic=7313011")
    }

    override fun onDestroyView(){
        super.onDestroyView()
        _binding=null
    }
}
