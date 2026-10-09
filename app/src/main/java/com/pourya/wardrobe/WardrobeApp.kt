package com.pourya.wardrobe

import android.app.Application
import android.content.Context
import com.pourya.wardrobe.utils.LocaleManager

class WardrobeApp : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager.onAttach(base))
    }
}
