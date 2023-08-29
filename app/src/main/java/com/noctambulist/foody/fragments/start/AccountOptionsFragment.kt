package com.noctambulist.foody.fragments.start

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noctambulist.foody.R
import com.noctambulist.foody.databinding.FragmentAccountOptionsBinding

class AccountOptionsFragment : Fragment(R.layout.fragment_account_options) {

    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.hide()

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val typewriterDelay = 20L
        applyTypewriterEffect(binding.tvDescription, "Welcome\nTo Foody Land", typewriterDelay)

        binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment_to_loginFragment)
        }

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment_to_registerFragment)
        }
    }

    private fun applyTypewriterEffect(textView: TextView, text: String, delay: Long) {
        val charArray = text.toCharArray()
        var currentIndex = 0

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentIndex <= charArray.size) {
                    val displayText = charArray.slice(0 until currentIndex).joinToString("")
                    textView.text = displayText
                    currentIndex++
                    handler.postDelayed(this, delay)
                }
            }
        }, delay)
    }
}