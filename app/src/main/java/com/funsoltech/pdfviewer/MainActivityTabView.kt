package com.funsoltech.pdfviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivityTabView : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tab_view)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)

        val pagerAdapter = MyPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = "Tab ${position + 1}"
        }.attach()
    }
}

class MyPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Fragment1()
            1 -> Fragment2()
            2 -> Fragment3()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}

class Fragment1 : Fragment(R.layout.fragment_1) {
    // Customize Fragment1 as per your requirements
    // Add any necessary logic or UI components
    // You can define fragment-specific methods and variables here
}

class Fragment2 : Fragment(R.layout.fragment_2) {
    // Customize Fragment2 as per your requirements
    // Add any necessary logic or UI components
    // You can define fragment-specific methods and variables here
}

class Fragment3 : Fragment(R.layout.fragment_3) {
    // Customize Fragment3 as per your requirements
    // Add any necessary logic or UI components
    // You can define fragment-specific methods and variables here
}
