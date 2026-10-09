package com.pourya.wardrobe.ui.home

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.pourya.wardrobe.R
import com.pourya.wardrobe.databinding.ActivityMainBinding
import com.pourya.wardrobe.utils.LocaleManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNav.setupWithNavController(navController)

        // RTL for Persian
        if (LocaleManager.isPersian(this)) {
            window.decorView.layoutDirection = android.view.View.LAYOUT_DIRECTION_RTL
        }
    }
}
