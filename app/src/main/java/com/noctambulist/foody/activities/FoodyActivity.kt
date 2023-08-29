package com.noctambulist.foody.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noctambulist.foody.R
import com.noctambulist.foody.databinding.ActivityFoodyBinding
import com.noctambulist.foody.util.Resource
import com.noctambulist.foody.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class FoodyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFoodyBinding
    val viewModelCart by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFoodyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.foodyFragment)
        binding.bottomNavigation.setupWithNavController(navController)

        lifecycleScope.launchWhenStarted {
            viewModelCart.cartProducts.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        val count = it.data?.size ?: 0
                        val bottomNavigation =
                            findViewById<BottomNavigationView>(R.id.bottom_navigation)
                        bottomNavigation.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.text_color)
                        }
                    }

                    else -> {
                        Unit
                    }
                }
            }
        }
    }
}