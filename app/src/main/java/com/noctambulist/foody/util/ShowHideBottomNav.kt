package com.noctambulist.foody.util

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.noctambulist.foody.R
import com.noctambulist.foody.activities.FoodyActivity

fun Fragment.hideBottomNavigationView() {
    val bottomNavigationView = (activity as FoodyActivity)
        .findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavigationView() {
    val bottomNavigationView = (activity as FoodyActivity)
        .findViewById<BottomNavigationView>(R.id.bottom_navigation)
    bottomNavigationView.visibility = View.VISIBLE
}