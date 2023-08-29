package com.noctambulist.foody.fragments.others

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noctambulist.foody.R
import com.noctambulist.foody.adapters.AllOrdersAdapter
import com.noctambulist.foody.databinding.FragmentAllOrdersBinding
import com.noctambulist.foody.util.Resource
import com.noctambulist.foody.viewmodel.AllOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AllOrdersFragment : Fragment() {

    private lateinit var binding: FragmentAllOrdersBinding
    val viewModel by viewModels<AllOrdersViewModel>()
    val ordersAdapter by lazy { AllOrdersAdapter() }
    private lateinit var lottieLoading: LottieAnimationView
    private lateinit var lottieEmptyOrder: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllOrdersBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOrdersRv()

        lottieEmptyOrder = binding.emptyOrder
        lottieLoading = binding.lottieLoading
        lottieLoading.visibility = View.GONE

        lifecycleScope.launchWhenStarted {
            viewModel.allOrders.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }

                    is Resource.Success -> {
                        hideLoading()
                        ordersAdapter.differ.submitList(it.data)
                        if (it.data.isNullOrEmpty()) {
                            showEmptyOrder()
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        hideLoading()
                    }

                    else -> Unit
                }
            }
        }

        ordersAdapter.onClick = {
            val action =
                AllOrdersFragmentDirections.actionAllOrdersFragmentToOrderDetailFragment(it)
            findNavController().navigate(action)
        }

        binding.imageCloseOrders.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showLoading() {
        binding.progressbarAllOrders.visibility = View.GONE
        lottieLoading.visibility = View.VISIBLE
        lottieLoading.repeatCount = LottieDrawable.INFINITE
        lottieLoading.playAnimation()
    }

    private fun hideLoading() {
        binding.progressbarAllOrders.visibility = View.GONE
        lottieLoading.visibility = View.GONE
        lottieLoading.cancelAnimation()
    }

    private fun showEmptyOrder() {
        binding.apply {
            lottieEmptyOrder.visibility = View.VISIBLE
            tvEmptyOrders.visibility = View.VISIBLE
            lottieEmptyOrder.repeatCount = LottieDrawable.INFINITE
            lottieEmptyOrder.playAnimation()
        }
    }

    private fun setupOrdersRv() {
        binding.rvAllOrders.apply {
            adapter = ordersAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.emptyOrder.cancelAnimation()
    }
}