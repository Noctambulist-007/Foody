package com.noctambulist.foody.fragments.foody

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noctambulist.foody.R
import com.noctambulist.foody.adapters.ViewPagerImageAdapter
import com.noctambulist.foody.data.CartProduct
import com.noctambulist.foody.databinding.FragmentProductDetailsBinding
import com.noctambulist.foody.util.Resource
import com.noctambulist.foody.util.hideBottomNavigationView
import com.noctambulist.foody.viewmodel.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {

    companion object {
        private const val IS_ITEM_ADDED_KEY = "is_item_added_key"
    }

    private val args by navArgs<ProductDetailsFragmentArgs>()
    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerImageAdapter by lazy { ViewPagerImageAdapter() }
    private val viewModel by viewModels<DetailsViewModel>()
    private var isItemAddedToCart = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product
        setupViewPager()

        val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
        actionBar?.hide()

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requireActivity().window.statusBarColor = Color.BLACK

        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(android.R.transition.move)


        binding.imageClose.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if (savedInstanceState != null) {
            isItemAddedToCart = savedInstanceState.getBoolean(IS_ITEM_ADDED_KEY, false)
            updateButtonAppearance()
        }

        binding.buttonAddToCart.setOnClickListener {
            if (!isItemAddedToCart) {
                binding.buttonAddToCart.startAnimation()

                viewModel.addUpdateProductInCart(CartProduct(product, 1))
                isItemAddedToCart = true
            } else {
                findNavController().navigate(R.id.action_productDetailsFragment_to_cartFragment)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.checkIfProductInCart(product.id) { inCart ->
                isItemAddedToCart = inCart
                updateButtonAppearance()
            }

            viewModel.addToCart.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonAddToCart.revertAnimation()
                        Toast.makeText(requireContext(), "Foody added", Toast.LENGTH_SHORT).show()
                        updateButtonAppearance()
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.buttonAddToCart.revertAnimation()
                        updateButtonAppearance()
                    }

                    else -> Unit
                }
            }
        }

        binding.apply {
            tvProdcutName.text = product.name
            headerTitle.text = product.name
            tvProdcutPrice.text = "TK. ${product.offerPercentage}"
            val typewriterDelay = 50L
            product.description?.let {
                applyTypewriterEffect(
                    tvProdcutDescription,
                    it, typewriterDelay
                )
            }
        }
        viewPagerImageAdapter.differ.submitList(product.images)
    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImage.adapter = viewPagerImageAdapter
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

    private fun updateButtonAppearance() {
        if (isItemAddedToCart) {
            binding.buttonAddToCart.text = "Go To Cart"
            binding.buttonAddToCart.setBackgroundResource(R.drawable.black_button)
        } else {
            binding.buttonAddToCart.text = "Add to Cart"
            binding.buttonAddToCart.setBackgroundResource(R.drawable.green_button)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_ITEM_ADDED_KEY, isItemAddedToCart)
    }
}
