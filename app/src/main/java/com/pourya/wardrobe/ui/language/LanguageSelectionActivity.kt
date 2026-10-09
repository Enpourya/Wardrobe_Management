package com.pourya.wardrobe.ui.language

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.pourya.wardrobe.R
import com.pourya.wardrobe.databinding.ActivityLanguageSelectionBinding
import com.pourya.wardrobe.ui.home.MainActivity
import com.pourya.wardrobe.utils.LocaleManager

class LanguageSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLanguageSelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // If language already selected, go to main
        if (LocaleManager.isLanguageSelected(this)) {
            goToMain()
            return
        }

        binding = ActivityLanguageSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        startAnimations()
    }

    private fun setupUI() {
        binding.btnFarsi.setOnClickListener {
            selectLanguage(LocaleManager.LANG_PERSIAN)
        }
        binding.btnEnglish.setOnClickListener {
            selectLanguage(LocaleManager.LANG_ENGLISH)
        }
    }

    private fun startAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.ivLogo.startAnimation(fadeIn)

        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.layoutButtons.startAnimation(slideUp)
    }

    private fun selectLanguage(lang: String) {
        LocaleManager.setLocale(this, lang)
        goToMain()
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
