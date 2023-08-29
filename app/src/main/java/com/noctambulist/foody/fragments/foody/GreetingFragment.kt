package com.noctambulist.foody.fragments.foody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noctambulist.foody.R
import com.noctambulist.foody.databinding.FragmentGreetingBinding

class GreetingFragment : Fragment() {

    private lateinit var binding: FragmentGreetingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGreetingBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lottieCongrats.visibility = View.VISIBLE
            lottieCongrats.repeatCount = LottieDrawable.INFINITE
            lottieCongrats.playAnimation()

            lottieCongratsBg.visibility = View.VISIBLE
            lottieCongratsBg.repeatCount = LottieDrawable.INFINITE
            lottieCongratsBg.playAnimation()
        }

        binding.buttonGoHome.setOnClickListener {
            findNavController().navigate(R.id.action_greetingFragment_to_homeFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(R.id.action_greetingFragment_to_homeFragment)
                }
            })
    }
}
