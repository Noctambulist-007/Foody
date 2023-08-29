package com.noctambulist.foody.fragments.others

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noctambulist.foody.R
import com.noctambulist.foody.adapters.BillingAdapter
import com.noctambulist.foody.data.OrderStatus
import com.noctambulist.foody.data.getOrderStatus
import com.noctambulist.foody.databinding.FragmentOrderDetailBinding
import com.noctambulist.foody.fragments.others.OrderDetailFragmentArgs
import com.noctambulist.foody.util.ItemDecorationHorizontal

class OrderDetailFragment : Fragment() {

    private lateinit var binding: FragmentOrderDetailBinding
    private val billingAdapter by lazy { BillingAdapter() }
    private val args by navArgs<OrderDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigation?.visibility = View.INVISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order
        setupOrderRv()

        binding.lottieOrderDetails.repeatCount = LottieDrawable.INFINITE
        binding.lottieOrderDetails.playAnimation()

        binding.imageCloseOrder.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.apply {
            tvOrderId.text = "Order #${order.orderId}"
            stepView.setSteps(
                mutableListOf(
                    OrderStatus.Ordered.status,
                    OrderStatus.Confirmed.status,
                    OrderStatus.Shipped.status,
                    OrderStatus.Delivered.status,
                )
            )
            val currentOrderState = when (getOrderStatus(order.orderStatus)) {
                is OrderStatus.Ordered -> 0
                is OrderStatus.Confirmed -> 1
                is OrderStatus.Shipped -> 2
                is OrderStatus.Delivered -> 3
                else -> 0
            }
            stepView.go(currentOrderState, false)
            if (currentOrderState == 3) {
                stepView.done(true)
            }
            tvFullName.text = order.address?.fullName
            tvAddress.text = "${order.address?.area} ${order.address?.city}"
            tvPhoneNumber.text = order.address?.phone
            tvTotalPrice.text = "TK. ${order.totalPrice}"
        }
        billingAdapter.differ.submitList(order.product)
    }

    private fun setupOrderRv() {
        binding.rvProducts.apply {
            adapter = billingAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            addItemDecoration(ItemDecorationHorizontal())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.lottieOrderDetails.cancelAnimation()
    }
}
